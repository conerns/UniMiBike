package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.FragmentCallback;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.databinding.BottomSheetLayoutBinding;

public class BottomSheet extends BottomSheetDialogFragment {
    private FragmentCallback    rentalCallback;
    private BottomSheetLayoutBinding binding;
    private BikesViewModel bikesViewModel;
    private MutableLiveData<Bike> bikeLiveData;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        binding.buttonUnlockBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.bikeCodeText.getText().length()!=0)
                    functionGetBike(Integer.parseInt(binding.bikeCodeText.getText().toString()));
            }
        });

        binding.bikeCodeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (binding.bikeCodeText.getRight() - binding.bikeCodeText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    public BottomSheet(FragmentCallback rentalCallback){
        this.rentalCallback=rentalCallback;
    }


    private void functionGetBike(int bike_id) {
        bikesViewModel = new BikesViewModel();
        final Observer<Bike> observer = new Observer<Bike>() {
            @Override
            public void onChanged(Bike bike) {
                DialogFragment newFragment;
                if(bike.getBikeState().getDescription().equals("Disponibile")) {
                    newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_id_header),
                            getString(R.string.unlock_first_half_message) + bike.getUnlockCode() +
                                    getString(R.string.unlock_second_half_message));
                    newFragment.show(getFragmentManager(), "dialog");

                    rentalCallback.callbackMethod(true, bike);
                }
                else {
                    newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_bike_not_avaible),
                            getString(R.string.not_avaible_message));
                    newFragment.show(getFragmentManager(), "dialog");
                }
                dismiss();
            }
        };
        bikeLiveData = bikesViewModel.getBike(getContext(), bike_id);

        bikeLiveData.observe(requireActivity(), observer);
    }

    private void method_called() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(getActivity().getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 0);
        }else{
            DialogFragment newFragment;
            newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_id_header),"Non hai dato i permessi per utilizzare la fotocamera");
            newFragment.show(getFragmentManager(), "dialog");
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
