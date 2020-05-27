package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.unimibike.Model.BikeHistory;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.List;

public class BikeHistoryRepository {
    private static BikeHistoryRepository instance;

    public BikeHistoryRepository(){}

    public static synchronized BikeHistoryRepository getInstance() {
        if (instance == null) {
            instance = new BikeHistoryRepository();
        }
        return instance;
    }

    public void getAdminBikeAdded(final int user_id, final Context context, final MutableLiveData<List<BikeHistory>> bikeHistory){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getAdminAddedBikeFromDatabase(user_id,context, bikeHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void getAdminAddedBikeFromDatabase(final int user_id, final Context context, final MutableLiveData<List<BikeHistory>> bikeHistory){
        UnimibBikeFetcher.postAddedBikes(context, user_id, new ServerResponseParserCallback<List<BikeHistory>>() {
            @Override
            public void onSuccess(List<BikeHistory> response) {
                bikeHistory.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

}
