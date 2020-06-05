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
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.ProjectFiles.ViewModels.RentalsViewModel;
import com.unimib.unimibike.R;

import com.unimib.unimibike.Util.CloseRentalCallback;
import com.unimib.unimibike.Util.FragmentCallback;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentNoleggioBinding;

import java.util.HashMap;
import java.util.List;

public class FrameNoleggio extends Fragment implements OnMapReadyCallback, FragmentCallback, GeolocationCallback, CloseRentalCallback {
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mHashMap = new HashMap<>();
    private LatLng mCurrentPosition;
    private FragmentCallback rentalCallback;
    private GeolocationCallback geolocationCallback;
    private CloseRentalCallback closeRentalCallback;
    private int user_id, rack_id;
    private RacksViewModel racksViewModel;
    private MutableLiveData<List<Rack>> racksMutableLiveData;
    private RentalsViewModel rentalsViewModel;
    private MutableLiveData<Rental> rentalMutableLiveData;
    private MutableLiveData<List<Rental>> activeRentalMutableLiveData;
    private FragmentNoleggioBinding binding;
    private Geolocation geo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoleggioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        rentalCallback = this;
        geolocationCallback = this;
        closeRentalCallback = this;
        //user_id = getActivity().getIntent().getIntExtra("USER-ID", 0);
        user_id = SaveSharedPreference.getUserID(getContext());
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
        getRentalInProgress();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        getRacks();
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
                geo = new Geolocation(getActivity(), geolocationCallback);
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
                startbottomSheet(rental);
            }
        });
    }


    //Server request

    public void getRacks(){
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
                binding.sbloccaBici.setVisibility(View.GONE);
                update_view_rental_in_progress(bike_used, rental);
            }
        };
        rentalMutableLiveData = rentalsViewModel.starRental(getContext(), user_id,bike_used.getId());

        rentalMutableLiveData.observe(requireActivity(), observer);
    }



    private void startbottomSheet(Rental rental) {
        BottomSheetEnd bsdf = new BottomSheetEnd(rental,getActivity().getApplicationContext(), closeRentalCallback);
        assert getFragmentManager() != null;
        bsdf.show(getFragmentManager() ,"un altro botto sheet");
    }

    private void getRentalInProgress(){
        rentalsViewModel = new RentalsViewModel();
        final Observer<List<Rental>> observer = new Observer<List<Rental>>() {
            @Override
            public void onChanged(final List<Rental> rental) {
                Log.d("FrameNoelggio", rental.get(0).toString());
                if(rental.get(0).getId() != 0) {
                    getBike(rental.get(0));
                }
                else{
                    binding.sbloccaBici.setVisibility(View.VISIBLE);
                }
            }
        };
        activeRentalMutableLiveData = rentalsViewModel.getUserRentals(getContext(), user_id, true);

        activeRentalMutableLiveData.observe(requireActivity(), observer);
    }
    //Callback interface methods

    private void getBike(final Rental rental){
        MutableLiveData<Resource<Bike>> bike;
        BikesViewModel bikesViewModel = new BikesViewModel();
        final Observer<Resource<Bike>> newObserver = new Observer<Resource<Bike>>() {
            @Override
            public void onChanged(Resource<Bike> bikeResource) {
                if(bikeResource.getStatusCode() == 200) {
                    update_view_rental_in_progress(bikeResource.getData(), rental);
                    binding.sbloccaBici.setVisibility(View.GONE);
                }
            }
        };
        bike = bikesViewModel.getBike(getContext(), rental.getBike().getId());
        bike.observe(this, newObserver);
    }
    @Override
    public void callbackMethod(boolean rental_start, Bike bike_used) {
        startRental(bike_used);
    }

    @Override
    public void positionCallback(Location mCurrentPosition) {
        this.mCurrentPosition = new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
        Log.d("aa", mCurrentPosition+"");
    }

    @Override
    public void afterRentalIsClosedCallback() {
        binding.sbloccaBici.setVisibility(View.VISIBLE);
        binding.rentalUpCardview.setVisibility(View.GONE);
    }
}

