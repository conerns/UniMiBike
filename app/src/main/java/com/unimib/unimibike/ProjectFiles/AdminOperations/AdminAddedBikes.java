package com.unimib.unimibike.ProjectFiles.AdminOperations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.unimib.unimibike.Model.BikeHistory;
import com.unimib.unimibike.ProjectFiles.Adapters.BikeAddedListAdapter;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikeHistoryViewModel;
import com.unimib.unimibike.databinding.ActivityListaAdminHistoryAddedBikeBinding;

import java.util.List;

public class AdminAddedBikes extends AppCompatActivity {
    ActivityListaAdminHistoryAddedBikeBinding binding;
    BikeAddedListAdapter adapter;
    private BikeHistoryViewModel model;
    private MutableLiveData<List<BikeHistory>> liveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaAdminHistoryAddedBikeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getBikesHistory(getIntent().getIntExtra("user_id", 0));
    }

    public void getBikesHistory(int user_id){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        model = new BikeHistoryViewModel();
        final Observer<List<BikeHistory>> observer = new Observer<List<BikeHistory>>() {
            @Override
            public void onChanged(List<BikeHistory> bikeAdded) {
                adapter = new BikeAddedListAdapter(bikeAdded, getApplicationContext());
                binding.recyclerView.setAdapter(adapter);
            }
        };
        liveData = model.getBikeAdded(getApplicationContext(), user_id);
        liveData.observe(this, observer);
    }
}
