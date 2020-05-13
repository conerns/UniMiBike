package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.io.IOException;

public class BottomSheet extends BottomSheetDialogFragment {
    private SurfaceView         surfaceView;
    private CameraSource        cameraSource;
    private BarcodeDetector     barcodeDetector;
    private TextInputEditText   editComment;
    private Button              mButton;
    private View                w;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        w = inflater.inflate(R.layout.bottom_sheet_layout,container,false);
        editComment = w.findViewById(R.id.bike_code_text);
        mButton     = w.findViewById(R.id.button_unlock_bike);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editComment.getText().length()!=0){
                    Log.d("tag", editComment.getText().toString());
                    functionGetBike(Integer.parseInt(editComment.getText().toString()));
                }
            }
        });
        editComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view = w;
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editComment.getRight() - editComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        editComment.setVisibility(View.INVISIBLE);
                        mButton.setVisibility(View.INVISIBLE);
                        surfaceView = view.findViewById(R.id.surface_area);
                        surfaceView.setVisibility(View.VISIBLE);
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });

        return w;
    }

    private void functionGetBike(int idBike) {
        UnimibBikeFetcher.getBike(getActivity().getApplicationContext(), idBike, new ServerResponseParserCallback<Bike>() {
            @Override
            public void onSuccess(Bike response) {
                DialogFragment newFragment;
                Log.d("ehenr", response.getUnlockCode() + "");
                if(response.getBikeState().getDescription().equals("Disponibile")) {
                    newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_id_header),
                            getString(R.string.unlock_first_half_message) + response.getUnlockCode() +
                                    getString(R.string.unlock_second_half_message));
                    newFragment.show(getFragmentManager(), "dialog");
                }
                else {
                    newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_bike_not_avaible),
                            getString(R.string.not_avaible_message));
                    newFragment.show(getFragmentManager(), "dialog");
                }
                dismiss();
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    private void method_called() {
        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(getActivity().getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
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

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    editComment.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            editComment.setText(String.valueOf((qrCode.valueAt(0).displayValue)));
                            cameraSource.stop();
                            editComment.setVisibility(View.VISIBLE);
                            surfaceView.setVisibility(View.GONE);
                            mButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }
}
