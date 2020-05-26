package com.unimib.unimibike.ProjectFiles;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.unimibike.Model.Rental;
import com.unimib.unimibike.R;

import java.util.List;
public class RentalFrameAdapter extends RecyclerView.Adapter<RentalFrameAdapter.RentalFrameAdapterViewHoder>{
    private List<Rental> rentals;

    public static class RentalFrameAdapterViewHoder extends RecyclerView.ViewHolder {
        private final TextView mPartenza;
        private final TextView mArrivo;
        private final TextView mDurata;
        private final TextView mGiorno;
        public RentalFrameAdapterViewHoder(View view) {
            super(view);
            mPartenza = view.findViewById(R.id.rack_available);
            mArrivo = view.findViewById(R.id.rack_address);
            mDurata = view.findViewById(R.id.rack_building);
            mGiorno = view.findViewById(R.id.rack_distance);
        }

        public TextView getmArrivo() {
            return mArrivo;
        }

        public TextView getmDurata() {
            return mDurata;
        }

        public TextView getmGiorno() {
            return mGiorno;
        }

        public TextView getmPartenza() {
            return mPartenza;
        }
    }

    public RentalFrameAdapter(List<Rental> racks) {
        rentals = racks;
    }
    @NonNull
    @Override
    public RentalFrameAdapterViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rental_frame_adapter, parent, false);
        return new RentalFrameAdapterViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalFrameAdapterViewHoder holder, int position) {
        holder.getmArrivo().setText(rentals.get(position).getEndRack().getLocationDescription());
        holder.getmPartenza().setText(rentals.get(position).getStartRack().getLocationDescription());
        String dataRent = rentals.get(position).getStartedOn().split(" ")[0];
        String inizioRent = rentals.get(position).getStartedOn().split(" ")[1].substring(0,5);
        String fineRent = rentals.get(position).getCompletedOn().split(" ")[1].substring(0,5);
        holder.getmDurata().setText(inizioRent + " - " + fineRent);
        String nuova[] = dataRent.split("-");
        holder.getmGiorno().setText(nuova[2]+"-"+nuova[1]+"-"+nuova[0]);
    }

    @Override
    public int getItemCount() {
        if (rentals != null) {
            Log.d("totale", ""+rentals.size());
            return rentals.size();
        }
        return 0;
    }

}
