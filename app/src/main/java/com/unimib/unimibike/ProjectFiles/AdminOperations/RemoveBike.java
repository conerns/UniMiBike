package com.unimib.unimibike.ProjectFiles.AdminOperations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

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

public class RemoveBike extends AppCompatActivity {
    private TextInputLayout mIDBike;
    private TextInputEditText mBikeContent;
    private Button mApply;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_bike);
        mIDBike = findViewById(R.id.id_remove_bike);
        mBikeContent = findViewById(R.id.id_remove_bike_content);

        mApply = findViewById(R.id.send_fixed_report);

        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIDBike.setError(null);
                mIDBike.setErrorEnabled(false);
                if(controlloId()){
                    MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(RemoveBike.this, R.style.Theme_MyTheme_Dialog);
                    mMaterialDialog
                            .setTitle(R.string.confirm_dati)
                            .setMessage(getString(R.string.remove_bike_desc) + mBikeContent.getText().toString()
                                        + getString(R.string.remove_bike_desc_finale))
                            .setPositiveButton(getString(R.string.confirm_message), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    UnimibBikeFetcher.postRemoveBike(getApplicationContext(),
                                            Integer.parseInt(mBikeContent.getText().toString()),
                                            SaveSharedPreference.getUserID(getApplicationContext()),
                                            new ServerResponseParserCallback<Bike>() {
                                                @Override
                                                public void onSuccess(Bike response) {
                                                    mBikeContent.setText(null);
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

                                                @Override
                                                public void onError(String errorTitle, String errorMessage) {

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
        }
    }
}
