package com.unimib.unimibike.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Rental;

public class SaveSharedPreference
{
    private static final String PREF_USER_NAME= "username";
    private static final String PREF_USER_ROLE= "userrole";
    private static final String PREF_USER_ID = "userid";
    private static final String PREF_USER_STATE = "user_state";
    private static final String PREF_RENTAL_IN_PROGRESS = "Pref_rental_in_progress";
    private static final String PREF_RENTAL_IN_PROGRESS_BIKE = "Pref_rental_in_progress_bike";
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
    public static int getUserState(Context ctx){
            return getSharedPreferences(ctx).getInt(PREF_USER_STATE, 0);
    }
    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_NAME);
        editor.remove(PREF_USER_ROLE);
        editor.remove(PREF_USER_ID);
        editor.remove(PREF_USER_STATE);
        editor.apply();
    }
    public static void setPrefRentalInProgress(Context ctx, Rental rental_in_progress, Bike bike_used){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();

        Gson gson = new Gson();
        String json = gson.toJson(rental_in_progress);
        editor.putString(PREF_RENTAL_IN_PROGRESS, json);
        json = gson.toJson(bike_used);
        editor.putString(PREF_RENTAL_IN_PROGRESS_BIKE,json);
        editor.apply();
    }

    public static Rental getRentalInProgress(Context ctx){
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString(PREF_RENTAL_IN_PROGRESS, null);
        return gson.fromJson(json, Rental.class);
    }

    public static Bike getBikeRentalInProgress(Context ctx){
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString(PREF_RENTAL_IN_PROGRESS_BIKE, null);
        return gson.fromJson(json, Bike.class);
    }

    public static void clearRental_in_progress(Context ctx)
    {
        //da controllare perchè cosi cancello anche l'utente in sessione
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_RENTAL_IN_PROGRESS);
        editor.remove(PREF_RENTAL_IN_PROGRESS_BIKE);
        editor.apply();
    }
}