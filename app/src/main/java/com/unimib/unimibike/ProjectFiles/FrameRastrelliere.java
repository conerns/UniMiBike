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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.model.LatLng;

import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.ArrayList;
import java.util.List;

public class FrameRastrelliere extends Fragment implements GeolocationCallback {
    private RecyclerView recyclerView;
    private RacksListAdapter adapter;
    private ArrayList<Rack> rackArrayList;
    private View view;
    private LatLng mCurrentPosition;
    private GeolocationCallback geolocationCallback;
    private Rack elemento_neutro;
    private int counter = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.frame_lista_rastrelliere,container, false);
        addValuesFromDB();
        geolocationCallback = this;

        getUserPosition();
        return view;
    }

    private void addValuesFromDB() {
        rackArrayList = new ArrayList<>();
        UnimibBikeFetcher.getRacks(getContext(), new ServerResponseParserCallback<List<Rack>>() {
            @Override
            public void onSuccess(List<Rack> response) {
                //proviamo a rifare da qui, e funziona
                for(int i = 0; i < response.size() ; i++){
                    elemento_neutro = response.get(i);
                    elemento_neutro.setDistance(-1);
                    if (checkPermissions())
                        if (isLocationEnabled() && mCurrentPosition != null) {
                            double distance = Geolocation.distance(mCurrentPosition,
                                    new LatLng(elemento_neutro.getLatitude(), elemento_neutro.getLongitude())
                            );
                            elemento_neutro.setDistance(distance);
                        }
                    rackArrayList.add(elemento_neutro);
                }
                recyclerView = view.findViewById(R.id.recycler_view);
                adapter = new RacksListAdapter(rackArrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
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

