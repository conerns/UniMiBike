package com.unimib.unimibike.ProjectFiles;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.unimib.unimibike.Model.Building;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;
import com.unimib.unimibike.ProjectFiles.RacksListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FrameRastrelliere extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.frame_rastrelliere_template,container, false);
        /*LinearLayout ll = view.findViewById(R.id.Lista_rastrelliere);
        List<Rack> l = new ArrayList<>();
        Rack r = new Rack();
        r.setAddressLocality("piazza della scienza");
        r.setAvailableBikes(5);
        r.setAvailableStands(6);
        List<Building> b = new ArrayList<>();
        b.add(new Building().setName("U6");)
        r.setBuildings(b);
        r.setDistance(12.5);
        l.add(r);
        RacksListAdapter x = new RacksListAdapter(l);*/


        return view;
    }
}
