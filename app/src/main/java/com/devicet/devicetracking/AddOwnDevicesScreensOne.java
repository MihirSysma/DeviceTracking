package com.devicet.devicetracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devicet.devicetracking.Models.DetectionModel;
import com.devicet.devicetracking.Models.GetBrands;
import com.devicet.devicetracking.Models.GetDevice;
import com.devicet.devicetracking.Models.GetDeviceSubList;
import com.devicet.devicetracking.Models.GetModel;
import com.devicet.devicetracking.Models.GetModelSub;
import com.devicet.devicetracking.Models.GetSubBrands;
import com.devicet.devicetracking.Models.StatusModel;
import com.devicet.devicetracking.Utils.ApiConfig;
import com.devicet.devicetracking.Utils.EndPoints;
import com.devicet.devicetracking.Utils.GPSTracker;
import com.devicet.devicetracking.Utils.RetrofitSingleton;
import com.devicet.devicetracking.Utils.SharedPreferenceHelper;
import com.devicet.devicetracking.Utils.isEmailValid;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddOwnDevicesScreensOne extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    static String strModel, strOs, strToken, tk, Uid;
    String[] mobielStatus = {"Select Mobile Status", "Active", "Deactivate", "Reactivate", "Stolen", "Lost", "Damaged"};
    static String strLat = "22.7196", strLng = "75.8577", netName, NetWStatusId, NetworkName;
    DatePickerDialog picker;
    LinearLayoutCompat lay_other, lay_add;
    RecyclerView networkList;

    List<GetDeviceSubList> getDeviceList;
    AlertDialog alertDialog;
    List<GetSubBrands> BList;
    List<GetModelSub> getModelSubList;
    AppCompatEditText etxImeiNumber, etxImeiNumber_2, etxSimNumber, etxOs, etxDt, etxGps, etxManuSerialNumber, etxMarkComplience, etxEmailId;
    AppCompatSpinner etxModelName, etxDeviceType, etxMobileStatus;
    AppCompatTextView txtDeviceName, brand_txt, model_txt, network_text, network_text_2;

    StatsuAdapter networkAdapter;
    List<StatusModel> statusModelList;
    Double saveLatitude, saveLongitude;
    static int dId, bId, mmId;
    TextView title;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_own_device);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
        }

        //GET GPS Current start
        ApiConfig.getLocation(AddOwnDevicesScreensOne.this);

        gps = new GPSTracker(AddOwnDevicesScreensOne.this);
        saveLatitude = gps.latitude;
        saveLongitude = gps.longitude;

        if (gps.getIsGPSTrackingEnabled()) {
            saveLatitude = gps.latitude;
            saveLongitude = gps.longitude;
        }


        //GET GPS Current end

        statusModelList = new ArrayList<>();
        title = findViewById(R.id.title);
        title.setText("Add this mobile device");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDeviceList = new ArrayList<>();
        BList = new ArrayList<>();
        getModelSubList = new ArrayList<>();

        txtDeviceName = findViewById(R.id.device_text);
        txtDeviceName.setText("Android");
        brand_txt = findViewById(R.id.brand_txt);
        model_txt = findViewById(R.id.model_text);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
        }
        strToken = SharedPreferenceHelper.getPrefTokenKey(AddOwnDevicesScreensOne.this, "token", "tk");
        Uid = SharedPreferenceHelper.getUid(AddOwnDevicesScreensOne.this, "UID", "0");
        if (strToken != null & Uid != null) {
            tk = strToken;
        } else {
            Toast.makeText(this, "Please try again. Something went wrong", Toast.LENGTH_SHORT).show();
        }


        network_text = findViewById(R.id.network_text);
        network_text.setOnClickListener(v -> NetWorkPopup(network_text));

        network_text_2 = findViewById(R.id.network_text_2);
        network_text_2.setOnClickListener(v -> NetWorkPopup(network_text_2));


        etxImeiNumber = findViewById(R.id.imei_number);
        etxImeiNumber_2 = findViewById(R.id.imei_number_2);
        etxSimNumber = findViewById(R.id.sim_number);
        etxOs = findViewById(R.id.operating_system);
        //   etxDt=findViewById(R.id.date_time);
        etxGps = findViewById(R.id.gps_location);
        etxManuSerialNumber = findViewById(R.id.manu_serial_number);
        etxMarkComplience = findViewById(R.id.mark_of_comp);
        etxEmailId = findViewById(R.id.email_id);


        //etxModelName = findViewById(R.id.model_name);
        /// etxDeviceType=findViewById(R.id.device_type);
        etxMobileStatus = findViewById(R.id.mobile_statuss);


        lay_add = findViewById(R.id.ly_add_device);
        lay_add.setVisibility(View.VISIBLE);
        lay_other = findViewById(R.id.ly_serial_number);

        String kk = SharedPreferenceHelper.getKey(AddOwnDevicesScreensOne.this, "key", "0");
        String dvId = getDeviceId(AddOwnDevicesScreensOne.this);
        String sim = getSimNumber(AddOwnDevicesScreensOne.this);
        String imeiNumber = getImeiNumber(AddOwnDevicesScreensOne.this);
        String imeiNumber_2 = getImeiNumber_2(AddOwnDevicesScreensOne.this);


        if (imeiNumber.equals("imei")) {
            etxImeiNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imeis = SharedPreferenceHelper.getStatus(AddOwnDevicesScreensOne.this, "imei", "ok");
                    Log.d("TAG", "gst" + imeis);
                    if (imeis.equals("2")) {
                        etxImeiNumber.setFocusableInTouchMode(true);
                        etxImeiNumber.setText(null);
                    } else {
                        InfoDialogImei();
                        etxImeiNumber.setFocusableInTouchMode(false);
                    }
                }
            });
        } else {
            etxImeiNumber.setFocusableInTouchMode(false);
            etxImeiNumber.setText(imeiNumber);
        }

        if (imeiNumber_2.equals("imei")) {
            etxImeiNumber_2.setOnClickListener(v -> {
                String imeis = SharedPreferenceHelper.getStatus(AddOwnDevicesScreensOne.this, "imei2", "ok");
                Log.d("TAG", "gst" + imeis);
                if (imeis.equals("3")) {
                    etxImeiNumber_2.setFocusableInTouchMode(true);
                    etxImeiNumber_2.setText(null);
                } else {
                    InfoDialogImei_2();
                    etxImeiNumber_2.setFocusableInTouchMode(false);
                }
            });
        } else {
            etxImeiNumber_2.setFocusableInTouchMode(false);
            etxImeiNumber_2.setText(imeiNumber_2);
        }


//        if(sim.equals("sim")){
//            etxSimNumber.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String sim=SharedPreferenceHelper.getStatus(AddOwnDevicesScreensOne.this,"sim","ok");
//                    Log.d("TAG", "gst"+sim);
//                    if(sim.equals("1")){
//                        etxSimNumber.setText(null);
//                        etxSimNumber.setFocusableInTouchMode(true);
//                    }else{
//                        etxSimNumber.setFocusableInTouchMode(false);
//                        InfoDialogSim();
//                    }
//                }
//            });
//        }else {
//            etxSimNumber.setFocusableInTouchMode(false);
//            etxSimNumber.setText(imeiNumber);
//        }

        //   etxSimNumber.setText(sim);
        brand_txt.setText(Build.BRAND);
        model_txt.setText(Build.MODEL);
        etxOs.setText(dvId);
        etxManuSerialNumber.setText(Build.ID);


        etxMobileStatus.setOnItemSelectedListener(this);
        ArrayAdapter mType = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mobielStatus);
        mType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etxMobileStatus.setAdapter(mType);


        strModel = Build.SERIAL;
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        findViewById(R.id.submit_add_device).setOnClickListener(v -> {
            if (txtDeviceName.getText().toString().isEmpty()) {
                Toast.makeText(AddOwnDevicesScreensOne.this, "Select Device Type", Toast.LENGTH_SHORT).show();
            } else if (brand_txt.getText().toString().isEmpty()) {
                Toast.makeText(AddOwnDevicesScreensOne.this, "Select Brand Type", Toast.LENGTH_SHORT).show();
            } else if (model_txt.getText().toString().isEmpty()) {
                Toast.makeText(AddOwnDevicesScreensOne.this, "Select Model Type", Toast.LENGTH_SHORT).show();
            }/*else if(etxImeiNumber.getText().toString().isEmpty()){
               etxImeiNumber.setError("Field empty");
           }*/ else if (etxSimNumber.getText().toString().isEmpty()) {
                etxSimNumber.setError("Field empty");
            }/*else if(etxGps.getText().toString().isEmpty()){
                etxGps.setError("Field empty");
            }*/ else {
                String imei = etxImeiNumber.getText().toString();
                String imei2 = etxImeiNumber_2.getText().toString();
                // msgAPi("msg");

                Log.d("Data", "Valuess" + tk + "==" + Integer.parseInt(Uid) + "==" + txtDeviceName.getText().toString() + "==" +
                        brand_txt.getText().toString() + "==" + model_txt.getText().toString() + "==" + NetworkName + "==" + imei +
                        "==" + etxSimNumber.getText().toString() + "==" + etxGps.getText().toString() + "==" + etxManuSerialNumber.getText().toString() + "=="
                        + etxOs.getText().toString() + "==" + strLat + "==" + strLng);
                detectionPost(tk, Integer.parseInt(Uid), txtDeviceName.getText().toString(), brand_txt.getText().toString(), model_txt.getText().toString(), network_text.getText().toString(), imei,
                        imei2, etxSimNumber.getText().toString(), etxGps.getText().toString(), etxManuSerialNumber.getText().toString(), etxOs.getText().toString(), strLat, strLng);
            }
        });
    }


    private void detectionPost(String token, int strUId, String dOS, String brand, String model, String networkname, String imei, String imei2, String simN, String gpsL, String manuSerial, String deviceId, String lt, String lng) {
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<DetectionModel> headmodel = requestInterface.detections(token, brand, model, dOS, imei, imei2, lt, lng, strUId, "1", simN, manuSerial,
                networkname, deviceId, gpsL, "1");
        headmodel.enqueue(new Callback<DetectionModel>() {
            @Override
            public void onResponse(Call<DetectionModel> call, Response<DetectionModel> response) {
                if (response.code() == 403) {
                    RetrofitSingleton.hideDialog();
                    String msg = "Having some issues from the server. Re-login the application";
                    Toast toast = Toast.makeText(AddOwnDevicesScreensOne.this, Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                } else {
                    try {
                        if (response.body() != null) {
                            String msg = response.body().getApiMessage();
                            int status = response.body().getApiStatus();
                            if (status == 1) {
                                msgAPi(msg);
                                //  Toast.makeText(AddOwnDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                String msgs = String.valueOf(Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"));
                                msgAPi(msgs);
                                // Toast toast = Toast.makeText(AddOwnDevicesScreensOne.this, Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
                                //  toast.setGravity(Gravity.TOP, 0, 0);
                                //  toast.show();
                                // Toast.makeText(AddOwnDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ff) {
                        ff.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DetectionModel> call, Throwable t) {
                Toast.makeText(AddOwnDevicesScreensOne.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v("TAG", longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v("TAG", latitude);

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            etxGps.setText(cityName);

            Log.d("TAG", "onLocationChanged: " + s);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("TAG", "getDeviceId_Q: " + deviceId);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Log.d("TAG", "sim_serial_number: " + mTelephony.getSimSerialNumber());
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
                Log.d("TAG", "getDeviceId: " + deviceId);
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.d("TAG", "getDeviceId_android_id: " + deviceId);
            }
        }
        return deviceId;
    }

    @SuppressLint("HardwareIds")
    public static String getSimNumber(Context context) {
        String simNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            simNumber = "sim";
            Log.d("TAG", "getDeviceId_Q: " + simNumber);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                simNumber = mTelephony.getSimSerialNumber();
            } else {
                simNumber = "sim";
                Log.d("TAG", "getDeviceId_android_id: " + simNumber);
            }
        }
        return simNumber;
    }


    @SuppressLint({"HardwareIds", "NewApi"})
    public static String getImeiNumber(Context context) {
        String imeiNumber = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imeiNumber = "imei";
//            imeiNumber = Settings.Secure.getString(
//                    context.getContentResolver(),
//                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                imeiNumber = mTelephony.getImei();
            } else {
                imeiNumber = "imei";
                Log.d("TAG", "getDeviceId_android_id: " + imeiNumber);
            }
        }
        return imeiNumber;
    }

    @SuppressLint({"HardwareIds", "NewApi"})
    public static String getImeiNumber_2(Context context) {
        String imeiNumber = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imeiNumber = "imei";
//            imeiNumber = Settings.Secure.getString(
//                    context.getContentResolver(),
//                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                imeiNumber = mTelephony.getDeviceId();


            } else {
                imeiNumber = "imei";
                Log.d("TAG", "getDeviceId_android_id: " + imeiNumber);
            }
        }
        return imeiNumber;
    }


    private void InfoDialogImei() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddOwnDevicesScreensOne.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.info_dialog, null);
        TextView closeBtn = vieww.findViewById(R.id.close);
        TextView knowBtn = vieww.findViewById(R.id.know_more);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this, "imei", "2");

            }
        });
        knowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this, "imei", "2");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/about/versions/10/privacy/changes#non-resettable-device-ids"));
                startActivity(browserIntent);
                alertDialog.dismiss();

            }
        });
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }

    private void InfoDialogImei_2() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddOwnDevicesScreensOne.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.info_dialog, null);
        TextView closeBtn = vieww.findViewById(R.id.close);
        TextView knowBtn = vieww.findViewById(R.id.know_more);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this, "imei2", "3");

            }
        });
        knowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this, "imei2", "3");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/about/versions/10/privacy/changes#non-resettable-device-ids"));
                startActivity(browserIntent);
                alertDialog.dismiss();

            }
        });
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }


    private void InfoDialogSim() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddOwnDevicesScreensOne.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.info_dialog, null);
        TextView closeBtn = vieww.findViewById(R.id.close);
        TextView knowBtn = vieww.findViewById(R.id.know_more);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this, "sim", "1");
            }
        });
        knowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this, "sim", "1");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/about/versions/10/privacy/changes#non-resettable-device-ids"));
                startActivity(browserIntent);
                alertDialog.dismiss();
//                if(type.equals("sim")){
//
//                }else if(type.equals("imei")){
//                    alertDialog.dismiss();
//                    SharedPreferenceHelper.setStatus(AddOwnDevicesScreensOne.this,"imei","2");
//                }

            }
        });
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }

    private void msgAPi(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddOwnDevicesScreensOne.this);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.info_msg, null);
        TextView closeBtn = vieww.findViewById(R.id.close);//know_more
        TextView know_more = vieww.findViewById(R.id.know_more);
        know_more.setVisibility(View.GONE);
        TextView txt = vieww.findViewById(R.id.txt);
        txt.setText(msg);
        closeBtn.setTextColor(getColor(R.color.purple_500));
        closeBtn.setText("Got it");
        closeBtn.setGravity(Gravity.CENTER);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent i = new Intent(AddOwnDevicesScreensOne.this, DeviceListScreens.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }


//    private void getNetWork(String tk,AppCompatTextView txt) {
//        RetrofitSingleton.showDialog(AddOwnDevicesScreensOne.this);
//        Retrofit retrofit1 = RetrofitSingleton.getClient();
//        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
//        final Call<GetDevice> headmodel= requestInterface.getDevice(tk);
//        headmodel.enqueue(new Callback<GetDevice>() {
//            @Override
//            public void onResponse( Call<GetDevice> call, Response<GetDevice> response) {
//                RetrofitSingleton.hideDialog();
//                try{
//                    if(response.body()!=null){
//                        String msg=response.body().getApiMessage();
//                        int status=response.body().getApiStatus();
//                        if(status == 1) {
//                            List<GetDeviceSubList> resp1 = response.body().getData();
//                            if(resp1!=null){
//                                networkAdapter = new NetworkAdapter(resp1,getApplicationContext(),txt);
//                                networkList.setAdapter(networkAdapter);
//                            }
//
//
//                            // Toast.makeText(AddAnotherDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(AddOwnDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }catch (Exception ff) { ff.printStackTrace(); }
//            }@Override
//            public void onFailure(Call<GetDevice> call, Throwable t) {
//                RetrofitSingleton.hideDialog();
//                Toast.makeText(AddOwnDevicesScreensOne.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private void NetWorkPopup(AppCompatTextView txtDeviceName) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(AddOwnDevicesScreensOne.this);
//        dialog.setCancelable(true);
//        LayoutInflater inflater = getLayoutInflater();
//        final View vieww = inflater.inflate(R.layout.device_dialog, null);
//        SearchView searchView=vieww.findViewById(R.id.search_view);
//        networkList=vieww.findViewById(R.id.device_list);
//        networkList.setLayoutManager(new LinearLayoutManager(AddOwnDevicesScreensOne.this));
//        networkList.addItemDecoration(new DividerItemDecoration(networkList.getContext(), DividerItemDecoration.VERTICAL));
//        getNetWork(tk,txtDeviceName);
//        dialog.setView(vieww);
//        dialog.setCancelable(true);
//
//        alertDialog = dialog.create();
//        alertDialog.show();
//    }
//    class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.MyStateHolder>{
//
//        List<GetDeviceSubList> deviceList;
//        Context context;
//        AppCompatTextView appCompatTextView;
//
//        public NetworkAdapter(List<GetDeviceSubList> deviceList, Context context,AppCompatTextView appCompatTextView) {
//            this.deviceList = deviceList;
//            this.context = context;
//            this.appCompatTextView=appCompatTextView;
//        }
//
//        @NonNull
//        @Override
//        public NetworkAdapter.MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View v= LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_itemsss,parent,false);
//            return new NetworkAdapter.MyStateHolder(v);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull NetworkAdapter.MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
//            final GetDeviceSubList gd=deviceList.get(position);
//            holder.device_name.setText(gd.getName());
//
//            holder.select_device.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    final GetDeviceSubList deviveM=deviceList.get(position);
//                    Log.d("TAG", "onClick: "+deviveM.getId());
//                    dId=deviveM.getId();
//                    netName=deviveM.getName();
//                    appCompatTextView.setText(netName);
//                    txtDeviceName.setText(deviveM.getName());
//                    alertDialog.dismiss();
//
//                }
//            });
//
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return deviceList.size();
//        }
//
//        class MyStateHolder extends RecyclerView.ViewHolder {
//            TextView device_name;
//            LinearLayoutCompat select_device;
//
//            public MyStateHolder(@NonNull View itemView) {
//                super(itemView);
//                device_name=itemView.findViewById(R.id.device_name);
//                select_device=itemView.findViewById(R.id.select_device);
//
//            }
//        }
//    }

    private void toList(AppCompatTextView txt) {

        StatusModel statusModel = new StatusModel();
        statusModel.setId("1");
        statusModel.setName("MTN");
        statusModelList.add(statusModel);

        StatusModel statusModel1 = new StatusModel();
        statusModel1.setId("2");
        statusModel1.setName("Cell C");
        statusModelList.add(statusModel1);

        StatusModel statusModel2 = new StatusModel();
        statusModel2.setId("3");
        statusModel2.setName("Vodacom");
        statusModelList.add(statusModel2);

        StatusModel statusModel3 = new StatusModel();
        statusModel3.setId("4");
        statusModel3.setName("Telkom");
        statusModelList.add(statusModel3);

        StatusModel statusModel4 = new StatusModel();
        statusModel4.setId("5");
        statusModel4.setName("Other");
        statusModelList.add(statusModel4);

        networkAdapter = new StatsuAdapter(statusModelList, getApplicationContext(), txt);
        networkList.setAdapter(networkAdapter);


    }

    class StatsuAdapter extends RecyclerView.Adapter<StatsuAdapter.MyStateHolder> {

        List<StatusModel> bList;
        Context context;
        AppCompatTextView txt;
        String im, mfg;

        public StatsuAdapter(List<StatusModel> deviceList, Context context, AppCompatTextView txt) {
            this.bList = deviceList;
            this.context = context;
            this.txt = txt;
        }

        @NonNull
        @Override
        public StatsuAdapter.MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_itemss, parent, false);
            return new StatsuAdapter.MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StatsuAdapter.MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final StatusModel gd = bList.get(position);
            holder.device_name.setText(gd.getName());

            holder.select_device.setOnClickListener(view -> {
                final StatusModel deviveM = bList.get(position);
                NetWStatusId = deviveM.getId();
                NetworkName = deviveM.getName();
                Log.d("TAG", "onClick: " + deviveM.getId() + "====" + deviveM.getName() + "===" + im + "====" + mfg);
                alertDialog.dismiss();
                txt.setText(deviveM.getName());

            });


        }

        @Override
        public int getItemCount() {
            return bList.size();
        }

        class MyStateHolder extends RecyclerView.ViewHolder {
            TextView device_name;
            LinearLayoutCompat select_device;

            public MyStateHolder(@NonNull View itemView) {
                super(itemView);
                device_name = itemView.findViewById(R.id.brands_name);
                select_device = itemView.findViewById(R.id.select_brands);

            }
        }
    }

    private void NetWorkPopup(AppCompatTextView txt) {
        statusModelList.clear();
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddOwnDevicesScreensOne.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        networkList = vieww.findViewById(R.id.device_list);
        networkList.setLayoutManager(new LinearLayoutManager(AddOwnDevicesScreensOne.this));
        networkList.addItemDecoration(new DividerItemDecoration(networkList.getContext(), DividerItemDecoration.VERTICAL));
        toList(txt);
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }


}