package com.unimib.unimibike.ProjectFiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.databinding.BottomSheetLayoutRackBinding;

public class BottomSheetRack extends BottomSheetDialogFragment implements GeolocationCallback{
    private LatLng rack_map_position;
    private LatLng mCurrentPosition;
    private GeolocationCallback geolocationCallback;
    private RacksViewModel model;
    private MutableLiveData<Rack> liveData;
    private BottomSheetLayoutRackBinding binding;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetLayoutRackBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        geolocationCallback = this;

        getRackId(getArguments().getInt("Rack_id"), view);

        binding.buttonGoToRack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.com/maps?daddr=" + rack_map_position.latitude+","+rack_map_position.longitude;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });

        return view;
    }

    public void getRackId(int rack_id, final View view){
        model = new RacksViewModel();
        final Observer<Rack> observer = new Observer<Rack>() {
            @Override
            public void onChanged(Rack rack) {
                binding.goToRackTextviewTitle.setText(rack.getLocationDescription());
                binding.rackAddress.setText(rack.getStreetAddress());
                String available_bike = rack.getAvailableBikes()+" "+view.getContext().getString(R.string.avaible_bikes);
                binding.rackBikeAvaible.setText(available_bike);
                rack_map_position = new LatLng(rack.getLatitude(),rack.getLongitude());
                if (MyUtils.checkPermissions(getActivity()) && MyUtils.isLocationEnabled(getActivity())){
                    Geolocation geo = new Geolocation(getActivity(), geolocationCallback);
                    geo.getLastLocation();
                }
                else binding.rackDistance.setText("-");
            }
        };
        liveData = model.getRackById(rack_id, getContext());

        liveData.observe(requireActivity(), observer);
    }

    @Override
    public void positionCallback(Location mCurrentPosition) {
        this.mCurrentPosition = new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
        double distance = Geolocation.distance(new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude()), rack_map_position);
        Rack tmp = new Rack();
        tmp.setDistance(distance);
        binding.rackDistance.setText(tmp.getDistanceString());
    }
}
