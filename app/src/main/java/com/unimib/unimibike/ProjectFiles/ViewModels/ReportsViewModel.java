package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.ProjectFiles.Repositories.ReportsRepository;

public class ReportsViewModel extends ViewModel {
    private MutableLiveData<Report> report;

    public MutableLiveData<Report> sendReport(Context context, Report reportToSend) {
        if (report == null)
            report = new MutableLiveData<>();

        ReportsRepository.getInstance().postReport(context, reportToSend, report);
        return report;
    }
}
