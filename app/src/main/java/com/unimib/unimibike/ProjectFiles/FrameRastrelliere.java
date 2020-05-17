package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.unimib.unimibike.Model.Building;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FrameRastrelliere extends Fragment implements GeolocationCallback {
    private RecyclerView recyclerView;
    private RacksListAdapter adapter;
    private ArrayList<Rack> rackArrayList;
    private View view;
    private LatLng mCurrentPosition;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GeolocationCallback geolocationCallback;
    private Rack elemento_neutro;
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
                    double distance = Geolocation.distance(mCurrentPosition,
                            new LatLng(elemento_neutro.getLatitude(),elemento_neutro.getLongitude())
                            );
                    elemento_neutro.setDistance(distance/1000.0);
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
            } else {
                displayLocationSettingsRequest(getActivity().getApplicationContext());
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

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (((Status) status).getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("AAA", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d("AAA", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d("AAA", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("AAA", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}

