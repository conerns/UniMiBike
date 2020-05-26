package com.unimib.unimibike.ProjectFiles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.ArrayList;
import java.util.List;

public class FrameStorico extends Fragment {
    private RecyclerView recyclerView;
    private RentalFrameAdapter adapter;
    private ArrayList<Rental> rackArrayList;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_storico,container, false);
        addValuesFromDB();
        return view;
    }

    private void addValuesFromDB() {
        rackArrayList = new ArrayList<>();
        UnimibBikeFetcher.getRentals(getContext(),
                SaveSharedPreference.getUserID(getActivity().getApplicationContext()),
                new ServerResponseParserCallback<List<Rental>>() {
            @Override
            public void onSuccess(List<Rental> response) {
                Log.d("Fragment", response.toString());
                rackArrayList.addAll(response);
                recyclerView = view.findViewById(R.id.recycler_storico);
                adapter = new RentalFrameAdapter(rackArrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });

    }
}
