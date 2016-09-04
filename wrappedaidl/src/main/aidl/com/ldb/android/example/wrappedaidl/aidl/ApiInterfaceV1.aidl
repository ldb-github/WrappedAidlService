// ApiInterfaceV1.aidl
package com.ldb.android.example.wrappedaidl.aidl;

// Declare any non-default types here with import statements
import com.ldb.android.example.wrappedaidl.aidl.CustomData;
import com.ldb.android.example.wrappedaidl.aidl.AidlCallback;

interface ApiInterfaceV1 {
    boolean isPrime(long value);
    void getAllDataSince(long timestamp, out CustomData[] result);
    void storeData(in CustomData data);
    void setCallback(in AidlCallback callback);
}
