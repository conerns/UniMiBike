package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
        getRacks();
        geolocationCallback = this;
        getUserPosition();
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
                    if (checkPermissions())
                        if (isLocationEnabled() && mCurrentPosition != null) {
                            double distance = Geolocation.distance(mCurrentPosition,
                                    new LatLng(racks.get(i).getLatitude(), racks.get(i).getLongitude())
                            );
                            racks.get(i).setDistance(distance);
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
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                Geolocation geo = new Geolocation(getActivity(), geolocationCallback);
                geo.getLastLocation();
            }
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getUserPosition();
        }
    }

    //Geolocation activate


}

