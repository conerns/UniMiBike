package com.unimib.unimibike.ProjectFiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.ProjectFiles.ViewModels.RentalsViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.databinding.BottomSheetEndBinding;

public class BottomSheetEnd extends BottomSheetDialogFragment {
    private BottomSheetEndBinding binding;
    private RacksViewModel racksViewModel;
    private MutableLiveData<Resource<Rack>> bikeLiveData;
    private RentalsViewModel rentalsViewModel;
    private MutableLiveData<Rental> rentalMutableLiveData;
    private Rental mRental;
    public BottomSheetEnd(Rental element, Context context){
        mRental = element;
    }
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEndBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        binding.buttonUnlockBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.bikeCodeText.getText().length()!=0) {
                    functionGetRack(Integer.parseInt(binding.bikeCodeText.getText().toString()),view);
                }
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

    private void functionGetRack(int rack_id, final View view) {
        racksViewModel = new RacksViewModel();
        final Observer<Resource<Rack>> observer = new Observer<Resource<Rack>>() {
            @Override
            public void onChanged(Resource<Rack> rack) {
                if(rack.getStatusCode() == 200) {
                    DialogFragment newFragment;
                    //se ho una bici e ho posto posso lasciarla nella rastrelliera
                    if (rack.getData().getAvailableStands() > 0)
                        ending_rental(mRental, view, getFragmentManager());
                    else {
                        newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_bike_not_avaible),
                                getString(R.string.not_avaible_message));
                        newFragment.show(getFragmentManager(), "dialog");
                    }
                    dismiss();
                }else if(rack.getStatusCode() == 404){
                    binding.bikeCode.setErrorEnabled(true);
                    binding.bikeCode.setError("Inserire ID valido");
                    binding.bikeCode.clearFocus();
                }
            }
        };
        //controllo distanza dalla rastrelliera
        bikeLiveData = racksViewModel.getRackById(rack_id, getContext());
        bikeLiveData.observe(requireActivity(), observer);
    }
    private void method_called() {
        if (MyUtils.checkCameraPermission(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 0);
        }else{
            MyUtils.showCameraPermissionDeniedDialog(getActivity(), getActivity().getSupportFragmentManager());
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

    public void ending_rental(Rental rental, final View view, final FragmentManager fragmentManager){
        rentalsViewModel = new RentalsViewModel();
        final Observer<Rental> observer = new Observer<Rental>() {
            @Override
            public void onChanged(Rental rental) {
                //qui devo fare tutto
                //prima idea, inserisce il coso di fine
               // FrameNoleggio.finishRent();

                DialogFragment newFragment= MyAlertDialogFragment.newInstance(view.getContext().getString(R.string.ended_succs),
                        view.getContext().getString(R.string.rent_ended_mess));
                newFragment.show(fragmentManager, "dialog");
                dismiss();
            }
        };
        rentalMutableLiveData = rentalsViewModel.endRental(getContext(), rental.getId(), Integer.parseInt(binding.bikeCodeText.getText().toString()));
        rentalMutableLiveData.observe(requireActivity(), observer);
    }
}
