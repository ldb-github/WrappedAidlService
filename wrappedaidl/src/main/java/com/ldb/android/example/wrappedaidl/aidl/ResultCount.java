package com.ldb.android.example.wrappedaidl.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lsp on 2016/9/3.
 */
public class ResultCount implements Parcelable {

    private int mCount;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCount);
    }

    public static final Parcelable.Creator<ResultCount> CREATOR = new Parcelable.Creator<ResultCount>(){
        @Override
        public ResultCount createFromParcel(Parcel source) {
            ResultCount resultCount = new ResultCount();
            resultCount.mCount = source.readInt();
            return resultCount;
        }

        @Override
        public ResultCount[] newArray(int size) {
            return new ResultCount[size];
        }
    };
}
