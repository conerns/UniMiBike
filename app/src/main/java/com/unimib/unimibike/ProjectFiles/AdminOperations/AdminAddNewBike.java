package com.unimib.unimibike.ProjectFiles.AdminOperations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.ProjectFiles.ViewModels.RacksViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Costants;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.ActivityAggiungiNuovaBiciBinding;

import java.util.List;

public class AdminAddNewBike extends AppCompatActivity {
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

        binding.valoriRastrelliereFine.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    binding.posizioneBiciNuova.setErrorEnabled(false);
                    binding.posizioneBiciNuova.setError(null);
                }
            }
        });
        binding.contenutoCodeBike.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    binding.idCodiceSblocco.setErrorEnabled(false);
                    binding.idCodiceSblocco.setError(null);
                }
            }
        });
    }
    private void funzione_dialog() {
        MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(AdminAddNewBike.this, R.style.Theme_MyTheme_Dialog);
        mMaterialDialog
                .setTitle(R.string.correct_bike_add)
                .setMessage(getString(R.string.correct_bike_add_text))
                .setPositiveButton(R.string.confirm_message, new DialogInterface.OnClickListener() {
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
                if(checkRackValue()){
                    if(checkRackExistence(racks) && controlloCodice()) {
                        MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(AdminAddNewBike.this, R.style.Theme_MyTheme_Dialog);
                        mMaterialDialog
                                .setTitle(R.string.confirm_dati)
                                .setMessage(getString(R.string.confirm_first_half) + binding.valoriRastrelliereFine.getText().toString()
                                        + getString(R.string.confirm_second_half) + binding.contenutoCodeBike.getText().toString())
                                .setPositiveButton(R.string.confirm_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        addBike();
                                    }
                                })
                                .setNegativeButton(R.string.cancel_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                        return;
                    }
                }
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

    public boolean checkRackValue(){
        if(binding.valoriRastrelliereFine.getText().length() == 0){
            binding.posizioneBiciNuova.setError(getString(R.string.should_not_be_empty));
            binding.posizioneBiciNuova.setErrorEnabled(true);
            binding.posizioneBiciNuova.clearFocus();
            binding.idCodiceSblocco.clearFocus();
            return false;
        }
        binding.posizioneBiciNuova.setError(null);
        binding.posizioneBiciNuova.setErrorEnabled(false);
        return true;
    }

    public boolean checkRackExistence(final List<Rack> racks){
        for(Rack rack: racks) {
            if (rack.getId() == Integer.parseInt(binding.valoriRastrelliereFine.getText().toString())) {
                binding.posizioneBiciNuova.setError(null);
                binding.posizioneBiciNuova.setErrorEnabled(false);
                return true;
            }
        }
        binding.posizioneBiciNuova.setError(getString(R.string.should_not_be_empty));
        binding.posizioneBiciNuova.setErrorEnabled(true);
        binding.posizioneBiciNuova.clearFocus();
        binding.idCodiceSblocco.clearFocus();
        return false;
    }

    private boolean controlloCodice(){
        if(binding.contenutoCodeBike.getText().length() == 0){
            binding.idCodiceSblocco.setError(getString(R.string.should_not_be_empty));
            binding.idCodiceSblocco.setErrorEnabled(true);
            binding.idCodiceSblocco.clearFocus();
            binding.posizioneBiciNuova.clearFocus();
            return false;
        }
        if(binding.contenutoCodeBike.getText().length() != 4){
            binding.idCodiceSblocco.setError(getString(R.string.only_four_digits));
            binding.idCodiceSblocco.setErrorEnabled(true);
            binding.idCodiceSblocco.clearFocus();
            binding.posizioneBiciNuova.clearFocus();
            return false;
        }
        binding.idCodiceSblocco.setError(null);
        binding.idCodiceSblocco.setErrorEnabled(false);
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
                    int returnValue = data.getBundleExtra(Costants.DATA_DETECT).getInt(Costants.QR_CODE_DETECTION);
                    binding.valoriRastrelliereFine.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }

}
