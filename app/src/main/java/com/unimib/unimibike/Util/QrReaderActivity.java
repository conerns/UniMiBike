package com.unimib.unimibike.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.unimib.unimibike.databinding.ActivityQrReaderBinding;

import java.io.IOException;

//TODO change with viewBinding

public class QrReaderActivity extends AppCompatActivity {
    private ActivityQrReaderBinding binding;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.surfaceArea.setVisibility(View.VISIBLE);
        camerScan();

    }

    public void camerScan(){

        binding.surfaceArea.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }


        });

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    binding.textViewForCamera.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            Intent resultIntent = new Intent();
                            Bundle returnData = new Bundle();
                            returnData.putInt(Costants.QR_CODE_DETECTION, Integer.parseInt(qrCode.valueAt(0).displayValue));
                            resultIntent.putExtra(Costants.DATA_DETECT, returnData);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}
