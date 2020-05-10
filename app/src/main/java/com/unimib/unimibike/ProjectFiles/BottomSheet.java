package com.unimib.unimibike.ProjectFiles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.unimibike.R;

public class BottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText editComment;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View w = inflater.inflate(R.layout.bottom_sheet_layout,container,false);
        editComment = w.findViewById(R.id.bike_code_text);
        Button prova =(Button) w.findViewById(R.id.button_unlock_bike);
        prova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("unlock-bike", "Funziona");
            }
        });
        editComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editComment.getRight() - editComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        /*BarcodeDetector detector =
                                new BarcodeDetector.Builder(getActivity().getApplicationContext())
                                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                        .build();
                        if(!detector.isOperational()){
                            Log.d("BARCODE", "BARCODE NON FUNZIONA");
                        }
                        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                        SparseArray<Barcode> barcodes = detector.detect(frame);*/
                        return true;
                    }
                }
                return false;
            }
        });
        return w;
    }
}
