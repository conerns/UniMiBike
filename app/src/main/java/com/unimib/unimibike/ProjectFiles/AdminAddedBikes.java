package com.unimib.unimibike.ProjectFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.unimib.unimibike.Model.BikeHistory;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;
import com.unimib.unimibike.databinding.ActivityListaAdminHistoryAddedBikeBinding;

import java.util.List;

public class AdminAddedBikes extends AppCompatActivity {
    ActivityListaAdminHistoryAddedBikeBinding activity_layout;
    BikeAddedListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_layout = ActivityListaAdminHistoryAddedBikeBinding.inflate(getLayoutInflater());
        View view = activity_layout.getRoot();
        setContentView(view);
        getBikesHistory(getIntent().getIntExtra("user_id", 0));
    }

    public void getBikesHistory(int user_id){
        UnimibBikeFetcher.postAddedBikes(getApplicationContext(), user_id, new ServerResponseParserCallback<List<BikeHistory>>() {
            @Override
            public void onSuccess(List<BikeHistory> response) {
                Log.d("BIKEHISTORYONSUCCESS",response.toString());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                adapter = new BikeAddedListAdapter(response, getApplicationContext());
                activity_layout.recyclerView.setLayoutManager(layoutManager);
                activity_layout.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }
}
