package com.ldb.android.example.wrappedaidlservice.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ldb.android.example.wrappedaidl.aidl.CustomData;
import com.ldb.android.example.wrappedaidl.aidl.ResultCount;
import com.ldb.android.example.wrappedaidl.api.MessageApi;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lsp on 2016/9/2.
 */
public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    private ArrayList<CustomData> mCustomDataArrayList;
    private Messenger mMessenger;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mCustomDataArrayList = new ArrayList<>();
        HandlerThread handlerThread = new HandlerThread("MessengerService");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), new CallbackImpl());
        mMessenger = new Messenger(mHandler);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.getLooper().quit();
        mMessenger = null;
    }

    private void storeData(CustomData data){
        Log.d(TAG, data.getName() + " -- " + data.getCreated());
        int size = mCustomDataArrayList.size();
        for(int i = 0; i < size; i++){
            CustomData customData = mCustomDataArrayList.get(i);
            if(customData.equals(data)){
                mCustomDataArrayList.set(i, data);
                return;
            }
        }
        mCustomDataArrayList.add(data);
    }

    private int getDataSinceImpl(CustomData[] result, Date since) {
        int count = 0;
        int size = mCustomDataArrayList.size();
        Log.d(TAG, "getDataSinceImpl size = " + size);
        Log.d(TAG, "since: " + since);
        int pos = 0;
        for (int i = 0; i < size && pos < result.length; i++) {
            CustomData storedValue = mCustomDataArrayList.get(i);
            Log.d(TAG, "storedValue " + i + ": " + storedValue.getCreated());
            if (since.before(storedValue.getCreated())) {
                Log.d(TAG, "add " + i);
                result[pos++] = storedValue;
                count++;
            }
        }

        return count;
    }

    private class CallbackImpl implements Handler.Callback{
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MessageApi.MSG_STORE_TEXT:
                    Bundle data = msg.getData();
                    data.setClassLoader(CustomData.class.getClassLoader());
                    CustomData customData = (CustomData)
                            data.getParcelable(MessageApi.ARG_STORE_TEXT); //msg.obj;
                    storeData(customData);
                    Message reply = Message.obtain();
                    reply.what = MessageApi.MSG_STORE_CALLBACK;
                    Bundle bundle = new Bundle();
                    bundle.putInt(MessageApi.ARG_STORE_CALLBACK_RESULT, 0);
                    bundle.putString(MessageApi.ARG_STORE_CALLBACK_INFO, "Updated successfully");
                    reply.setData(bundle);
                    try {
                        msg.replyTo.send(reply);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return true;
                case MessageApi.MSG_GET_DATA:
                    Bundle dataGet= msg.getData();
                    long since = dataGet.getLong(MessageApi.ARG_GET_DATA_SINCE);
                    int size = dataGet.getInt(MessageApi.ARG_GET_DATA_SIZE);
                    CustomData[] result = new CustomData[size];
                    int count = getDataSinceImpl(result, new Date(since));
                    ResultCount resultCount = new ResultCount();
                    resultCount.setCount(count);
                    Message replyGet = Message.obtain();
                    replyGet.what = MessageApi.MSG_GET_DATA_CALLBACK;
                    Bundle bundleGet = new Bundle();
//                    bundleGet.putInt(MessageApi.ARG_GET_DATA_RESULT_COUNT, count);
                    bundleGet.putParcelable(MessageApi.ARG_GET_DATA_RESULT_COUNT, resultCount);
                    bundleGet.putParcelableArray(MessageApi.ARG_GET_DATA_RESULT, result);
                    replyGet.setData(bundleGet);
                    try {
                        msg.replyTo.send(replyGet);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return true;
            }
            return false;
        }
    }
}
