package com.unimib.unimibike.ProjectFiles.AdminOperations;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.ViewModels.BikesViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.ModificaPosizioneBiciBinding;

public class ModificaPosizione extends AppCompatActivity {
    private BikesViewModel bikesViewModel;
    private MutableLiveData<Resource<Bike>> mutableLiveData;
    private ModificaPosizioneBiciBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ModificaPosizioneBiciBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*mIDBike = findViewById(R.id.id_posizione_bici);
        mBikeContent = findViewById(R.id.bike_code_text_fault);

        mIDRack = findViewById(R.id.posizione_bici_finale);
        mRackContent = findViewById(R.id.valori_rastrelliere_fine);
        mApply = findViewById(R.id.send_fixed_report);*/

        binding.sendFixedReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyModify();
            }
        });
        binding.valoriRastrelliereFine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (binding.bikeCodeTextFault.getRight() - binding.bikeCodeTextFault.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        method_called_onRack();
                        return true;
                    }
                }
                return false;
            }
        });
        binding.bikeCodeTextFault.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (binding.bikeCodeTextFault.getRight() - binding.bikeCodeTextFault.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });

    }


    public void applyModify(){
        binding.posizioneBiciFinale.setError(null);
        binding.idPosizioneBici.setError(null);
        binding.posizioneBiciFinale.setErrorEnabled(false);
        binding.idPosizioneBici.setErrorEnabled(false);
        if(controlloId() & controlloRack()){
            MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(ModificaPosizione.this, R.style.Theme_MyTheme_Dialog);
            mMaterialDialog
                .setTitle(R.string.confirm_dati)
                .setMessage(getString(R.string.confirm_fh_modify) + binding.bikeCodeTextFault.getText().toString()
                        + getString(R.string.confirm_sh_modify) + binding.valoriRastrelliereFine.getText().toString() + "?")
                .setPositiveButton(getString(R.string.confirm_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        modifyBikePosition();
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

    private boolean controlloRack() {
        binding.valoriRastrelliereFine.setError(null);
        if(binding.valoriRastrelliereFine.getText().length() == 0){
            binding.posizioneBiciFinale.setError("Campo richiesto!");
            return false;
        }
        return true;
    }

    private boolean controlloId() {
        if(binding.bikeCodeTextFault.getText().length() == 0){
            binding.idPosizioneBici.setError("Campo richiesto!");
            return false;
        }
        return true;
    }


    public void modifyBikePosition(){
        bikesViewModel = new BikesViewModel();
        final Observer<Resource<Bike>> observer = new Observer<Resource<Bike>>() {
            @Override
            public void onChanged(Resource<Bike> bike) {
                if(bike.getStatusCode() == 200){
                    binding.bikeCodeTextFault.setText(null);
                    binding.valoriRastrelliereFine.setText(null);
                    MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(ModificaPosizione.this, R.style.Theme_MyTheme_Dialog);
                    mMaterialDialog
                            .setTitle(R.string.modifed_position)
                            .setMessage(getString(R.string.correct_bike_modify))
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }else if(bike.getStatusCode() == 404){
                    binding.idPosizioneBici.setError("Dati inseriti non validi");
                    binding.posizioneBiciFinale.setError("Dati inseriti non validi");
                }
            }
        };
        mutableLiveData = bikesViewModel.modifyBikePosition(this.getApplicationContext(),
                Integer.parseInt(binding.bikeCodeTextFault.getText().toString()),
                Integer.parseInt(binding.valoriRastrelliereFine.getText().toString()),
                SaveSharedPreference.getUserID(getApplicationContext()));
        mutableLiveData.observe(this, observer);
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
    private void method_called_onRack() {
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this.getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 1);
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
                    binding.bikeCodeTextFault.setText(String.valueOf(returnValue));
                }
                break;
            }
            case (1) :{
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
