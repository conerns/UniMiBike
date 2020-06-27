package com.unimib.unimibike.ProjectFiles.AdminOperations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.ActivityRemoveBikeBinding;

public class RemoveBike extends AppCompatActivity {
    private ActivityRemoveBikeBinding binding;
    private BikesViewModel bikeViewModel;
    private MutableLiveData<Resource<Bike>> bikeMutableLiveData;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemoveBikeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.sendRemoveBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               removeBike();
            }
        });
        binding.idRemoveBikeContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (binding.idRemoveBikeContent.getRight() - binding.idRemoveBikeContent.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCameraPermission();
                        return true;
                    }
                }
                return false;
            }
        });

        binding.idRemoveBikeContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    binding.idRemoveBike.setError(null);
                    binding.idRemoveBike.setErrorEnabled(false);
                }
            }
        });
    }

    public void removeBike(){
        if(controlloId()){
            MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(RemoveBike.this, R.style.Theme_MyTheme_Dialog);
            mMaterialDialog
                .setTitle(R.string.confirm_dati)
                .setMessage(getString(R.string.remove_bike_desc) + binding.idRemoveBikeContent.getText().toString()
                        + getString(R.string.remove_bike_desc_finale))
                .setPositiveButton(getString(R.string.confirm_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeBikeIntoServer();
                    }
                })
                .setNegativeButton(getString(R.string.cancel_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
        }
    }

    public void removeBikeIntoServer(){
        bikeViewModel = new BikesViewModel();
        final Observer<Resource<Bike>> observer = new Observer<Resource<Bike>>() {
            @Override
            public void onChanged(Resource<Bike> bike) {
                if(bike.getStatusCode() == 200) {
                    binding.idRemoveBikeContent.setText(null);
                    MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(RemoveBike.this, R.style.Theme_MyTheme_Dialog);
                    mMaterialDialog
                            .setTitle(R.string.removed_bike)
                            .setMessage(getString(R.string.correct_bike_remove))
                            .setPositiveButton(R.string.confirm_message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else if(bike.getStatusCode() == 404){
                    binding.idRemoveBike.setError(getString(R.string.insert_vaild_value));
                    binding.idRemoveBike.setErrorEnabled(true);
                    binding.idRemoveBike.clearFocus();
                }

            }
        };
        bikeMutableLiveData = bikeViewModel.removeBike(this.getApplicationContext(),
                Integer.parseInt(binding.idRemoveBikeContent.getText().toString()),
                SaveSharedPreference.getUserID(getApplicationContext()));
        bikeMutableLiveData.observe(this, observer);
    }

    private boolean controlloId() {
        if(binding.idRemoveBikeContent.getText().length() == 0){
            binding.idRemoveBike.setError(getString(R.string.should_not_be_empty));
            binding.idRemoveBike.setErrorEnabled(true);
            binding.idRemoveBike.clearFocus();
            return false;
        }
        binding.idRemoveBike.setErrorEnabled(false);
        binding.idRemoveBike.setError(null);
        return true;
    }

    private void checkCameraPermission() {
        if (MyUtils.checkCameraPermission(this)) {
            Intent intent = new Intent(this.getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 0);
        }else{
            MyUtils.showCameraPermissionDeniedDialog(this, getSupportFragmentManager());
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
                    binding.idRemoveBikeContent.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }
}
