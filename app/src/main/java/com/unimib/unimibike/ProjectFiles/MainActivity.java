package com.unimib.unimibike.ProjectFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.CheckForInternet;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;
import com.unimib.unimibike.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private static final String MAIL = "Mail";
    private static final String PASSWORD = "Password";
    private ActivityMainBinding activity_layout;
    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SaveSharedPreference.getUserName(getApplicationContext()).length() != 0) {
            getUserRole(SaveSharedPreference.getUserName(getApplicationContext()));

        } else {
            activity_layout = ActivityMainBinding.inflate(getLayoutInflater());
            View view = activity_layout.getRoot();
            setContentView(view);
            activity_layout.accediUtente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckForInternet.check_connection((ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE))) {
                        email = activity_layout.testoEmail.getText().toString();
                        password = activity_layout.testoPassword.getText().toString();
                        if (controlla_email(email) & controlla_pass(password)) {
                            activity_layout.mainActivityRelativeLayout.setVisibility(View.VISIBLE);
                            activity_layout.pBar.setVisibility(View.VISIBLE);
                            activity_layout.testoEmail.setFocusable(false);
                            activity_layout.testoPassword.setFocusable(false);
                            activity_layout.accediUtente.setClickable(false);
                            effettua_login(email, password);
                        }
                    } else {
                        MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(MainActivity.this, R.style.Theme_MyTheme_Dialog);
                        mMaterialDialog
                                .setTitle(R.string.internet_connection_dialog_title)
                                .setMessage(R.string.check_internet)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                }
            });

        }
    }
    //questo metodo fa partire una nuova activity
    public void apr_activity(User user){
        Log.d("APRACTIVITY",user.toString());
        Intent pagina = new Intent(this, Principal.class);
        pagina.putExtra("USER-ID", user.getmId());
        pagina.putExtra("USER-MAIL", user.getEmail());
        pagina.putExtra("USER-PERISSION", user.getmRole());
        SaveSharedPreference.setUserName(getApplicationContext(),user.getEmail(),user.getmRole(),user.getmId());
        startActivity(pagina);
        finish();
    }
    public boolean controlla_email(String value){
        if(value.isEmpty()){
            activity_layout.userEmail.setError("Campo email vuoto!");
            return false;
        }
        if((value.split("@")).length == 2){
            if(value.endsWith("@campus.unimib.it") || value.endsWith("@unimib.it")){
                activity_layout.userEmail.setError(null);
                activity_layout.userEmail.setErrorEnabled(false);
                return true;
            }
        }
        activity_layout.userEmail.setError("Email non valida!");
        return false;
    }

    public boolean controlla_pass(String value){
        if(value.isEmpty()){
            activity_layout.userPassword.setError("Campo password vuoto!");
            return false;
        }
        activity_layout.userPassword.setError(null);
        return true;
    }

    public void effettua_login(String value_email, String value_password){
        activity_layout.accediUtente.setEnabled(true);
        UnimibBikeFetcher.postUserLogin(getApplicationContext(), value_email, value_password, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                returnUserIdRoute(response.getEmail());
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                activity_layout.mainActivityRelativeLayout.setVisibility(View.GONE);
                activity_layout.pBar.setVisibility(View.GONE);
                activity_layout.testoEmail.setFocusableInTouchMode(true);
                activity_layout.testoPassword.setFocusableInTouchMode(true);
                activity_layout.accediUtente.setClickable(true);
                activity_layout.userEmail.setError("email potrebbe essere errata");
                activity_layout.userPassword.setError("password potrebbe essere errata");
                activity_layout.accediUtente.setEnabled(true);
            }

        });
    }

    public void returnUserIdRoute(final String email){
        UnimibBikeFetcher.getUserId(getApplicationContext(), email, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                if(response != null) {
                    Log.d("USERIDROUTE", "onSuccess: " + response.toString());
                    apr_activity(response);
                }
                else
                    addUser(email);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                Log.d("ONERRORRETURNUSER", errorMessage);
            }
        });
    }

    public void addUser(String email){
        UnimibBikeFetcher.postAddUser(getApplicationContext(), email, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                apr_activity(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }


    public void getUserRole(String user_email){
        UnimibBikeFetcher.getUserId(getApplicationContext(), user_email, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                SaveSharedPreference.clearUserName(getApplicationContext());
                SaveSharedPreference.setUserName(getApplicationContext(),response.getEmail(),response.getmRole(),response.getmId());
                Intent mPagina = new Intent(getApplicationContext(), Principal.class);
                startActivity(mPagina);
                finish();
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }
}