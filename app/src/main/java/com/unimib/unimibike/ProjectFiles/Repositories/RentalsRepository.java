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

    public void getUserRentals(final Context context, final int user_id, final MutableLiveData<List<Rental>> rentals){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getUserRentalsFromDatabase(context, user_id, rentals);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void getUserRentalsFromDatabase(final Context context, final int user_id, final MutableLiveData<List<Rental>> rentals){
        UnimibBikeFetcher.getRentals(context, user_id, new ServerResponseParserCallback<List<Rental>>() {
            @Override
            public void onSuccess(List<Rental> response) {
                rentals.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }
}
