package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.ArrayList;
import java.util.List;

public class AggiungiNuovaBici extends AppCompatActivity {
    private TextInputEditText editComment;
    private TextInputEditText codiceSblocco;
    private TextInputLayout mContainerEdit;
    private TextInputLayout mContainerCodice;
    private Button mApply;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_nuova_bici);
        editComment = (TextInputEditText) findViewById(R.id.valori_rastrelliere_fine);
        codiceSblocco = (TextInputEditText) findViewById(R.id.contenuto_code_bike);

        mContainerEdit = findViewById(R.id.posizione_bici_nuova);
        mContainerCodice = findViewById(R.id.id_codice_sblocco);

        codiceSblocco.setFilters((new InputFilter[] {new InputFilter.LengthFilter(4)}));
        mApply = (Button) findViewById(R.id.aggiunta_bici);
        editComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (editComment.getRight() - editComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });
        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContainerCodice.setErrorEnabled(false);
                mContainerEdit.setErrorEnabled(false);
                mContainerEdit.setError(null);
                mContainerCodice.setError(null);
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
    private void controlloPresente() {
        UnimibBikeFetcher.getRacks(getApplicationContext(), new ServerResponseParserCallback<List<Rack>>() {
            @Override
            public void onSuccess(List<Rack> response) {
                if(editComment.getText().length() != 0){
                    for(Rack elemento : response) {
                        if (elemento.getId() == Integer.parseInt(editComment.getText().toString())) {
                            Log.d("Controllo", String.valueOf(elemento.getId() == Integer.parseInt(editComment.getText().toString())));
                            if(controlloCodice()){
                                MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(AggiungiNuovaBici.this, R.style.Theme_MyTheme_Dialog);
                                mMaterialDialog
                                        .setTitle(R.string.confirm_dati)
                                        .setMessage(getString(R.string.confirm_first_half) + editComment.getText().toString()
                                                + getString(R.string.confirm_second_half) + codiceSblocco.getText().toString())
                                        .setPositiveButton("Coferma", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                UnimibBikeFetcher.postAddBike(getApplicationContext(),
                                                        SaveSharedPreference.getUserID(getApplicationContext()),
                                                        Integer.parseInt(editComment.getText().toString()),
                                                        Integer.parseInt(codiceSblocco.getText().toString()), new ServerResponseParserCallback<Bike>() {
                                                            @Override
                                                            public void onSuccess(Bike response) {
                                                                codiceSblocco.setText(null);
                                                                editComment.setText(null);
                                                                funzione_dialog();
                                                            }
                                                            @Override
                                                            public void onError(String errorTitle, String errorMessage) {

                                                            }
                                                        }
                                                );
                                            }
                                        })
                                        .setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            }
                            return;
                        }
                    }
                    mContainerEdit.setError("Valore ID non valido");
                    return;
                }
                mContainerEdit.setError("Inserire valore ID");
                return;
            }
            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    private boolean controlloCodice(){
        if(codiceSblocco.getText().length() == 0){
            mContainerCodice.setError("Codice di sblocco richiesto!");
            return false;
        }
        if(codiceSblocco.getText().length() != 4){
            mContainerCodice.setError("Il codice deve contenere 4 numeri!");
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
                    editComment.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }

}
