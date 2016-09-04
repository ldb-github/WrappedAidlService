package com.ldb.android.example.wrappedaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.ldb.android.example.wrappedaidl.aidl.CustomData;
import com.ldb.android.example.wrappedaidl.aidl.ResultCount;
import com.ldb.android.example.wrappedaidl.api.MessageApi;
import com.ldb.android.example.wrappedaidl.util.Util;

import java.util.Arrays;

/**
 * Created by lsp on 2016/9/2.
 */
public class ApiWrapperByMessenger {

    private static final String TAG = "ApiWrapperByMessenger";

    private Context mContext;
    private ApiCallback mCallback;
    private MessengerServiceConnectionV1 mServiceConnectionV1
            = new MessengerServiceConnectionV1();
    private Messenger mRemoteMessenger;
    private Messenger mReplyMessenger;
    private Handler mHandler;

    public ApiWrapperByMessenger(Context context, ApiCallback callback){
        mContext = context;
        mCallback = callback;

        HandlerThread handlerThread = new HandlerThread("ApiWrapperByMessenger");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), new CallbackImpl());
        mReplyMessenger = new Messenger(mHandler);

        Intent intent =
                new Intent("com.ldb.android.example.wrappedaidlservice.MessengerService");
        mContext.bindService(Util.createExplicitFromImplicitIntent(mContext, intent),
                mServiceConnectionV1, Context.BIND_AUTO_CREATE);
    }

    public void storeData(CustomData customData){
        if(mRemoteMessenger != null){
            Message request = Message.obtain();
            request.what = MessageApi.MSG_STORE_TEXT;
            // Can't use Message.obj to send the info.
//            request.obj = customData;
            // Should use Bundle as a parcel.
            Bundle bundle = new Bundle();
            bundle.putParcelable(MessageApi.ARG_STORE_TEXT, customData);
            request.setData(bundle);
            request.replyTo = mReplyMessenger;
            try {
                mRemoteMessenger.send(request);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAllDataSince(long timestamp, int size){
        if (mRemoteMessenger != null) {
            Message request = Message.obtain();
            request.what = MessageApi.MSG_GET_DATA;
            Bundle bundle = new Bundle();
            bundle.putLong(MessageApi.ARG_GET_DATA_SINCE, timestamp);
            bundle.putInt(MessageApi.ARG_GET_DATA_SIZE, size);
            request.setData(bundle);
            request.replyTo = mReplyMessenger;
            try {
                mRemoteMessenger.send(request);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void release(){
        mContext.unbindService(mServiceConnectionV1);
    }

    private class MessengerServiceConnectionV1 implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteMessenger = new Messenger(service);
            if(mCallback != null){
                mCallback.onApiReady(ApiWrapperByMessenger.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteMessenger = null;
            mReplyMessenger = null;
            if(mCallback != null){
                mCallback.onApiLost();
            }
        }
    }

    private class CallbackImpl implements Handler.Callback{
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MessageApi.MSG_STORE_CALLBACK:
                    Bundle bundle = msg.getData();
                    int result = bundle.getInt(MessageApi.ARG_STORE_CALLBACK_RESULT);
                    String info = bundle.getString(MessageApi.ARG_STORE_CALLBACK_INFO);
                    if(mCallback != null) {
                        mCallback.onDataUpdated(result, info);
                    }
                    return true;
                case MessageApi.MSG_GET_DATA_CALLBACK:
                    Bundle bundleGet = msg.getData();

                    Log.d(TAG, "ResultCount classloader: " + ResultCount.class.getClassLoader());
                    Log.d(TAG, "CustomData classloader: " + CustomData.class.getClassLoader());
                    // Should appoint the class loader, otherwise some FATAL EXCEPTIONS like those:
                    // Class not found when unmarshalling
                    // java.lang.ClassNotFoundException
                    // java.lang.NoClassDefFoundError: Class not found using the boot class loader; no stack trace available
                    bundleGet.setClassLoader(CustomData.class.getClassLoader());

                    // Can't cast Parcelable[] to CustomData[] directly, otherwise:
                    // android.os.Parcelable[] cannot be cast to CustomData[]
//                    CustomData[] customData = (CustomData[])
//                            bundleGet.getParcelableArray(MessageApi.ARG_GET_DATA_RESULT);
                    Parcelable[] data =
                            bundleGet.getParcelableArray(MessageApi.ARG_GET_DATA_RESULT);
                    CustomData[] customData = new CustomData[data.length];
                    // This approach is ok.
//                    customData = Arrays.copyOf(data, data.length, CustomData[].class);
                    // This approach is ok too.
                    for(int i = 0; i < data.length; i++){
                        customData[i] = (CustomData) data[i];
                    }
//                    int count = bundleGet.getInt(MessageApi.ARG_GET_DATA_RESULT_COUNT);
                    ResultCount resultCount = bundleGet.getParcelable(MessageApi.ARG_GET_DATA_RESULT_COUNT);

                    if(mCallback != null) {
//                        mCallback.onGetData(customData, count);
                        mCallback.onGetData(customData, resultCount);
                    }
                    return true;
            }
            return false;
        }
    }
}
