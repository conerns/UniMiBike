package com.unimib.unimibike.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    private static final String PREF_USER_NAME= "username";
    private static final String PREF_USER_ROLE= "userrole";
    private static final String PREF_USER_ID = "userid";
    private static final String PREF_USER_STATE = "user_state";
    private static final String PREF_USER_REMEMBER = "remember_me";
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static void setUserName(Context ctx, String userName,String userRole, int userID, int user_state){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_ROLE, userRole);
        editor.putInt(PREF_USER_ID, userID);
        editor.putInt(PREF_USER_STATE, user_state);
        editor.apply();
    }
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPrefUserRole(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ROLE, "");
    }

    public static int getUserID(Context ctx){
        return getSharedPreferences(ctx).getInt(PREF_USER_ID, 0);
    }

    public static int getUserState(Context ctx){
            return getSharedPreferences(ctx).getInt(PREF_USER_STATE, 0);
    }

    public static boolean getPrefUserRemember(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_USER_REMEMBER, false);
    }

    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_NAME);
        editor.remove(PREF_USER_ROLE);
        editor.remove(PREF_USER_ID);
        editor.remove(PREF_USER_STATE);
        editor.remove(PREF_USER_REMEMBER);
        editor.apply();
    }

    public static void setLogged(Context ctx, boolean remember_me){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_USER_REMEMBER, remember_me);
        editor.apply();
    }
}