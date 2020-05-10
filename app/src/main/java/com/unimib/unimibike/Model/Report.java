package com.unimib.unimibike.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable {

    private int mBikeId;
    private int mUserId;
    private int mType;
    private String mDescription;
    private String mCreatedOn;

    public Report(int mBikeId,int mUserId,int mType, String mDescription, String mCreatedOn) {
        this.mBikeId = mBikeId;
        this.mUserId = mUserId;
        this.mType = mType;
        this.mDescription = mDescription;
        this.mCreatedOn = mCreatedOn;
    }

    public Report() {
    }

    private Report(Parcel in) {
        mBikeId = in.readInt();
        mUserId = in.readInt();
        mType = in.readInt();
        mDescription = in.readString();
        mCreatedOn = in.readString();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(mBikeId);
        out.writeInt(mUserId);
        out.writeInt(mType);
        out.writeString(mDescription);
        out.writeString(mCreatedOn);
    }

    public int getBikeId() {
        return mBikeId;
    }

    public void setBikeId(int mBikeId) {
        this.mBikeId = mBikeId;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getCreatedOn() {
        return mCreatedOn;
    }

    public void setCreatedOn(String mCreatedOn) {
        this.mCreatedOn = mCreatedOn;
    }

    @Override
    public String toString() {
        return "Report{" +
                "BikeId=" + mBikeId +
                ", UserId=" + mUserId +
                ", Type=" + mType +
                ", Description='" + mDescription + '\'' +
                ", CreatedOn='" + mCreatedOn + '\'' +
                '}';
    }
}
