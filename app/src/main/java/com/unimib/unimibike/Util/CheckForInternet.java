package com.unimib.unimibike.Util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckForInternet {
    public static boolean check_connection(ConnectivityManager Context){
        ConnectivityManager connectivityManager = Context;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
