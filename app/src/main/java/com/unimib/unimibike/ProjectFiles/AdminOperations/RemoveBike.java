package com.unimib.unimibike.ProjectFiles.AdminOperations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.ActivityRemoveBikeBinding;

public class RemoveBike extends AppCompatActivity {
    private ActivityRemoveBikeBinding binding;
    private BikesViewModel bikeViewModel;
    private MutableLiveData<Bike> bikeMutableLiveData;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemoveBikeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.sendFixedReport.setOnClickListener(new View.OnClickListener() {
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
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void removeBike(){
        binding.idRemoveBike.setError(null);
        binding.idRemoveBike.setErrorEnabled(false);
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
        final Observer<Bike> observer = new Observer<Bike>() {
            @Override
            public void onChanged(Bike bike) {
                binding.idRemoveBikeContent.setText(null);
                MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(RemoveBike.this, R.style.Theme_MyTheme_Dialog);
                mMaterialDialog
                    .setTitle(R.string.removed_bike)
                    .setMessage(getString(R.string.correct_bike_remove))
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            }
        };
        bikeMutableLiveData = bikeViewModel.removeBike(this.getApplicationContext(),
                Integer.parseInt(binding.idRemoveBikeContent.getText().toString()),
                SaveSharedPreference.getUserID(getApplicationContext()));
        bikeMutableLiveData.observe(this, observer);
    }

    private boolean controlloId() {
        if(binding.idRemoveBikeContent.getText().length() == 0){
            binding.idRemoveBike.setError("Campo richiesto!");
            return false;
        }
        return true;
    }

    private void method_called() {
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this.getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 0);
        }else{
            DialogFragment newFragment;
            newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_id_header),"Non hai dato i permessi per utilizzare la fotocamera");
            newFragment.show(getSupportFragmentManager(), "dialog");
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
