package com.unimib.unimibike.ProjectFiles.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.unimibike.Model.BikeHistory;
import com.unimib.unimibike.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BikeAddedListAdapter extends RecyclerView.Adapter<BikeAddedListAdapter.BikeAddedViewHolder>{
    private List<BikeHistory> bikes;
    private LayoutInflater layoutInflater;
    public static class BikeAddedViewHolder extends RecyclerView.ViewHolder {
        private final TextView mRackBuildings;
        private final TextView mBikeId;
        private final TextView mBikeState;
        private final TextView mCreatedOn;
        private final TextView mUnlockCode;

        public BikeAddedViewHolder(View view) {
            super(view);
            mRackBuildings = view.findViewById(R.id.history_admin_added_bike_template_rack_id_textView);
            mBikeId = view.findViewById(R.id.history_admin_added_bike_template_bike_id_textView);
            mBikeState = view.findViewById(R.id.history_admin_added_bike_template_description_textView);
            mCreatedOn = view.findViewById(R.id.history_admin_added_bike_template_created_on_textView);
            mUnlockCode = view.findViewById(R.id.history_admin_added_bike_template_unlock_code_textView);
        }

        public TextView getmRackBuildings() {
            return mRackBuildings;
        }

        public TextView getmBikeId() {
            return mBikeId;
        }

        public TextView getmBikeState() {
            return mBikeState;
        }

        public TextView getmCreatedOn() {
            return mCreatedOn;
        }

        public TextView getmUnlockCode() {
            return mUnlockCode;
        }

    }

    public BikeAddedListAdapter(List<BikeHistory> bikes, Context context) {
        this.bikes = bikes;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BikeAddedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.history_admin_added_bike_template, parent, false);
        return new BikeAddedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeAddedViewHolder holder, int position) {
        holder.mRackBuildings.setText(bikes.get(position).getmRackBuildings());
        holder.mBikeState.setText(bikes.get(position).getmBikeDescription());
        holder.mUnlockCode.setText(String.valueOf(bikes.get(position).getmUnlockCode()));
        holder.mBikeId.setText(String.valueOf(bikes.get(position).getmBikeId()));
        String created_on = bikes.get(position).getmCreatedOn();

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dt.parse(created_on);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        created_on = dt.format(date);

        holder.mCreatedOn.setText(created_on);
    }


    @Override
    public int getItemCount() {
        return bikes.size();
    }
}
