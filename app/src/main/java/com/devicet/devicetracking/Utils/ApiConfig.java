package com.devicet.devicetracking.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.devicet.devicetracking.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ApiConfig {
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    static String[] permissionsRequired = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
    };
    public static GPSTracker gps;
    public static String user_location = "";
    public static double latitude1 = 0, longitude1 = 0;

    public static String getAddress(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
            }
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static void getLocation(final Activity activity)
    {
        try {
            if (ContextCompat.checkSelfPermission(activity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED)
            )
            {

                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[3])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[4])
                )
                {
                    // Show an explanation to the user asynchronously -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.location_permission))
                            .setMessage(activity.getResources().getString(R.string.location_permission_message))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE }, 0);
                                    Constant.is_permission_grant=1;

                                }
                            })
                            .create()
                            .show();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            } else {
                gps = new GPSTracker(activity);
                if (gps.canGetLocation()) {
                    user_location = gps.getAddressLine(activity);
                }
                if (gps.getIsGPSTrackingEnabled())
                {
                    latitude1 = gps.latitude;
                    longitude1 = gps.longitude;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGPSEnable(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }
}
