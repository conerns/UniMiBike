package com.unimib.unimibike.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rental implements Parcelable {

    private int mId;
    private Bike mBike;
    private String mStartedOn, mCompletedOn;
    private Rack mStartRack, mEndRack;

    public Rental(int mId, Bike mBike, String mStartedOn, String mCompletedOn, Rack mStartRack, Rack mEndRack) {
        this.mId = mId;
        this.mBike = mBike;
        this.mStartedOn = mStartedOn;
        this.mCompletedOn = mCompletedOn;
        this.mStartRack = mStartRack;
        this.mEndRack = mEndRack;
    }

    public Rental() {
    }

    private Rental(Parcel in) {
        mId = in.readInt();
        mBike = in.readParcelable(Bike.class.getClassLoader());
        mStartedOn = in.readString();
        mCompletedOn = in.readString();
        mStartRack = in.readParcelable(Rack.class.getClassLoader());
        mEndRack = in.readParcelable(Rack.class.getClassLoader());
    }

    public static final Creator<Rental> CREATOR = new Creator<Rental>() {
        @Override
        public Rental createFromParcel(Parcel in) {
            return new Rental(in);
        }

        @Override
        public Rental[] newArray(int size) {
            return new Rental[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(mId);
        out.writeParcelable(mBike, i);
        out.writeString(mStartedOn);
        out.writeString(mCompletedOn);
        out.writeParcelable(mStartRack, i);
        out.writeParcelable(mEndRack, i);
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public Bike getBike() {
        return mBike;
    }

    public void setBike(Bike mBike) {
        this.mBike = mBike;
    }

    public String getStartedOn() {
        return mStartedOn;
    }

    public void setStartedOn(String mStartedOn) {
        this.mStartedOn = mStartedOn;
    }

    public String getCompletedOn() {
        return mCompletedOn;
    }

    public void setCompletedOn(String mCompletedOn) {
        this.mCompletedOn = mCompletedOn;
    }

    public Rack getStartRack() {
        return mStartRack;
    }

    public void setStartRack(Rack mStartRack) {
        this.mStartRack = mStartRack;
    }

    public Rack getEndRack() {
        return mEndRack;
    }

    public void setEndRack(Rack mEndRack) {
        this.mEndRack = mEndRack;
    }

    @Override
    public String toString() {
        String s = "Rental{" + "Id=" + mId;
        if (mBike != null) {
            s += ", Bike=" + mBike.toString();
        }
        s += ", StartedOn ='" + mStartedOn + "' , CompletedOn='" + mCompletedOn + '\'';
        if (mStartRack != null) {
            s += ", StartRack=" + mStartRack.toString();
        }
        if (mEndRack != null) {
            s += ", EndRack=" + mEndRack.toString();
        }
        s += '}';
        return s;
    }
}
