package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Rack;
import com.unimib.unimibike.ProjectFiles.Repositories.RacksRepository;

import java.util.List;

public class RacksViewModel extends ViewModel {
    private MutableLiveData<List<Rack>> listOfRacks;
    private MutableLiveData<Rack> rack;

    public MutableLiveData<List<Rack>> getListOfRacks(Context context) {
        if (listOfRacks == null)
            listOfRacks = new MutableLiveData<>();

        RacksRepository.getInstance().getRacks(context, listOfRacks);
        return listOfRacks;
    }

    public MutableLiveData<Rack> getRackById(final int rack_id, Context context){
        if (rack == null)
            rack = new MutableLiveData<>();

        RacksRepository.getInstance().getRack(context, rack, rack_id);
        return rack;
    }
}
