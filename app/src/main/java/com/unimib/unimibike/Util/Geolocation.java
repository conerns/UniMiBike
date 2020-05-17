package com.unimib.unimibike.Util;

import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Looper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Geolocation{

    private FusedLocationProviderClient mFusedLocationClient;
    private Activity activity;
    private GeolocationCallback geolocationCallback;

    public Geolocation(Activity activity, GeolocationCallback geolocationCallback){
        this.activity = activity;
        this.geolocationCallback = geolocationCallback;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);
    }
    public Geolocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(new Activity());
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else
                            geolocationCallback.positionCallback(location);
                    }
                }
        );
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

        }
    };


    public static double distance(LatLng first_position, LatLng second_position){
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(second_position.latitude - first_position.latitude);
        double lonDistance = Math.toRadians(second_position.longitude - first_position.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(first_position.latitude)) * Math.cos(Math.toRadians(second_position.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

}
