package com.fmdc.library.network;

import android.content.Context;
import android.net.ConnectivityManager;

public class DetectConnection {
    public static boolean checkInternetConnection(Context context){
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isAvailable() &&
                conMan.getActiveNetworkInfo().isConnected());
    }

}
