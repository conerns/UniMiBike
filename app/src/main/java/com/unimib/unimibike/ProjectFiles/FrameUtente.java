package com.unimib.unimibike.ProjectFiles;

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
import com.unimib.unimibike.ProjectFiles.AdminOperations.AggiungiNuovaBici;
import com.unimib.unimibike.ProjectFiles.AdminOperations.ModificaPosizione;
import com.unimib.unimibike.ProjectFiles.AdminOperations.RemoveBike;

import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentUtenteBinding;

public class FrameUtente extends Fragment {
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
            get_role = getArguments().getString("USER-ROLE");
            get_email = getArguments().getString("USER-MAIL");
        }
        binding.logoutUtente.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                apr_activity();
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

    public void apr_activity(){
        SaveSharedPreference.clearUserName(getContext().getApplicationContext());
        Intent mPagina = new Intent(getActivity(), MainActivity.class);
        startActivity(mPagina);
        Activity activity = getActivity();
        activity.finish();
    }

    public void apri_modifica_posizione(){
        Intent mPagina = new Intent(getActivity(), ModificaPosizione.class);
        startActivity(mPagina);
    }
    public void apri_remove_biciletta(){
        Intent mPagina = new Intent(getActivity(), RemoveBike.class);
        startActivity(mPagina);
    }
    public void apri_aggiungi_biciletta(){
        Intent mPagina = new Intent(getActivity(), AggiungiNuovaBici.class);
        startActivity(mPagina);
    }

    public void apri_storico_biciclette_aggiunte(){
        Intent mPagina = new Intent(getActivity(), AdminAddedBikes.class);
        int user_id = getActivity().getIntent().getIntExtra("USER-ID", 0);
        Log.d("FRAMEUTENTE", user_id+"");
        mPagina.putExtra("user_id", user_id);
        startActivity(mPagina);
    }
}
