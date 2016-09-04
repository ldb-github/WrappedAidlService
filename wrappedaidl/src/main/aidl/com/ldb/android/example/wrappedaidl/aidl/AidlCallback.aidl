// AidlCallback.aidl
package com.ldb.android.example.wrappedaidl.aidl;

// Declare any non-default types here with import statements
import com.ldb.android.example.wrappedaidl.aidl.CustomData;

oneway interface AidlCallback {
    void onDataUpdated(in CustomData[] data);
}
