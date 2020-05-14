package com.unimib.unimibike.Util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.unimib.unimibike.R;

import java.io.IOException;

public class QrReaderActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);
        activity = this;
        surfaceView = (SurfaceView) findViewById(R.id.surface_area2);
        surfaceView.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.textViewForCamera);
        camerScan();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("BOTTOMSHEETIF", "on request permission result");
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("flag", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Log.d("ELSE", "qui ci arriva");
                    //overridePendingTransition(0, 0);
                    finish();
                    //overridePendingTransition(0, 0);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public void camerScan(){
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, 1);
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }


        });

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    textView.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            Intent resultIntent = new Intent();
                            Bundle returnData = new Bundle();
                            returnData.putInt("qr_code_detection", Integer.parseInt(qrCode.valueAt(0).displayValue));
                            resultIntent.putExtra("flag", false);
                            resultIntent.putExtra("data_detect", returnData);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}
