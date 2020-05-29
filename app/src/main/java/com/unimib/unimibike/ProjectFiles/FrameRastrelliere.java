package com.unimib.unimibike.ProjectFiles;

import android.location.Location;
import android.os.Bundle;
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

import com.google.android.gms.maps.model.LatLng;

import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.ProjectFiles.Adapters.RacksListAdapter;
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.databinding.FrameListaRastrelliereBinding;

import java.util.List;

public class FrameRastrelliere extends Fragment implements GeolocationCallback {
    private RacksListAdapter adapter;
    private LatLng mCurrentPosition;
    private GeolocationCallback geolocationCallback;
    private RacksViewModel model;
    private MutableLiveData<List<Rack>> liveData;
    private int counter = 0;
    private FrameListaRastrelliereBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrameListaRastrelliereBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        geolocationCallback = this;
        getUserPosition();
        getRacks();
        return view;
    }

    public void getRacks(){
        model = new RacksViewModel();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);
        final Observer<List<Rack>> observer = new Observer<List<Rack>>() {
            @Override
            public void onChanged(List<Rack> racks) {
                for(int i = 0; i < racks.size() ; i++){
                    racks.get(i).setDistance(-1);
                    if (MyUtils.checkPermissions(getActivity())) {
                        if (MyUtils.isLocationEnabled(getActivity()) && mCurrentPosition != null) {
                            double distance = Geolocation.distance(mCurrentPosition,
                                    new LatLng(racks.get(i).getLatitude(), racks.get(i).getLongitude())
                            );
                            racks.get(i).setDistance(distance);
                        }
                    }
                }
                adapter = new RacksListAdapter(racks);
                binding.recyclerView.setAdapter(adapter);
            }
        };
        liveData = model.getListOfRacks(getContext());
        liveData.observe(requireActivity(), observer);
    }


    @Override
    public void positionCallback(Location mCurrentPosition) {
            this.mCurrentPosition = new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
    }

    private void getUserPosition(){
        if (MyUtils.checkPermissions(getActivity())) {
            if (MyUtils.isLocationEnabled(getActivity())) {
                Geolocation geo = new Geolocation(getActivity(), geolocationCallback);
                geo.getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (MyUtils.checkPermissions(getActivity())) {
            getUserPosition();
        }
    }

    //Geolocation activate


}

