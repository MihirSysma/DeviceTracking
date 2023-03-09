package com.devicet.devicetracking.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetStatus {

    private static InternetStatus internetStatus=new InternetStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean connected=false;


    public static InternetStatus getInternetStatus(Context ctx)
    {

        context=ctx.getApplicationContext();
        return internetStatus;
    }

    public boolean getCheckInternet()
    {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        }catch (Exception ee)
        {
            Log.v("connectivity", ee.toString());
        }
        return connected;
    }



}
