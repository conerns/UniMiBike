package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.OnGeolocationActive;

public class Principal extends AppCompatActivity implements OnGeolocationActive {
    private String ruolo;
    private String utente;
    private int id;
    private final String TAG = "PRINCIPAL_ACTIVITY";
    private int counter = 0;
    private OnGeolocationActive geolocationActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geolocationActive = this;

        if (!isLocationEnabled() && counter == 0 && checkPermissions()) {
            Geolocation geo = new Geolocation(this, geolocationActive);
            geo.displayLocationSettingsRequest(this.getApplicationContext());
            counter++;
        } else createView();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Bundle creato = new Bundle();
                    creato.putString("USER-ROLE", ruolo);
                    creato.putString("USER-MAIL", utente);
                    creato.putInt("USER-ID", id);
                    Fragment selected = null;
                    switch (menuItem.getItemId()) {
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

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onGeolocationActiveCallback() {
        createView();
    }

    public void createView(){
        setContentView(R.layout.initial_page_bottom);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(listener);
        utente = getIntent().getStringExtra("USER-MAIL");
        ruolo = getIntent().getStringExtra("USER-PERISSION");
        id = getIntent().getIntExtra("USER-ID", 0);
        Log.d("Qui", utente + ruolo);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                new FrameNoleggio()).commit();
    }

}
