package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.Repositories.BikesRepository;

import java.net.ResponseCache;
import java.util.List;

public class BikesViewModel extends ViewModel {
    private MutableLiveData<List<Bike>> listOfBikes;
    private MutableLiveData<Bike> bike;
    private MutableLiveData<Resource<Bike>> resourceBike;

    public MutableLiveData<Resource<Bike>> adminAddBike(Context context, final int user_id, final int unlock_code, final int rack_id) {
        if (resourceBike == null)
            resourceBike = new MutableLiveData<>();

        BikesRepository.getInstance().addBike(user_id, unlock_code, rack_id, context, resourceBike);
        return resourceBike;
    }

    public MutableLiveData<Resource<Bike>> getBike(Context context, final int bike_id){
        if (resourceBike == null)
            resourceBike = new MutableLiveData<>();

        BikesRepository.getInstance().getBike(bike_id, context, resourceBike);
        return resourceBike;
    }

    public MutableLiveData<Resource<Bike>> modifyBikePosition(Context context, final int bike_id,
                                                              final int new_rack_id, final int user_id){
        if (resourceBike == null)
            resourceBike = new MutableLiveData<>();

        BikesRepository.getInstance().modifyAdminBikePosition(context, bike_id, new_rack_id, user_id, resourceBike);
        return resourceBike;
    }

    public MutableLiveData<Resource<Bike>> removeBike(Context context, final int bike_id, final int user_id){
        if (resourceBike == null)
            resourceBike = new MutableLiveData<>();

        BikesRepository.getInstance().removeAdminBike(context, bike_id, user_id, resourceBike);
        return resourceBike;
    }
}
