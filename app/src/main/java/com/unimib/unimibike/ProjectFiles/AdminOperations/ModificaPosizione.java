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
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.ArrayList;

public class ModificaPosizione extends AppCompatActivity {
    private TextInputLayout mIDBike;
    private TextInputLayout mIDRack;
    private TextInputEditText mBikeContent;
    private TextInputEditText mRackContent;
    private Button mApply;

    private ArrayList<String> mTutteRack;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifica_posizione_bici);

        mIDBike = findViewById(R.id.id_posizione_bici);
        mBikeContent = findViewById(R.id.bike_code_text_fault);

        mIDRack = findViewById(R.id.posizione_bici_finale);
        mRackContent = findViewById(R.id.valori_rastrelliere_fine);
        mApply = findViewById(R.id.send_fixed_report);

        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIDRack.setError(null);
                mIDBike.setError(null);
                mIDRack.setErrorEnabled(false);
                mIDBike.setErrorEnabled(false);
                if(controlloId() & controlloRack()){
                    MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(ModificaPosizione.this, R.style.Theme_MyTheme_Dialog);
                    mMaterialDialog
                            .setTitle(R.string.confirm_dati)
                            .setMessage(getString(R.string.confirm_fh_modify) + mBikeContent.getText().toString()
                                    + getString(R.string.confirm_sh_modify) + mRackContent.getText().toString() + "?")
                            .setPositiveButton(getString(R.string.confirm_message), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    UnimibBikeFetcher.postModifyBike(getApplicationContext(),
                                            Integer.parseInt(mBikeContent.getText().toString()),
                                            Integer.parseInt(mRackContent.getText().toString()),
                                            SaveSharedPreference.getUserID(getApplicationContext()),
                                            new ServerResponseParserCallback<Bike>() {
                                                @Override
                                                public void onSuccess(Bike response) {
                                                    mBikeContent.setText(null);
                                                    mRackContent.setText(null);
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
                                                }

                                                @Override
                                                public void onError(String errorTitle, String errorMessage) {
                                                    mIDBike.setError(errorMessage);
                                                    mIDRack.setError(errorMessage);
                                                }
                                            }
                                    );
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
        });
        mRackContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (mBikeContent.getRight() - mBikeContent.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        method_called_onRack();
                        return true;
                    }
                }
                return false;
            }
        });
        mBikeContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (mBikeContent.getRight() - mBikeContent.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private boolean controlloRack() {
        mRackContent.setError(null);
        if(mRackContent.getText().length() == 0){
            mIDRack.setError("Campo richiesto!");
            return false;
        }
        return true;
    }

    private boolean controlloId() {
        if(mBikeContent.getText().length() == 0){
            mIDBike.setError("Campo richiesto!");
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
    }private void method_called_onRack() {
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
                   mBikeContent.setText(String.valueOf(returnValue));
                }
                break;
            }
            case (1) :{
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    int returnValue = data.getBundleExtra("data_detect").getInt("qr_code_detection");
                    mRackContent.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }
}
