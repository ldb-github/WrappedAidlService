package com.ldb.android.example.wrappedaidlservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ldb.android.example.wrappedaidl.aidl.AidlCallback;
import com.ldb.android.example.wrappedaidl.aidl.ApiInterfaceV1;
import com.ldb.android.example.wrappedaidl.aidl.CustomData;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lsp on 2016/9/1.
 */
public class AidlService extends Service {

    private static final String TAG = "AidlService";

    private ArrayList<CustomData> mCustomDataCollection;
    private AidlCallback mCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        mCustomDataCollection = new ArrayList<>();
        // TODO Populate the list with stored value...
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public static boolean isPrimeImpl(long number) {
        // Implementation left out for brevity...
        return false;
    }

    private void getDataSinceImpl(CustomData[] result, Date since) {
        int size = mCustomDataCollection.size();
        Log.d(TAG, "getDataSinceImpl size = " + size);
        Log.d(TAG, "since: " + since);
        int pos = 0;
        for (int i = 0; i < size && pos < result.length; i++) {
            CustomData storedValue = mCustomDataCollection.get(i);
            Log.d(TAG, "storedValue " + i + ": " + storedValue.getCreated());
            if (since.before(storedValue.getCreated())) {
                Log.d(TAG, "add " + i);
                result[pos++] = storedValue;
            }
        }
    }

    private void storeDataImpl(CustomData data) {
        Log.d(TAG, data.getName() + " -- " + data.getCreated());
        int size = mCustomDataCollection.size();
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < size; i++) {
            CustomData customData = mCustomDataCollection.get(i);
            if (customData.equals(data)) {
                mCustomDataCollection.set(i, data);
                return;
            }
        }
        mCustomDataCollection.add(data);
    }

    private final ApiInterfaceV1.Stub mBinder = new ApiInterfaceV1.Stub() {
        @Override
        public boolean isPrime(long value) throws RemoteException {
            return isPrimeImpl(value);
        }

        @Override
        public void getAllDataSince(long timestamp, CustomData[] result) throws RemoteException {
            getDataSinceImpl(result, new Date(timestamp));
        }

        @Override
        public void storeData(CustomData data) throws RemoteException {
            storeDataImpl(data);
            if(mCallback != null){
                mCallback.onDataUpdated(new CustomData[]{data});
            }
        }

        @Override
        public void setCallback(AidlCallback callback) throws RemoteException {
            mCallback = callback;
            mCallback.asBinder().linkToDeath(new DeathRecipient() {
                @Override
                public void binderDied() {
                    Log.d(TAG, "binderDied");
                    mCallback = null;
                }
            }, 0);
        }
    };

}
