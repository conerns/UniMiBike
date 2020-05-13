package com.unimib.unimibike.Model;
import android.os.Parcel;
import android.os.Parcelable;

public class BikeState implements Parcelable {
    private int mId;
    private String mDescription;

    public BikeState() {
    }

    private BikeState(Parcel in) {
        mId = in.readInt();
        mDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BikeState> CREATOR = new Creator<BikeState>() {
        @Override
        public BikeState createFromParcel(Parcel in) {
            return new BikeState(in);
        }

        @Override
        public BikeState[] newArray(int size) {
            return new BikeState[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}
