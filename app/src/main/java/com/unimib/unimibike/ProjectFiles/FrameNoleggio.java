package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.R;

import com.unimib.unimibike.Util.FragmentCallback;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.Util.UnimibBikeFetcher;
import com.unimib.unimibike.Util.ServerResponseParserCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FrameNoleggio extends Fragment implements OnMapReadyCallback, FragmentCallback, GeolocationCallback {
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mHashMap = new HashMap<>();
    private LatLng mCurrentPosition;
    private Button btn;
    private Button mButtonEndRental;
    private CardView mRentalCardView;
    private FragmentCallback rentalCallback;
    private GeolocationCallback geolocationCallback;
    private View view;
    private int user_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_noleggio,container, false);
        btn = (Button) view.findViewById(R.id.sblocca_bici);
        mRentalCardView = view.findViewById(R.id.rental_up_cardview);
        rentalCallback = this;
        geolocationCallback = this;
        user_id = getActivity().getIntent().getIntExtra("USER-ID", 0);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                BottomSheet bsdf = new BottomSheet(rentalCallback);
                assert getFragmentManager() != null;
                bsdf.show(getFragmentManager() ,"bottomsheetlayout");
            }
        });

        getUserPosition();

        if(SaveSharedPreference.getRentalInProgress(getActivity().getApplicationContext()) != null &&
            SaveSharedPreference.getBikeRentalInProgress(getActivity().getApplicationContext()) != null &&
            SaveSharedPreference.getUserID(getActivity().getApplicationContext()) == user_id &&
            SaveSharedPreference.getUserState(getActivity().getApplicationContext()) == 3) {
            update_view_rental_in_progress(SaveSharedPreference.getBikeRentalInProgress(getActivity().getApplicationContext()),
                    SaveSharedPreference.getRentalInProgress(getActivity().getApplicationContext()));
            Log.d("FRAMENOLEGGIOONCREATE", SaveSharedPreference.getRentalInProgress(getActivity().getApplicationContext()).toString());
            Log.d("FRAMENOLEGGIOONCREATE", SaveSharedPreference.getBikeRentalInProgress(getActivity().getApplicationContext()).toString());
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        getRacks(mMap);
        LatLng bicocca = new LatLng(45.5136609,9.211324);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bicocca));
        mMap.setMinZoomPreference(12);
        mMap.setMaxZoomPreference(17);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int rack_id = mHashMap.get(marker);
                BottomSheetRack bsr = new BottomSheetRack();
                Bundle rack_information = new Bundle();
                rack_information.putInt("Rack_id", rack_id);
                bsr.setArguments(rack_information);
                getActivity().getSupportFragmentManager().beginTransaction().add(bsr, "si").commit();
                return true;
            }
        });

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

    public void update_view_rental_in_progress(Bike bike_used, final Rental rental){
        Log.d("update_view_rental", bike_used +" ");
        TextView starting_rack = view.findViewById(R.id.rental_up_starting_rack);
        String[] splitTmp = bike_used.getRack().getLocationDescription().split(" ");
        String rack_description = getString(R.string.start_rental_rack)+splitTmp[1];
        starting_rack.setText(rack_description);

        TextView starting_time = view.findViewById(R.id.rental_up_starting_time);
        String strDate = getString(R.string.start_rental_time)+rental.getStartedOn().substring(11);
        starting_time.setText(strDate);

        mRentalCardView = view.findViewById(R.id.rental_up_cardview);
        mRentalCardView.setVisibility(View.VISIBLE);
        mButtonEndRental = view.findViewById(R.id.rental_up_button_end_procedure);
        mButtonEndRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ending_rental(rental);
            }
        });
    }


    //Server request

    public void getRacks(GoogleMap googleMap){
        UnimibBikeFetcher.getRacks(getContext(), new ServerResponseParserCallback<List<Rack>>() {
            @Override
            public void onSuccess(List<Rack> response) {
                for(Rack rack: response){
                    LatLng rackPositition = new LatLng(rack.getLatitude(),rack.getLongitude());
                    Marker m = mMap.addMarker(new MarkerOptions().position(rackPositition).title(rack.getLocationDescription()));
                    mHashMap.put(m, rack.getId());
                }

            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    public void startRental(final Bike bike_used){

        Log.d("update_view_rental", bike_used +" startRental");
        UnimibBikeFetcher.postRental(getActivity().getApplicationContext(),
                bike_used.getId(), user_id,
                new ServerResponseParserCallback<Rental>() {
                    @Override
                    public void onSuccess(Rental response) {
                        Log.d("update_view_rental", bike_used +" on_success");
                        SaveSharedPreference.setPrefRentalInProgress(getActivity().getApplicationContext(),response,bike_used);
                        update_view_rental_in_progress(bike_used, response);
                    }

                    @Override
                    public void onError(String errorTitle, String errorMessage) {
                        Log.d("update_view_rental", errorMessage);
                    }
                });
    }

    public void ending_rental(Rental rental){
        UnimibBikeFetcher.putRental(getActivity().getApplicationContext(),
                rental.getId(), 4,
                new ServerResponseParserCallback<Rental>() {
                    @Override
                    public void onSuccess(Rental response) {
                        mRentalCardView.setVisibility(View.GONE);
                        SaveSharedPreference.clearRental_in_progress(getActivity().getApplicationContext());
                    }

                    @Override
                    public void onError(String errorTitle, String errorMessage) {

                    }
                });
    }




    //Callback interface methods

    @Override
    public void callbackMethod(boolean rental_start, Bike bike_used) {
        startRental(bike_used);
    }

    @Override
    public void positionCallback(Location mCurrentPosition) {
        this.mCurrentPosition = new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
        Log.d("aa", mCurrentPosition+"");
    }
}

