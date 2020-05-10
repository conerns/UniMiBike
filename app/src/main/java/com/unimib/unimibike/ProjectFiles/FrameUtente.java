package com.unimib.unimibike.ProjectFiles;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.R;

public class FrameUtente extends Fragment {
    private String get_email;
    private String get_role;
    private MaterialButton mAddNewBike;
    private MaterialButton mModBike;
    private MaterialButton mRemBike;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View user = inflater.inflate(R.layout.fragment_utente,container, false);
        Button get_out = (Button) user.findViewById(R.id.logout_utente);
        get_role = getArguments().getString("USER-ROLE");
        get_email = getArguments().getString("USER-MAIL");

        get_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                apr_activity();
            }
        });
        return user;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView user_mail = (TextView) getView().findViewById(R.id.credenziali);
        user_mail.setText(get_email);
        TextView user_image = (TextView) getView().findViewById(R.id.nome_utente_pagina);
        String[] value = get_email.split("\\.");
        String cambia= value[0].toUpperCase() + (value[1].toUpperCase()).charAt(0);
        user_image.setText(cambia);
        if(get_role.equals("admin")){
            mAddNewBike = view.findViewById(R.id.admin_button_bici_add);
            mAddNewBike.setVisibility(View.VISIBLE);
            mModBike = view.findViewById(R.id.admin_button_bici_mod);
            mModBike.setVisibility(View.VISIBLE);
            mRemBike = view.findViewById(R.id.admin_button_bici_remove);
            mRemBike.setVisibility(View.VISIBLE);
        }
        mModBike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                apri_modifica_posizione();
            }
        });
        mAddNewBike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                apri_aggiungi_biciletta();
            }
        });
    }

    public void apr_activity(){
        Intent mPagina = new Intent(getActivity(), MainActivity.class);
        startActivity(mPagina);
        Activity activity = getActivity();
        activity.finish();
    }

    public void apri_modifica_posizione(){
        Intent mPagina = new Intent(getActivity(), ModificaPosizione.class);
        startActivity(mPagina);
    }
    public void apri_aggiungi_biciletta(){
        Intent mPagina = new Intent(getActivity(), AggiungiNuovaBici.class);
        startActivity(mPagina);
    }
}
