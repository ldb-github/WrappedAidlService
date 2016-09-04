package com.ldb.android.example.wrappedaidl.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lsp on 2016/9/2.
 */
public class CustomData implements Parcelable {

    private static final String TAG = "CustomData";

    private String mName;
    private List<String> mReference;
    private Date mCreated;

    public CustomData(){
        mName = "";
        mReference = new ArrayList<>();
        mCreated = new Date();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getReference() {
        return mReference;
    }

    public void setReference(List<String> reference) {
        mReference = reference;
    }

    public Date getCreated() {
        return mCreated;
    }

    public void setCreated(Date created) {
        mCreated = created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeStringList(mReference);
        dest.writeLong(mCreated.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        CustomData that = (CustomData) o;
        return mCreated.equals(that.mCreated) && mName.equals(that.mName);
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mCreated.hashCode();
        return result;
    }

    public static final Parcelable.Creator<CustomData> CREATOR = new Parcelable.Creator<CustomData>(){
        @Override
        public CustomData createFromParcel(Parcel source) {
            CustomData customData = new CustomData();
            customData.mName = source.readString();
//            customData.mReference = new ArrayList<>();
            source.readStringList(customData.mReference);
            Long created = source.readLong();
            Log.d(TAG, "createFromParcel " + created);
            customData.mCreated = new Date(created);
            return customData;
        }

        @Override
        public CustomData[] newArray(int size) {
            return new CustomData[size];
        }
    };
}

