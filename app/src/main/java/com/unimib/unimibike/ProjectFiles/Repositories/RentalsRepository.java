package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.List;

public class RentalsRepository {
    private static RentalsRepository instance;

    public RentalsRepository(){}

    public static synchronized RentalsRepository getInstance() {
        if (instance == null) {
            instance = new RentalsRepository();
        }
        return instance;
    }

    public void getUserRentals(final Context context, final int user_id, final boolean active, final MutableLiveData<List<Rental>> rentals){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getUserRentalsFromDatabase(context, user_id, active, rentals);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void startRental(final Context context, final int user_id, final int bike_id, final MutableLiveData<Rental> rental){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    postStartRentalIntoDatabase(context, user_id, bike_id, rental);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void stopRental(final Context context, final int rental_id, final int rack_id, final MutableLiveData<Rental> rental){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    postCloseRentalIntoDatabase(context, rental_id, rack_id, rental);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }





    private void getUserRentalsFromDatabase(final Context context, final int user_id, final boolean active, final MutableLiveData<List<Rental>> rentals){
        UnimibBikeFetcher.getRentals(context, user_id, active, new ServerResponseParserCallback<List<Rental>>() {
            @Override
            public void onSuccess(List<Rental> response) {
                Log.d("GETRENTALFROMDATABASE",response.toString());
                rentals.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    private void postStartRentalIntoDatabase(final Context context, final int user_id,
                                        final int bike_id, final MutableLiveData<Rental> rental){
        UnimibBikeFetcher.postRental(context, bike_id, user_id, new ServerResponseParserCallback<Rental>() {
            @Override
            public void onSuccess(Rental response) {
                rental.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
            }
        });
    }

    private void postCloseRentalIntoDatabase(final Context context, final int rental_id,
                                             final int rack_id, final MutableLiveData<Rental> rental){
        UnimibBikeFetcher.putRental(context,rental_id, rack_id, new ServerResponseParserCallback<Rental>() {
            @Override
            public void onSuccess(Rental response) {
                rental.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }
}
