package com.ldb.android.example.wrappedaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.IBinder;
import android.os.RemoteException;

import com.ldb.android.example.wrappedaidl.aidl.AidlCallback;
import com.ldb.android.example.wrappedaidl.aidl.ApiInterfaceV1;
import com.ldb.android.example.wrappedaidl.aidl.CustomData;
import com.ldb.android.example.wrappedaidl.util.Util;

import java.util.List;

/**
 * Created by lsp on 2016/9/2.
 */
public class ApiWrapper {

    private Context mContext;
    private ApiCallback mCallback;
    private ApiInterfaceV1 mServiceV1;
    private MyServiceConnectionV1 mServiceConnection = new MyServiceConnectionV1();


    public ApiWrapper(Context context, ApiCallback callback){
        mContext = context;
        mCallback = callback;
        Intent intent = new Intent("com.ldb.android.example.wrappedaidlservice.AidlService");
        // Since Android 5.0(Lollipop), it doesn't support implicit intent.
        mContext.bindService(Util.createExplicitFromImplicitIntent(mContext, intent),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void release(){
        mContext.unbindService(mServiceConnection);
    }

    public boolean isPrime(long value){
        if(mServiceV1 != null){
            try {
                return mServiceV1.isPrime(value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void getAllDataSince(long timestamp, CustomData[] result){
        if (mServiceV1 != null) {
            try {
                mServiceV1.getAllDataSince(timestamp, result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void storeData(CustomData data){
        if(mServiceV1 != null){
            try {
                mServiceV1.storeData(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private AidlCallback.Stub mAidlCallback = new AidlCallback.Stub() {
        @Override
        public void onDataUpdated(CustomData[] data) throws RemoteException {
            if(mCallback != null){
                mCallback.onDataUpdated(data);
            }
        }
    };

    private class MyServiceConnectionV1 implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceV1 = ApiInterfaceV1.Stub.asInterface(service);
            try {
                mServiceV1.setCallback(mAidlCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCallback.onApiReady(ApiWrapper.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceV1 = null;
            if(mCallback != null){
                mCallback.onApiLost();
            }
        }
    }
}
