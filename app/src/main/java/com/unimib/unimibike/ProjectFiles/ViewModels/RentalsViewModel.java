package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.ProjectFiles.Repositories.RentalsRepository;

import java.util.List;

public class RentalsViewModel extends ViewModel {

    private MutableLiveData<List<Rental>> rentals;
    private MutableLiveData<Rental> rental;

    public MutableLiveData<List<Rental>> getUserRentals(final Context context, final int user_id) {
        if (rentals == null)
            rentals = new MutableLiveData<>();

        RentalsRepository.getInstance().getUserRentals(context, user_id, rentals);
        return rentals;
    }

    public MutableLiveData<Rental> starRental(final Context context, final int user_id, final int bike_id) {
        if (rental == null)
            rental = new MutableLiveData<>();

        RentalsRepository.getInstance().startRental(context, user_id, bike_id,rental);
        return rental;
    }

    public MutableLiveData<Rental> endRental(final Context context, final int rental_id, final int rack_id) {
        if (rental == null)
            rental = new MutableLiveData<>();

        RentalsRepository.getInstance().stopRental(context, rental_id, rack_id, rental);
        return rental;
    }
}
