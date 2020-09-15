package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.Repositories.ReportsRepository;

import java.net.ResponseCache;

public class ReportsViewModel extends ViewModel {
    private MutableLiveData<Resource<Report>> report;
    private MutableLiveData<Resource<Bike>> bike;
    public MutableLiveData<Resource<Report>> sendReport(final Context context, final Report reportToSend) {
        if (report == null)
            report = new MutableLiveData<>();

        ReportsRepository.getInstance().postReport(context, reportToSend, report);

        return report;
    }

    public MutableLiveData<Resource<Bike>> fixReport(final Context context, final int user_id, final int rack_id, final int bike_id){
        if(bike == null)
            bike = new MutableLiveData<>();
        ReportsRepository.getInstance().postFix(context, user_id, rack_id, bike_id, bike);
        return bike;
    }
}
