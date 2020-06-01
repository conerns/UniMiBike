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
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.ActivityAggiungiNuovaBiciBinding;

import java.util.List;

public class AggiungiNuovaBici extends AppCompatActivity {
    private ActivityAggiungiNuovaBiciBinding binding;
    private RacksViewModel racksViewModel;
    private BikesViewModel bikeViewModel;
    private MutableLiveData<Resource<Bike>> bikeLiveData;
    private MutableLiveData<List<Rack>> racksliveData;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAggiungiNuovaBiciBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.contenutoCodeBike.setFilters((new InputFilter[]{new InputFilter.LengthFilter(4)}));
        binding.valoriRastrelliereFine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (binding.valoriRastrelliereFine.getRight() - binding.valoriRastrelliereFine.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCameraPermission();
                        return true;
                    }
                }
                return false;
            }
        });
        binding.aggiuntaBici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.idCodiceSblocco.setErrorEnabled(false);
                binding.posizioneBiciNuova.setErrorEnabled(false);
                binding.idCodiceSblocco.setError(null);
                binding.posizioneBiciNuova.setError(null);
                controlloPresente();
            }
        });

    }
    private void funzione_dialog() {
        MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(AggiungiNuovaBici.this, R.style.Theme_MyTheme_Dialog);
        mMaterialDialog
                .setTitle(R.string.correct_bike_add)
                .setMessage(getString(R.string.correct_bike_add_text))
                .setPositiveButton("Avanti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void controlloPresente(){
        racksViewModel = new RacksViewModel();
        final Observer<List<Rack>> observer = new Observer<List<Rack>>() {
            @Override
            public void onChanged(List<Rack> racks) {
                if(binding.valoriRastrelliereFine.getText().length() != 0){
                    for(Rack rack: racks) {
                        if (rack.getId() == Integer.parseInt(binding.valoriRastrelliereFine.getText().toString()) &
                            controlloCodice()) {
                            Log.d("Controllo", String.valueOf(binding.valoriRastrelliereFine.getId() == Integer.parseInt(binding.valoriRastrelliereFine.getText().toString())));
                            MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(AggiungiNuovaBici.this, R.style.Theme_MyTheme_Dialog);
                            mMaterialDialog
                                .setTitle(R.string.confirm_dati)
                                .setMessage(getString(R.string.confirm_first_half) + binding.valoriRastrelliereFine.getText().toString()
                                        + getString(R.string.confirm_second_half) + binding.contenutoCodeBike.getText().toString())
                                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        addBike();
                                    }
                                })
                                .setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                            return;
                        }
                    }
                    binding.posizioneBiciNuova.setError("Valore ID non valido");
                    return;
                }
                binding.posizioneBiciNuova.setError("Inserire valore ID");
                return;
            }
        };
        racksliveData = racksViewModel.getListOfRacks(this.getApplicationContext());
        racksliveData.observe(this, observer);
    }

    public void addBike(){
        bikeViewModel = new BikesViewModel();
        final Observer<Resource<Bike>> observer = new Observer<Resource<Bike>>() {
            @Override
            public void onChanged(Resource<Bike> bike) {
                if(bike.getStatusCode() == 200) {
                    binding.contenutoCodeBike.setText(null);
                    binding.valoriRastrelliereFine.setText(null);
                    funzione_dialog();
                }
            }
        };
        bikeLiveData = bikeViewModel.adminAddBike(this.getApplicationContext(),
                                                    SaveSharedPreference.getUserID(getApplicationContext()),
                                                    Integer.parseInt(binding.contenutoCodeBike.getText().toString()),
                                                    Integer.parseInt(binding.valoriRastrelliereFine.getText().toString()));
        bikeLiveData.observe(this, observer);
    }



    private boolean controlloCodice(){
        if(binding.contenutoCodeBike.getText().length() == 0){
            binding.idCodiceSblocco.setError("Codice di sblocco richiesto!");
            return false;
        }
        if(binding.contenutoCodeBike.getText().length() != 4){
            binding.idCodiceSblocco.setError("Il codice deve contenere 4 numeri!");
            return false;
        }
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
                    binding.valoriRastrelliereFine.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }

}
