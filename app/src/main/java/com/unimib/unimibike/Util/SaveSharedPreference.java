package com.unimib.unimibike.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    private static final String PREF_USER_NAME= "username";
    private static final String PREF_USER_ROLE= "userrole";
    private static final String PREF_USER_ID = "userid";
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static void setUserName(Context ctx, String userName,String userRole, int userID)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_ROLE, userRole);
        editor.putInt(PREF_USER_ID, userID);
        editor.apply();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPrefUserRole(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ROLE, "");
    }
    public static int getUserID(Context ctx){
        return getSharedPreferences(ctx).getInt(PREF_USER_ID, 0);
    }
    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }
}