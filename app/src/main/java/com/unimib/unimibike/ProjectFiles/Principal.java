package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.SaveSharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Principal extends AppCompatActivity {
    private String ruolo;
    private String utente;
    private int id;
    private final String TAG = "PRINCIPAL_ACTIVITY";
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_page_bottom);
        checkAndRequestPermissions();
        /*if (!isLocationEnabled() && counter == 0) {
            Geolocation geo = new Geolocation(this);
            geo.displayLocationSettingsRequest(this.getApplicationContext());
            counter++;
        }*/


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(listener);
        utente = getIntent().getStringExtra("USER-MAIL");
        ruolo = getIntent().getStringExtra("USER-PERISSION");
        id = getIntent().getIntExtra("USER-ID", 0);
        Log.d("Qui", utente + ruolo);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                new FrameNoleggio()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Bundle creato=new Bundle();
                creato.putString("USER-ROLE", ruolo);
                creato.putString("USER-MAIL", utente);
                creato.putInt("USER-ID", id);
                Fragment selected = null;
                switch (menuItem.getItemId()){
                    case R.id.id_guasti:
                        selected = new FrameGuasti();
                        break;
                    case R.id.id_rastrelliere:
                        selected = new FrameRastrelliere();
                        break;
                    case R.id.id_storico:
                        selected = new FrameStorico();
                        break;
                    case R.id.id_utente:
                        selected = new FrameUtente();
                        break;
                    case R.id.id_noleggio:
                        selected = new FrameNoleggio();
                        break;

                }
                selected.setArguments(creato);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                        selected).commit();
            return true;
        }
    };



    private boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "camera & location services permission granted");

                        // here you can do your logic all Permission Success Call
                        //moveToNxtScreen();

                    }
                }
            }
        }

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }


}
