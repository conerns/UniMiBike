package com.unimib.unimibike.Util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unimib.unimibike.R;

public class MyUtils {
    public static boolean checkPermissions(final Context context) {
        if(context != null)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        return false;
    }

    public static boolean isLocationEnabled(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    public static boolean checkCameraPermission(final Context context){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static void showCameraPermissionDeniedDialog(final Context context, FragmentManager fragmentManager){
        DialogFragment newFragment;
        newFragment = MyAlertDialogFragment.newInstance(context.getString(R.string.unlock_id_header),"Non hai dato i permessi per utilizzare la fotocamera");
        newFragment.show(fragmentManager, "dialog");
    }
}
