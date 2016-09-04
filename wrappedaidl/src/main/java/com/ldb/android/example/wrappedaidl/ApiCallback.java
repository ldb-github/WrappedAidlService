package com.ldb.android.example.wrappedaidl;

import com.ldb.android.example.wrappedaidl.aidl.CustomData;
import com.ldb.android.example.wrappedaidl.aidl.ResultCount;

/**
 * Created by lsp on 2016/9/2.
 */
public interface ApiCallback {
    void onApiReady(ApiWrapper apiWrapper);
    void onApiReady(ApiWrapperByMessenger apiWrapper);
    void onApiLost();
    void onDataUpdated(CustomData[] data);
    void onDataUpdated(int result, String info);
    void onGetData(CustomData[] data, int count);
    void onGetData(CustomData[] data, ResultCount count);
}
