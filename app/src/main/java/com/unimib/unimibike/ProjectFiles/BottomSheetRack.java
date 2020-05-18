package com.unimib.unimibike.ProjectFiles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Geolocation;
import com.unimib.unimibike.Util.GeolocationCallback;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

public class BottomSheetRack extends BottomSheetDialogFragment implements GeolocationCallback{
    private TextView rack_description;
    private TextView rack_distance;
    private TextView rack_position;
    private TextView rack_avaiable_bike;
    private Button rack_go_to;
    private LatLng rack_map_position;
    private LatLng mCurrentPosition;
    private GeolocationCallback geolocationCallback;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View w = inflater.inflate(R.layout.bottom_sheet_layout_rack,container,false);
        rack_description = w.findViewById(R.id.go_to_rack_textview_title);
        rack_distance = w.findViewById(R.id.rack_distance);
        rack_position = w.findViewById(R.id.rack_address);
        rack_avaiable_bike = w.findViewById(R.id.rack_bike_avaible);
        rack_go_to = w.findViewById(R.id.button_go_to_rack);
        geolocationCallback = this;

        getRackId(getArguments().getInt("Rack_id"));

        rack_go_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.com/maps?daddr=" + rack_map_position.latitude+","+rack_map_position.longitude;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });

        return w;
    }

    public void getRackId(int rack_id){
        UnimibBikeFetcher.getRack(getActivity().getApplicationContext(), rack_id, new ServerResponseParserCallback<Rack>() {
            @Override
            public void onSuccess(Rack response) {
                rack_description.setText(response.getLocationDescription());
                rack_position.setText(response.getStreetAddress());
                String avaible_bike = String.valueOf(response.getAvailableBikes())+" "+getString(R.string.avaible_bikes);
                rack_avaiable_bike.setText(avaible_bike);
                rack_map_position = new LatLng(response.getLatitude(),response.getLongitude());
                if (checkPermissions() && isLocationEnabled()){
                    Geolocation geo = new Geolocation(getActivity(), geolocationCallback);
                    geo.getLastLocation();
                }
                else rack_distance.setText("-");
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

    @Override
    public void positionCallback(Location mCurrentPosition) {
        this.mCurrentPosition = new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
        double distance = Geolocation.distance(new LatLng(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude()), rack_map_position);
        Rack tmp = new Rack();
        tmp.setDistance(distance);
        rack_distance.setText(tmp.getDistanceString());
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }
}
