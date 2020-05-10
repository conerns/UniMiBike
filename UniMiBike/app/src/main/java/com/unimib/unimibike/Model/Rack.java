package com.unimib.unimibike.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class Rack implements Parcelable {

    private int mId;
    private int mAvailableStands, mAvailableBikes;
    private double mLatitude, mLongitude;
    private String mAddressLocality, mStreetAddress, mLocationDescription;
    private List<Building> mBuildings;
    private double mDistance;

    public Rack(int mId, int mAvailableStands, int mAvailableBikes, double mLatitude, double mLongitude, String mAddressLocality, String mStreetAddress, String mLocationDescription, List<Building> mBuildings) {
        this.mId = mId;
        this.mAvailableStands = mAvailableStands;
        this.mAvailableBikes = mAvailableBikes;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAddressLocality = mAddressLocality;
        this.mStreetAddress = mStreetAddress;
        this.mLocationDescription = mLocationDescription;
        this.mBuildings = mBuildings;
    }

    public Rack() {
    }

    private Rack(Parcel in) {
        mId = in.readInt();
        mAvailableStands = in.readInt();
        mAvailableBikes = in.readInt();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mAddressLocality = in.readString();
        mStreetAddress = in.readString();
        mLocationDescription = in.readString();
        mBuildings = in.createTypedArrayList(Building.CREATOR);
        mDistance = in.readDouble();
    }

    public static final Creator<Rack> CREATOR = new Creator<Rack>() {
        @Override
        public Rack createFromParcel(Parcel in) {
            return new Rack(in);
        }

        @Override
        public Rack[] newArray(int size) {
            return new Rack[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(mId);
        out.writeInt(mAvailableStands);
        out.writeInt(mAvailableBikes);
        out.writeDouble(mLatitude);
        out.writeDouble(mLongitude);
        out.writeString(mAddressLocality);
        out.writeString(mStreetAddress);
        out.writeString(mLocationDescription);
        out.writeTypedList(mBuildings);
        out.writeDouble(mDistance);
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getAvailableStands() {
        return mAvailableStands;
    }

    public void setAvailableStands(int mAvailableStands) {
        this.mAvailableStands = mAvailableStands;
    }

    public int getAvailableBikes() {
        return mAvailableBikes;
    }

    public void setAvailableBikes(int mAvailableBikes) {
        this.mAvailableBikes = mAvailableBikes;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getAddressLocality() {
        return mAddressLocality;
    }

    public void setAddressLocality(String mAddressLocality) {
        this.mAddressLocality = mAddressLocality;
    }

    public String getStreetAddress() {
        return mStreetAddress;
    }

    public void setStreetAddress(String mStreetAddress) {
        this.mStreetAddress = mStreetAddress;
    }

    public String getLocationDescription() {
        return mLocationDescription;
    }

    public void setLocationDescription(String mLocationDescription) {
        this.mLocationDescription = mLocationDescription;
    }

    public List<Building> getBuildings() {
        return mBuildings;
    }

    public void setBuildings(List<Building> mBuildings) {
        this.mBuildings = mBuildings;
    }

    public double getDistance() {
        return mDistance;
    }

    public String getDistanceString() {
        String result;
        if (mDistance < 1) {
            int distanceInt = (int) Math.round(mDistance);
            result = String.valueOf(distanceInt*1000) + " m";
        } else {
            NumberFormat formatter = new DecimalFormat("#0.00");
            result = formatter.format(mDistance) + " km";
        }
        return result;
    }

    public void setDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    @Override
    public String toString() {
        String s = "Rack{" +
                "id=" + mId +
                ", availableStands=" + mAvailableStands +
                ", availableBikes=" + mAvailableBikes +
                ", latitude=" + mLatitude +
                ", longitude=" + mLongitude +
                ", addressLocality='" + mAddressLocality + '\'' +
                ", streetAddress='" + mStreetAddress + '\'' +
                ", locationDescription='" + mLocationDescription + '\'';
        if (mBuildings != null) {
            s += ", buildings=" + mBuildings.toString();
        }
        s += ", distance=" + mDistance + '}';
        return s;
    }
}
