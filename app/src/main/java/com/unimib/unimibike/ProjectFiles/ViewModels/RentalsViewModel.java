package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.ProjectFiles.Repositories.RentalsRepository;

import java.util.List;

public class RentalsViewModel extends ViewModel {

    private MutableLiveData<List<Rental>> rentals;

    public MutableLiveData<List<Rental>> getUserRentals(final Context context, final int user_id) {
        if (rentals == null)
            rentals = new MutableLiveData<>();

        RentalsRepository.getInstance().getUserRentals(context, user_id, rentals);
        return rentals;
    }
}
