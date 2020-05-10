package com.unimib.unimibike.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Building implements Parcelable {

    private int mId;
    private String mName;

    public Building(int mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }

    public Building() {
    }

    private Building(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
    }

    public static final Creator<Building> CREATOR = new Creator<Building>() {
        @Override
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        @Override
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mId);
        out.writeString(mName);
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + mId +
                ", name='" + mName + '\'' +
                '}';
    }
}
