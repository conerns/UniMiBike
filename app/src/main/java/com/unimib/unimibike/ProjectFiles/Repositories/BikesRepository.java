package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

public class BikesRepository {
    private static BikesRepository instance;

    public BikesRepository(){}

    public static synchronized BikesRepository getInstance() {
        if (instance == null) {
            instance = new BikesRepository();
        }
        return instance;
    }

    public void addBike(final int user_id, final int unlock_code,
                        final int rack_id, final Context context, final MutableLiveData<Bike> bike){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    addBikeAdminIntoDatabase(user_id, unlock_code, rack_id, context, bike);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void getBike(final int bike_id, final Context context, final MutableLiveData<Bike> bike){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getBikeFromDatabase(bike_id, context, bike);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void modifyAdminBikePosition(final Context context, final int bike_id,
                                        final int new_rack_id, final int user_id, final MutableLiveData<Resource<Bike>> bike){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    modifyAdminBikePositionIntoDatabase(context, bike_id, new_rack_id, user_id, bike);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void removeAdminBike(final Context context, final int bike_id,
                                final int user_id, final MutableLiveData<Bike> bike){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    removeAdminBikeIntoDatabase(context, bike_id, user_id, bike);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void addBikeAdminIntoDatabase(final int user_id, final int unlock_code,
                                          final int rack_id, final Context context, final MutableLiveData<Bike> bike){
        UnimibBikeFetcher.postAddBike(context, user_id, rack_id, unlock_code, new ServerResponseParserCallback<Bike>() {
            @Override
            public void onSuccess(Bike response) {
                bike.postValue(response);
            }
            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    private void getBikeFromDatabase(final int bike_id, final Context context, final MutableLiveData<Bike> bike){
        UnimibBikeFetcher.getBike(context, bike_id, new ServerResponseParserCallback<Bike>() {
            @Override
            public void onSuccess(Bike response) {
                bike.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    private void modifyAdminBikePositionIntoDatabase(final Context context, final int bike_id,
                                                     final int new_rack_id, final int user_id, final MutableLiveData<Resource<Bike>> bike){
        UnimibBikeFetcher.postModifyBike(context, bike_id, new_rack_id, user_id, new ServerResponseParserCallback<Bike>() {
            @Override
            public void onSuccess(Bike response) {
                Resource<Bike> bikeResource = new Resource<>();
                bikeResource.setData(response);
                bikeResource.setStatusCode(200);
                bike.postValue(bikeResource);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                Resource<Bike> bikeResource = new Resource<>();
                bikeResource.setStatusCode(404);
                bike.postValue(bikeResource);
            }
        });
    }

    private void removeAdminBikeIntoDatabase(final Context context, final int bike_id,
                                             final int user_id, final MutableLiveData<Bike> bike){
        UnimibBikeFetcher.postRemoveBike(context, bike_id, user_id, new ServerResponseParserCallback<Bike>() {
            @Override
            public void onSuccess(Bike response) {
                bike.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }
}
