package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.Model.Resource;
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

    public void postReport(final Context context, final Report reportToSend, final MutableLiveData<Resource<Report>> report){
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

    public void postFix(final Context context,final int user_id, final int rack_id, final int bike_id, final MutableLiveData<Resource<Bike>> report){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    fixReportToDatabase(context, user_id,rack_id,bike_id, report);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }


    private void sendReportToDatabase(final Report reportToSend, final Context context, final MutableLiveData<Resource<Report>> report){
        UnimibBikeFetcher.postReport(context, reportToSend, new ServerResponseParserCallback<Report>() {
            @Override
            public void onSuccess(Report response) {
                Resource<Report> reportResource = new Resource<>();
                reportResource.setStatusCode(200);
                reportResource.setData(response);
                report.postValue(reportResource);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                Resource<Report> reportResource = new Resource<>();
                reportResource.setStatusCode(404);
                report.postValue(reportResource);
            }
        });
    }
    private void fixReportToDatabase(final Context context, final int user_id, final int rack_id, final int bike_id, final MutableLiveData<Resource<Bike>> report){
        UnimibBikeFetcher.postFixReport(context, user_id, rack_id, bike_id, new ServerResponseParserCallback<Bike>() {
            @Override
            public void onSuccess(Bike response) {
                Resource<Bike> bikeResource = new Resource<>();
                bikeResource.setStatusCode(200);
                bikeResource.setData(response);
                report.postValue(bikeResource);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                Log.d("ReportOnError", errorMessage);
                Resource<Bike> bikeResource = new Resource<>();
                bikeResource.setStatusCode(404);
                report.postValue(bikeResource);
            }
        });
    }
}
