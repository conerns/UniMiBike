package com.unimib.unimibike.ProjectFiles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.ProjectFiles.Adapters.RentalFrameAdapter;
import com.unimib.unimibike.ProjectFiles.ViewModels.RentalsViewModel;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentStoricoBinding;

import java.util.List;

public class FrameStorico extends Fragment {
    private RentalFrameAdapter adapter;
    private RentalsViewModel rentalsViewModel;
    private MutableLiveData<List<Rental>> rentalsLiveData;
    private FragmentStoricoBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoricoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        addValuesFromDB();
        return view;
    }

    private void addValuesFromDB() {
        rentalsViewModel = new RentalsViewModel();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerStorico.setLayoutManager(layoutManager);
        final Observer<List<Rental>> observer = new Observer<List<Rental>>() {
            @Override
            public void onChanged(List<Rental> rentals) {
                adapter = new RentalFrameAdapter(rentals);
                binding.recyclerStorico.setAdapter(adapter);
            }
        };
        rentalsLiveData = rentalsViewModel.getUserRentals(getContext(), SaveSharedPreference.getUserID(getActivity().getApplicationContext()));

        rentalsLiveData.observe(requireActivity(), observer);
    }
}
