package com.unimib.unimibike.ProjectFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.ProjectFiles.ViewModels.UsersViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.CheckForInternet;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String MAIL = "Mail";
    private static final String PASSWORD = "Password";
    private ActivityMainBinding binding;
    private String email;
    private String password;
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;
    private static final String TAG = "MAIN ACTIVITY";
    private UsersViewModel usersViewModel;
    private MutableLiveData<Resource<User>> recourceUserMutableLiveData;
    private MutableLiveData<User> userMutableLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkAndRequestPermissions()) {
            userRequireLog();
        }
    }

    private void userRequireLog() {
        if(SaveSharedPreference.getPrefUserRemember(getApplicationContext())){
            afterPermission();
        }
        else{
            SaveSharedPreference.clearUserName(getApplicationContext());
            afterPermission();
        }
    }

    //questo metodo fa partire una nuova activity
    public void apr_activity(User user){
        Log.d("APRACTIVITY",user.toString());
        Intent pagina = new Intent(this, Principal.class);
        pagina.putExtra("USER-ID", user.getmId());
        pagina.putExtra("USER-MAIL", user.getEmail());
        pagina.putExtra("USER-PERISSION", user.getmRole());
        SaveSharedPreference.setUserName(getApplicationContext(),user.getEmail(),user.getmRole(),user.getmId(),user.getUserState());
        startActivity(pagina);
        finish();
    }
    public boolean controlla_email(String value){
        if(value.isEmpty()){
            binding.userEmail.setError(getString(R.string.should_not_be_empty));
            return false;
        }
        if((value.split("@")).length == 2){
            if(value.endsWith("@campus.unimib.it") || value.endsWith("@unimib.it")){
                binding.userEmail.setError(null);
                binding.userEmail.setErrorEnabled(false);
                return true;
            }
        }
        binding.userEmail.clearFocus();
        binding.userEmail.setError(getString(R.string.insert_vaild_value));
        return false;
    }

    public boolean controlla_pass(String value){
        if(value.isEmpty()){
            binding.userPassword.setError(getString(R.string.should_not_be_empty));
            binding.userPassword.clearFocus();
            return false;
        }
        binding.userPassword.setError(null);
        return true;
    }

    public void effettua_login(String value_email, String value_password){
        binding.accediUtente.setEnabled(true);
        usersViewModel = new UsersViewModel();
        final Observer<Resource<User>> observer = new Observer<Resource<User>>() {
            @Override
            public void onChanged(Resource<User> user) {
                if(user.getStatusCode() == 200)
                    returnUserIdRoute(user.getData().getEmail(), true);
                else if(user.getStatusCode() == 404){
                    binding.mainActivityRelativeLayout.setVisibility(View.GONE);
                    binding.pBar.setVisibility(View.GONE);
                    binding.testoEmail.setFocusableInTouchMode(true);
                    binding.testoPassword.setFocusableInTouchMode(true);
                    binding.accediUtente.setClickable(true);
                    binding.userEmail.setError(getString(R.string.invalid_email_value));
                    binding.userPassword.setError(getString(R.string.invlaid_password_value));
                    binding.accediUtente.setEnabled(true);
                }
            }
        };
        recourceUserMutableLiveData = usersViewModel.login(this.getApplicationContext(), value_email, value_password);
        recourceUserMutableLiveData.observe(this, observer);
    }

    public void returnUserIdRoute(final String email, final boolean call){
        usersViewModel = new UsersViewModel();
        final Observer<User> observer = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(call)
                    if (user != null) {
                        Log.d("USERIDROUTE", "onSuccess: " + user.toString());
                        apr_activity(user);
                    } else
                        addUser(email);
                else{
                    SaveSharedPreference.clearUserName(getApplicationContext());
                    apr_activity(user);
                }
            }
        };
        userMutableLiveData = usersViewModel.getUserId(this.getApplicationContext(), email);
        userMutableLiveData.observe(this, observer);
    }

    public void addUser(String email){
        usersViewModel = new UsersViewModel();
        final Observer<User> observer = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                apr_activity(user);
            }
        };
        userMutableLiveData = usersViewModel.addUser(this.getApplicationContext(), email);
        userMutableLiveData.observe(this, observer);
    }

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
            afterPermission();
        }

    }

    public void afterPermission(){
        if (SaveSharedPreference.getUserName(getApplicationContext()).length() != 0) {
            //getUserRole(SaveSharedPreference.getUserName(getApplicationContext()));
            returnUserIdRoute(SaveSharedPreference.getUserName(getApplicationContext()), false);
        } else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);
            binding.accediUtente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(binding.ricordaUtente.isChecked()) SaveSharedPreference.setLogged(getApplicationContext(), true);
                    if (CheckForInternet.check_connection((ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE))) {
                        email = binding.testoEmail.getText().toString();
                        password = binding.testoPassword.getText().toString();
                        if (controlla_email(email) & controlla_pass(password)) {
                            binding.mainActivityRelativeLayout.setVisibility(View.VISIBLE);
                            binding.pBar.setVisibility(View.VISIBLE);
                            binding.testoEmail.setFocusable(false);
                            binding.testoPassword.setFocusable(false);
                            binding.accediUtente.setClickable(false);
                            effettua_login(email, password);
                        }
                    } else {
                        MaterialAlertDialogBuilder mMaterialDialog = new MaterialAlertDialogBuilder(MainActivity.this, R.style.Theme_MyTheme_Dialog);
                        mMaterialDialog
                                .setTitle(R.string.internet_connection_dialog_title)
                                .setMessage(R.string.check_internet)
                                .setPositiveButton(R.string.confirm_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                }
            });

            binding.testoEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                        binding.userEmail.setError(null);
                }
            });

            binding.testoPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                        binding.userPassword.setError(null);
                }
            });

        }

    }
}