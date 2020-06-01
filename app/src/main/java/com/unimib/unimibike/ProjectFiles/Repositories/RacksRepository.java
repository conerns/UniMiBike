package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.List;

public class RacksRepository {
    private static RacksRepository instance;

    private RacksRepository(){}

    public static synchronized RacksRepository getInstance() {
        if (instance == null) {
            instance = new RacksRepository();
        }
        return instance;
    }

    public void getRacks(final Context context, final MutableLiveData<List<Rack>> racks) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getRacksFromDatabase(context, racks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void getRack(final Context context, final MutableLiveData<Resource<Rack>> rack, final int rack_id) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getRackByIdFromDatabase(rack_id, context, rack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }


    private void getRacksFromDatabase(Context context, final MutableLiveData<List<Rack>> racks){
        UnimibBikeFetcher.getRacks(context, new ServerResponseParserCallback<List<Rack>>() {
            @Override
            public void onSuccess(List<Rack> response) {
                racks.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    private void getRackByIdFromDatabase(int rack_id, Context context,final MutableLiveData<Resource<Rack>> rack) {
        UnimibBikeFetcher.getRack(context, rack_id, new ServerResponseParserCallback<Rack>() {
            @Override
            public void onSuccess(Rack response) {
                Resource<Rack> rackResource = new Resource<>();
                rackResource.setData(response);
                rackResource.setStatusCode(200);
                rack.postValue(rackResource);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                Resource<Rack> rackResource = new Resource<>();
                rackResource.setStatusCode(404);
                rack.postValue(rackResource);
            }
        });
    }
}
