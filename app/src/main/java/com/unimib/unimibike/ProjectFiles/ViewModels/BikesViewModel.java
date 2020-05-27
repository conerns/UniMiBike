package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.ProjectFiles.Repositories.BikesRepository;

import java.util.List;

public class BikesViewModel extends ViewModel {
    private MutableLiveData<List<Bike>> listOfBikes;
    private MutableLiveData<Bike> bike;

    public MutableLiveData<Bike> adminAddBike(Context context, final int user_id, final int unlock_code, final int rack_id) {
        if (bike == null)
            bike = new MutableLiveData<>();

        BikesRepository.getInstance().addBike(user_id, unlock_code, rack_id, context, bike);
        return bike;
    }

    public MutableLiveData<Bike> getBike(Context context, final int bike_id){
        if (bike == null)
            bike = new MutableLiveData<>();

        BikesRepository.getInstance().getBike(bike_id, context, bike);
        return bike;
    }

}
