package com.unimib.unimibike.Model;
import android.os.Parcel;
import android.os.Parcelable;

public class Bike implements Parcelable {

    private int mId;
    private int mUnlockCode;
    private BikeState mBikeState;
    private Rack mRack;

    public Bike(int mId, int mUnlockCode, BikeState mBikeState, Rack mRack) {
        this.mId = mId;
        this.mUnlockCode = mUnlockCode;
        this.mBikeState = mBikeState;
        this.mRack = mRack;
    }

    public Bike() {
    }

    private Bike(Parcel in) {
        mId = in.readInt();
        mUnlockCode = in.readInt();
        mBikeState = in.readParcelable(BikeState.class.getClassLoader());
        mRack = in.readParcelable(Rack.class.getClassLoader());
    }

    public static final Creator<Bike> CREATOR = new Creator<Bike>() {
        @Override
        public Bike createFromParcel(Parcel in) {
            return new Bike(in);
        }

        @Override
        public Bike[] newArray(int size) {
            return new Bike[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(mId);
        out.writeInt(mUnlockCode);
        out.writeParcelable(mBikeState, i);
        out.writeParcelable(mRack, i);
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public BikeState getBikeState() {
        return mBikeState;
    }

    public void setBikeState(BikeState mBikeState) {
        this.mBikeState = mBikeState;
    }

    public Rack getRack() {
        return mRack;
    }

    public void setRack(Rack mRack) {
        this.mRack = mRack;
    }

    public int getUnlockCode() {
        return mUnlockCode;
    }

    public void setUnlockCode(int mUnlockCode) {
        this.mUnlockCode = mUnlockCode;
    }

    @Override
    public String toString() {
        String s = "Bike{" +
                "id=" + mId +
                ", unlockCode=" + mUnlockCode +
                ", bikeState='" + mBikeState + '\'' +
                '}';
        if(mRack != null) {
            s += '\'' + ", rack =" + mRack.toString() + '\'';
        }
        return s;
    }
}
