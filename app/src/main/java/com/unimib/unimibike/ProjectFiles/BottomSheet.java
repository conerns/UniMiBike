package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.FragmentCallback;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

import java.util.List;

public class BottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText   editComment;
    private Button              mButton;
    private View                w;
    private FragmentCallback    rentalCallback;
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
                        method_called();
                        return true;
                    }
                }
                return false;
            }
        });

        return w;
    }

    public BottomSheet(FragmentCallback rentalCallback){
        this.rentalCallback=rentalCallback;
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

                    rentalCallback.callbackMethod(true, response);
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
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(getActivity().getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, 0);
        }else{
            DialogFragment newFragment;
            newFragment = MyAlertDialogFragment.newInstance(getString(R.string.unlock_id_header),"Non hai dato i permessi per utilizzare la fotocamera");
            newFragment.show(getFragmentManager(), "dialog");
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
