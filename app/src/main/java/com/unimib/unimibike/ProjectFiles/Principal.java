package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.unimibike.ProjectFiles.Fragments.FragmentReport;
import com.unimib.unimibike.ProjectFiles.Fragments.FragmentRental;
import com.unimib.unimibike.ProjectFiles.Fragments.FragmentRacks;
import com.unimib.unimibike.ProjectFiles.Fragments.FragmentHistoricalRental;
import com.unimib.unimibike.ProjectFiles.Fragments.FragmentUser;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Costants;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.OnGeolocationActive;
@Deprecated
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

        if (!MyUtils.isLocationEnabled(this) && counter == 0 && MyUtils.checkPermissions(this)) {
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
                    creato.putString(Costants.USER_ROLE, ruolo);
                    creato.putString(Costants.USER_MAIL, utente);
                    creato.putInt(Costants.USER_ID, id);
                    Fragment selected = null;
                    switch (menuItem.getItemId()) {
                        case R.id.id_guasti:
                            selected = new FragmentReport();
                            break;
                        case R.id.id_rastrelliere:
                            selected = new FragmentRacks();
                            break;
                        case R.id.id_storico:
                            selected = new FragmentHistoricalRental();
                            break;
                        case R.id.id_utente:
                            selected = new FragmentUser();
                            break;
                        case R.id.id_noleggio:
                            selected = new FragmentRental();
                            break;

                    }
                    selected.setArguments(creato);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                            selected).commit();
                    return true;
                }
            };

    @Override
    public void onGeolocationActiveCallback() {
        createView();
    }

    public void createView(){
        setContentView(R.layout.initial_page_bottom);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(listener);
        utente = getIntent().getStringExtra(Costants.USER_MAIL);
        ruolo = getIntent().getStringExtra(Costants.USER_PERMISSION);
        id = getIntent().getIntExtra(Costants.USER_ID, 0);
        Log.d("Qui", utente + ruolo);

        bottomNav.setSelectedItemId(R.id.id_noleggio);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                new FragmentRental()).commit();
    }

}
