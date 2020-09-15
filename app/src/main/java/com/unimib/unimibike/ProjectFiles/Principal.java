package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.MyUtils;
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

        bottomNav.setSelectedItemId(R.id.id_noleggio);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                new FrameNoleggio()).commit();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        //BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setOnNavigationItemSelectedListener(listener);
        //bottomNav.setSelectedItemId(R.id.id_noleggio);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_displayer,
                new FrameNoleggio()).commit();
    }*/
}
