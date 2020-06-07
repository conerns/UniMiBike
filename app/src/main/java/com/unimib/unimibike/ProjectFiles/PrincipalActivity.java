package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuItemView;
import com.unimib.unimibike.R;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.OnGeolocationActive;

public class PrincipalActivity extends AppCompatActivity implements OnGeolocationActive{
    private String ruolo;
    private String utente;
    private int id;
    private final String TAG = "PRINCIPAL_ACTIVITY";
    private int counter = 0;
    private OnGeolocationActive geolocationActive;
    NavController navController;
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

    @Override
    public void onGeolocationActiveCallback() {
        createView();
    }

    public void createView(){
        setContentView(R.layout.activity_principal);
        utente = getIntent().getStringExtra("USER-MAIL");
        ruolo = getIntent().getStringExtra("USER-PERISSION");
        id = getIntent().getIntExtra("USER-ID", 0);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navView.setOnNavigationItemSelectedListener(listener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.id_noleggio:
                            navController.navigate(R.id.navigation_maps);
                            break;
                        case R.id.id_rastrelliere:
                            navController.navigate(R.id.navigation_rack_list);
                            break;
                        case R.id.id_storico:
                            navController.navigate(R.id.navigation_historical_rental);
                            break;
                        case R.id.id_guasti:
                            navController.navigate(R.id.navigation_trouble);
                            break;
                        case R.id.id_utente:
                            navController.navigate(R.id.navigation_user);
                            break;
                    }
                    return true;
                }
            };
}
