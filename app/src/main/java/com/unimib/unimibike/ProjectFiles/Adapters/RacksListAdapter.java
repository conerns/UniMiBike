package com.unimib.unimibike.ProjectFiles.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.R;


public class RacksListAdapter extends RecyclerView.Adapter<RacksListAdapter.RackViewHolder> {
    private List<Rack> mRacks;
    public static class RackViewHolder extends RecyclerView.ViewHolder {
        private final TextView mRackBuilding;
        private final TextView mRackAddress;
        private final TextView mRackDistance;
        private final TextView mRackAvailableBikes;
        private final Button mGoToButton;

        public RackViewHolder(View view) {
            super(view);
            mRackBuilding = view.findViewById(R.id.rack_building);
            mRackAddress = view.findViewById(R.id.rack_address);
            mRackDistance = view.findViewById(R.id.rack_distance);
            mRackAvailableBikes = view.findViewById(R.id.rack_available);
            mGoToButton = view.findViewById(R.id.button_naviga);
        }

        public TextView getRackBuilding() {
            return mRackBuilding;
        }

        public TextView getRackAddress() {
            return mRackAddress;
        }

        public TextView getRackDistance() {
            return mRackDistance;
        }

        public TextView getRackAvailableBikes() {
            return mRackAvailableBikes;
        }

        public Button getGoToButton() {
            return mGoToButton;
        }

    }

    public RacksListAdapter(List<Rack> racks) {
        mRacks = racks;
    }

    @NonNull
    @Override
    public RackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.frame_rastrelliere_template, parent, false);
        return new RackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RackViewHolder holder, int position) {
        holder.getRackBuilding().setText(mRacks.get(position).getLocationDescription());
        holder.getRackAddress().setText(mRacks.get(position).getStreetAddress());
        if(mRacks.get(position).getDistance() == -1)
            holder.getRackDistance().setText("-");
        else
            holder.getRackDistance().setText(mRacks.get(position).getDistanceString());
        holder.getRackAvailableBikes().setText(String.valueOf(mRacks.get(position).getAvailableBikes()));

        holder.getGoToButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUri = "http://maps.google.com/maps?daddr="
                        + mRacks.get(holder.getAdapterPosition()).getLatitude()
                        + "," + mRacks.get(holder.getAdapterPosition()).getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUri));
                v.getContext().startActivity(intent);
            }
        }); //funziona
    }

    @Override
    public int getItemCount() {
        if (mRacks != null) {
            Log.d("totale", ""+mRacks.size());
            return mRacks.size();
        }
        return 0;
    }


}
