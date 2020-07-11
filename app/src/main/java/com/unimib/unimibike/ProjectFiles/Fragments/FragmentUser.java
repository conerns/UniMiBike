package com.unimib.unimibike.ProjectFiles.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unimib.unimibike.ProjectFiles.AdminOperations.AdminAddedBikes;
import com.unimib.unimibike.ProjectFiles.AdminOperations.AdminAddNewBike;
import com.unimib.unimibike.ProjectFiles.AdminOperations.AdminModifyBikePosition;
import com.unimib.unimibike.ProjectFiles.AdminOperations.AdminRemoveBike;

import com.unimib.unimibike.ProjectFiles.ActivityLogin;
import com.unimib.unimibike.Util.Costants;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentUtenteBinding;

public class FragmentUser extends Fragment {
    private String get_email;
    private String get_role;
    private FragmentUtenteBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUtenteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        if(SaveSharedPreference.getUserName(getContext().getApplicationContext()).length() != 0) {
            get_email = SaveSharedPreference.getUserName(getContext().getApplicationContext());
            get_role = SaveSharedPreference.getPrefUserRole(getContext().getApplicationContext());
        }
        else {
            get_role = getArguments().getString(Costants.USER_ROLE);
            get_email = getArguments().getString(Costants.USER_MAIL);
        }
        binding.logoutUtente.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.credenziali.setText(get_email);
        String[] value = get_email.split("\\.");
        String cambia= value[0].toUpperCase() + (value[1].toUpperCase()).charAt(0);
        binding.nomeUtentePagina.setText(cambia);

        if(get_role.equals("admin")){
            binding.adminButtonBiciAdd.setVisibility(View.VISIBLE);
            binding.adminButtonBiciMod.setVisibility(View.VISIBLE);
            binding.adminButtonBiciRemove.setVisibility(View.VISIBLE);
            binding.adminButtonStorico.setVisibility(View.VISIBLE);

            binding.adminButtonBiciAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    apri_aggiungi_biciletta();
                }
            });

            binding.adminButtonBiciMod.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    apri_modifica_posizione();
                }
            });

            binding.adminButtonBiciRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    apri_remove_biciletta();
                }
            });

            binding.adminButtonStorico.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    apri_storico_biciclette_aggiunte();
                }
            });
        }

    }

    public void logoutUser(){
        SaveSharedPreference.clearUserName(getContext());
        Intent mPagina = new Intent(getActivity(), ActivityLogin.class);
        startActivity(mPagina);
        Activity activity = getActivity();
        assert activity != null;
        activity.finish();
    }

    public void apri_modifica_posizione(){
        Intent mPagina = new Intent(getActivity(), AdminModifyBikePosition.class);
        startActivity(mPagina);
    }
    public void apri_remove_biciletta(){
        Intent mPagina = new Intent(getActivity(), AdminRemoveBike.class);
        startActivity(mPagina);
    }
    public void apri_aggiungi_biciletta(){
        Intent mPagina = new Intent(getActivity(), AdminAddNewBike.class);
        startActivity(mPagina);
    }

    public void apri_storico_biciclette_aggiunte(){
        Intent mPagina = new Intent(getActivity(), AdminAddedBikes.class);
        int user_id = getActivity().getIntent().getIntExtra(Costants.USER_ID, 0);
        Log.d("FRAMEUTENTE", user_id+"");
        mPagina.putExtra(Costants.USER_ID, user_id);
        startActivity(mPagina);
    }
}
