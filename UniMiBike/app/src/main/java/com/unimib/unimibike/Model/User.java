package com.unimib.unimibike.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String mEmail;
    private int mUserState;
    private String mRole;

    public User(String mEmail, int mUserState, String mRole) {
        this.mEmail = mEmail;
        this.mUserState = mUserState;
        this.mRole= mRole;
    }

    public User() {
    }

    private User(Parcel in) {
        mEmail = in.readString();
        mUserState = in.readInt();
        mRole = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mEmail);
        out.writeInt(mUserState);
        out.writeString(mRole);
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmRole() {
        return mRole;
    }

    public void setmRole(String mRole) {
        this.mRole = mRole;
    }

    public int getUserState() {
        return mUserState;
    }

    public void setUserState(int mUserState) {
        this.mUserState = mUserState;
    }

    @Override
    public String toString() {
        return "User{" +
                "Email='" + mEmail + '\'' +
                ", UserState='" + mUserState + '\'' +
                ", Role='"+ mRole + '\'' +
                '}';
    }
}
