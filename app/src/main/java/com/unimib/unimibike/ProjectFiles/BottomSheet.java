package com.unimib.unimibike.ProjectFiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.FragmentCallback;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.databinding.BottomSheetLayoutBinding;

public class BottomSheet extends BottomSheetDialogFragment implements GeolocationCallback{
    private FragmentCallback    rentalCallback;
    private BottomSheetLayoutBinding binding;
    private BikesViewModel bikesViewModel;
    private MutableLiveData<Resource<Bike>> bikeLiveData;
    private Geolocation geo;
    private GeolocationCallback geolocationCallback;
    private LatLng mCurrentPosition;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        geolocationCallback = this;
        getUserPosition();
        binding.buttonUnlockBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bikeCode.setError(null);
                binding.bikeCode.setErrorEnabled(false);
                if(binding.bikeCodeText.getText().length()!=0) {

                    functionGetBike(Integer.parseInt(binding.bikeCodeText.getText().toString()));
                }else{
                    binding.bikeCode.setError(getString(R.string.should_not_be_empty));
                    binding.bikeCode.setErrorEnabled(true);
                }
            }
        });

        binding.bikeCodeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (binding.bikeCodeText.getRight() - binding.bikeCodeText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCameraPermission();
                        return true;
                    }
                }
                return false;
            }
        });
        binding.bikeCodeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    binding.bikeCode.setError(null);
                    binding.bikeCode.setErrorEnabled(false);
                }
            }
        });
        return view;
    }

    public BottomSheet(FragmentCallback rentalCallback){
        this.rentalCallback = rentalCallback;
    }
    private void getUserPosition(){
        if (MyUtils.checkPermissions(getActivity())) {
            if (MyUtils.isLocationEnabled(getActivity())) {
                geo = new Geolocation(getActivity(), geolocationCallback);
                geo.getLastLocation();
                Log.d("BOTTOM-POSITION", geo.toString());
            }
        }
    }

    @Override
    public void positionCallback(Location mCurrentPosition) {
        this.mCurrentPosition = new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
    }


    private void functionGetBike(int bike_id) {
        bikesViewModel = new BikesViewModel();
        final Observer<Resource<Bike>> observer = new Observer<Resource<Bike>>() {
            @Override
            public void onChanged(Resource<Bike> bike) {
                DialogFragment newFragment;
                double distance = -999;
                if(bike.getStatusCode() == 200) {
                    if (MyUtils.checkPermissions(getActivity())) {
                        if (MyUtils.isLocationEnabled(getActivity()) && mCurrentPosition != null) {
                            distance = Geolocation.distance(mCurrentPosition,
                                    new LatLng(bike.getData().getRack().getLatitude(),bike.getData().getRack().getLongitude())
                            );

                        }
                    }
                    if(distance == -999 || distance > 300){ //possiamo prenotare nel raggio di 300 metri, sono elastico
                        Log.d("DISTANCE-RACK", distance + "");
                        newFragment = MyAlertDialogFragment.newInstance(getString(R.string.too_far_from_rack),
                                getString(R.string.too_far_from_rack_text));
                        newFragment.show(getFragmentManager(), "dialog");
                    }
                    else {
                        if (bike.getData().getBikeState().getDescription().equals("Disponibile")) {
                            newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_id_header),
                                    getString(R.string.unlock_first_half_message) + bike.getData().getUnlockCode() +
                                            getString(R.string.unlock_second_half_message));
                            newFragment.show(getFragmentManager(), "dialog");

                            rentalCallback.callbackMethod(true, bike.getData());
                        } else {
                            newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_bike_not_avaible),
                                    getString(R.string.not_avaible_message));
                            newFragment.show(getFragmentManager(), "dialog");
                        }
                    }
                    dismiss();
                }
                else if(bike.getStatusCode() == 404){
                    binding.bikeCode.setErrorEnabled(true);
                    binding.bikeCode.setError(getString(R.string.insert_vaild_value));
                    binding.bikeCode.clearFocus();
                }
            }
        };
        bikeLiveData = bikesViewModel.getBike(getContext(), bike_id);

        bikeLiveData.observe(requireActivity(), observer);
    }

    private void checkCameraPermission() {
        if (MyUtils.checkCameraPermission(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 0);
        }else{
            MyUtils.showCameraPermissionDeniedDialog(getActivity(), getFragmentManager());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    int returnValue = data.getBundleExtra("data_detect").getInt("qr_code_detection");
                    binding.bikeCodeText.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }
}
