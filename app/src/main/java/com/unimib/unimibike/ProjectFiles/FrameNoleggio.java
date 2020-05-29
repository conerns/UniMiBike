package com.unimib.unimibike.ProjectFiles;

import android.location.Location;
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
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.ProjectFiles.ViewModels.RentalsViewModel;
import com.unimib.unimibike.R;

import com.unimib.unimibike.Util.FragmentCallback;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentNoleggioBinding;

import java.util.HashMap;
import java.util.List;

public class FrameNoleggio extends Fragment implements OnMapReadyCallback, FragmentCallback, GeolocationCallback {
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mHashMap = new HashMap<>();
    private LatLng mCurrentPosition;
    private FragmentCallback rentalCallback;
    private GeolocationCallback geolocationCallback;
    private int user_id;
    private RacksViewModel racksViewModel;
    private MutableLiveData<List<Rack>> racksMutableLiveData;
    private RentalsViewModel rentalsViewModel;
    private MutableLiveData<Rental> rentalMutableLiveData;
    private FragmentNoleggioBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoleggioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        rentalCallback = this;
        geolocationCallback = this;
        user_id = getActivity().getIntent().getIntExtra("USER-ID", 0);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        binding.sbloccaBici.setOnClickListener(new View.OnClickListener(){
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

    public void update_view_rental_in_progress(Bike bike_used, final Rental rental){
        String[] splitTmp = bike_used.getRack().getLocationDescription().split(" ");
        String rack_description = getString(R.string.start_rental_rack)+splitTmp[1];
        binding.rentalUpStartingRack.setText(rack_description);

        String strDate = getString(R.string.start_rental_time)+rental.getStartedOn().substring(11);
        binding.rentalUpStartingTime.setText(strDate);

        binding.rentalUpCardview.setVisibility(View.VISIBLE);
        binding.rentalUpButtonEndProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ending_rental(rental);
            }
        });
    }


    //Server request

    public void getRacks(GoogleMap googleMap){
        racksViewModel = new RacksViewModel();
        final Observer<List<Rack>> observer = new Observer<List<Rack>>() {
            @Override
            public void onChanged(List<Rack> racks) {
                for(Rack rack: racks){
                    LatLng rackPositition = new LatLng(rack.getLatitude(),rack.getLongitude());
                    Marker m = mMap.addMarker(new MarkerOptions().position(rackPositition).title(rack.getLocationDescription()));
                    mHashMap.put(m, rack.getId());
                }
            }
        };
        racksMutableLiveData = racksViewModel.getListOfRacks(getContext());

        racksMutableLiveData.observe(requireActivity(), observer);
    }

    public void startRental(final Bike bike_used){
        rentalsViewModel = new RentalsViewModel();
        final Observer<Rental> observer = new Observer<Rental>() {
            @Override
            public void onChanged(Rental rental) {
                SaveSharedPreference.setPrefRentalInProgress(getActivity().getApplicationContext(), rental, bike_used);
                update_view_rental_in_progress(bike_used, rental);
            }
        };
        rentalMutableLiveData = rentalsViewModel.starRental(getContext(), user_id,bike_used.getId());

        rentalMutableLiveData.observe(requireActivity(), observer);
    }

    public void ending_rental(Rental rental){
        rentalsViewModel = new RentalsViewModel();
        final Observer<Rental> observer = new Observer<Rental>() {
            @Override
            public void onChanged(Rental rental) {
                binding.rentalUpCardview.setVisibility(View.GONE);
                SaveSharedPreference.clearRental_in_progress(getActivity().getApplicationContext());
            }
        };
        rentalMutableLiveData = rentalsViewModel.endRental(getContext(), rental.getId(),5);
        rentalMutableLiveData.observe(requireActivity(), observer);
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

