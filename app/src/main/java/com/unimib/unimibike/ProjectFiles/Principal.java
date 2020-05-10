package com.unimib.unimibike.ProjectFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.R;

public class Principal extends AppCompatActivity {
    private String ruolo;
    private String utente;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        /*if(ruolo.equals("standard"))
                            selected = new FrameUtente();
                        else
                            selected = new FrameAdmin();*/
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

}
