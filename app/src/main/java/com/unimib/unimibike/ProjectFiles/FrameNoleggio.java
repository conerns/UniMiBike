package com.unimib.unimibike.ProjectFiles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;

import com.unimib.unimibike.Util.UnimibBikeFetcher;
import com.unimib.unimibike.Util.ServerResponseParserCallback;

import java.util.HashMap;
import java.util.List;

public class FrameNoleggio extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mHashMap = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_noleggio,container, false);
        Button btn = (Button) view.findViewById(R.id.sblocca_bici);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                BottomSheet bsdf = new BottomSheet();
                assert getFragmentManager() != null;
                bsdf.show(getFragmentManager() ,"bottomsheetlayout");
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        getRacks(mMap);
        LatLng bicocca = new LatLng(45.5136609,9.211324);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bicocca));
        mMap.setMinZoomPreference(13);
        mMap.setMaxZoomPreference(17);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int rack_id = mHashMap.get(marker);
                BottomSheetRack bsr = new BottomSheetRack();
                Bundle rack_information = new Bundle();
                rack_information.putInt("Rack_id", rack_id);
                bsr.setArguments(rack_information);
                getActivity().getSupportFragmentManager().beginTransaction().add(bsr, "si").commit();
                return true;
            }
        });

    }

    public void getRacks(GoogleMap googleMap){
        UnimibBikeFetcher.getRacks(getContext(), new ServerResponseParserCallback<List<Rack>>() {
            @Override
            public void onSuccess(List<Rack> response) {
                for(Rack rack: response){
                    LatLng rackPositition = new LatLng(rack.getLatitude(),rack.getLongitude());
                    Marker m = mMap.addMarker(new MarkerOptions().position(rackPositition).title(rack.getLocationDescription()));
                    mHashMap.put(m, rack.getId());
                }

            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }


}

