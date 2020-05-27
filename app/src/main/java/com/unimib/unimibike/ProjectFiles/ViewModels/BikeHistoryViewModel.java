package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.BikeHistory;
import com.unimib.unimibike.ProjectFiles.Repositories.BikeHistoryRepository;

import java.util.List;

public class BikeHistoryViewModel extends ViewModel {
    private MutableLiveData<List<BikeHistory>> listOfBikeHistory;

    public MutableLiveData<List<BikeHistory>> getBikeAdded(Context context, final int user_id) {
        if (listOfBikeHistory == null)
            listOfBikeHistory = new MutableLiveData<>();

        BikeHistoryRepository.getInstance().getAdminBikeAdded(user_id,context, listOfBikeHistory);
        return listOfBikeHistory;
    }
}
