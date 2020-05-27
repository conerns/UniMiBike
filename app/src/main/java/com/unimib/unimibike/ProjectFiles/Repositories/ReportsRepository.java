package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

public class ReportsRepository {
    private static ReportsRepository instance;

    public ReportsRepository(){}

    public static synchronized ReportsRepository getInstance() {
        if (instance == null) {
            instance = new ReportsRepository();
        }
        return instance;
    }

    public void postReport(final Context context, final Report reportToSend, final MutableLiveData<Report> report){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    sendReportToDatabase(reportToSend, context, report);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void sendReportToDatabase(final Report reportToSend, final Context context, final MutableLiveData<Report> report){
        UnimibBikeFetcher.postReport(context, reportToSend, new ServerResponseParserCallback<Report>() {
            @Override
            public void onSuccess(Report response) {
                report.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }
}