package com.unimib.unimibike.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class BikeHistory implements Parcelable {
    private String mBikeDescription;
    private int mBikeId;
    private String mCreatedOn;
    private int mUnlockCode;
    private String mRackBuildings;

    public BikeHistory(){

    }

    public String getmBikeDescription() {
        return mBikeDescription;
    }

    public void setmBikeDescription(String mBikeDescription) {
        this.mBikeDescription = mBikeDescription;
    }

    public int getmBikeId() {
        return mBikeId;
    }

    public void setmBikeId(int mBikeId) {
        this.mBikeId = mBikeId;
    }

    public String getmCreatedOn() {
        return mCreatedOn;
    }

    public void setmCreatedOn(String mCreatedOn) {
        this.mCreatedOn = mCreatedOn;
    }

    public int getmUnlockCode() {
        return mUnlockCode;
    }

    public void setmUnlockCode(int mUnlockCode) {
        this.mUnlockCode = mUnlockCode;
    }

    public String getmRackBuildings() {
        return mRackBuildings;
    }

    public void setmRackBuildings(String mRackBuildings) {
        this.mRackBuildings = mRackBuildings;
    }

    @Override
    public String toString() {
        return "BikeHistory{" +
                "mBikeDescription='" + mBikeDescription + '\'' +
                ", mBikeId=" + mBikeId +
                ", mCreatedOn='" + mCreatedOn + '\'' +
                ", mUnlockCode=" + mUnlockCode +
                ", mRackBuildings='" + mRackBuildings + '\'' +
                '}';
    }

    protected BikeHistory(Parcel in) {
        mBikeDescription = in.readString();
        mBikeId = in.readInt();
        mCreatedOn = in.readString();
        mUnlockCode = in.readInt();
        mRackBuildings = in.readString();
    }

    public static final Creator<BikeHistory> CREATOR = new Creator<BikeHistory>() {
        @Override
        public BikeHistory createFromParcel(Parcel in) {
            return new BikeHistory(in);
        }

        @Override
        public BikeHistory[] newArray(int size) {
            return new BikeHistory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBikeDescription);
        dest.writeInt(mBikeId);
        dest.writeString(mCreatedOn);
        dest.writeInt(mUnlockCode);
        dest.writeString(mRackBuildings);
    }
}
