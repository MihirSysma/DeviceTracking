package com.devicet.devicetracking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devicet.devicetracking.Models.GetBrands;
import com.devicet.devicetracking.Models.GetDeviceModel;
import com.devicet.devicetracking.Models.GetDeviceSubModel;
import com.devicet.devicetracking.Models.GetSubBrands;
import com.devicet.devicetracking.Models.StatusModel;
import com.devicet.devicetracking.Models.UpdateStatus;
import com.devicet.devicetracking.Utils.EndPoints;
import com.devicet.devicetracking.Utils.GPSTracker;
import com.devicet.devicetracking.Utils.RetrofitSingleton;
import com.devicet.devicetracking.Utils.SharedPreferenceHelper;
import com.nambimobile.widgets.efab.FabOption;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceListScreens extends AppCompatActivity {

    List<GetDeviceModel> demoModelList;
    static String strToken, Uid, brandOs, brandModel, brandName, lat, lng, address, dt, uname;
    List<StatusModel> statusModelList;
    boolean doubleClick = false;

    RecyclerView rvDeviceList;
    LocationManager locationManager;

    StatsuAdapter statusAdapter;
    RecyclerView statusRecyclerview;
    AlertDialog alertDialog;
    FabOption fab3, fab1, fab2;
    LinearLayoutCompat data_not_found;
    TextView u_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_screens);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        demoModelList = new ArrayList<GetDeviceModel>();
        rvDeviceList = (RecyclerView) findViewById(R.id.rv_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));


        fab2 = findViewById(R.id.add_own_device);
        fab1 = findViewById(R.id.add_another_device);
        fab3 = findViewById(R.id.add_another_mobile_device);
        data_not_found = findViewById(R.id.data_not_found);

        u_name = findViewById(R.id.u_name);

        brandOs = Build.MANUFACTURER + " " + Build.VERSION.RELEASE;
        brandName = Build.BRAND;
        brandModel = Build.MODEL;

        statusModelList = new ArrayList<>();
        uname = SharedPreferenceHelper.getUsername(DeviceListScreens.this, "nm", "null");
        if (uname != null) {
            u_name.setText(uname);
        }
        strToken = SharedPreferenceHelper.getPrefTokenKey(DeviceListScreens.this, "token", "tk");
        Uid = SharedPreferenceHelper.getUid(DeviceListScreens.this, "UID", "0");
        if (strToken != null & Uid != null) {
            DeviceListCall(strToken, Uid);
        } else {
            Toast.makeText(this, "Please try again. Something went wrong", Toast.LENGTH_SHORT).show();
        }
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
            Date date = new Date();
            dt = formatter.format(date);
            Log.d("TAG", "onCreate: " + formatter.format(date));
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        fab1.setOnClickListener(v -> {
            Intent i = new Intent(DeviceListScreens.this, AddAnotherDevicesScreensOne.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        fab2.setOnClickListener(v -> {
            Intent i = new Intent(DeviceListScreens.this, AddOwnDevicesScreensOne.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        fab3.setOnClickListener(v -> {
            Intent i = new Intent(DeviceListScreens.this, AddAnotherMobileScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        findViewById(R.id.exit_app).setOnClickListener(v -> {
            SharedPreferenceHelper.clearUsername(DeviceListScreens.this);
            SharedPreferenceHelper.clearStatus(DeviceListScreens.this);
            SharedPreferenceHelper.clearKey(DeviceListScreens.this);
            SharedPreferenceHelper.clearPrefToken(DeviceListScreens.this);
            Intent i = new Intent(DeviceListScreens.this, SplashScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });


        findViewById(R.id.add_own_device).setOnClickListener(v -> {
            Intent i = new Intent(DeviceListScreens.this, AddOwnDevicesScreensOne.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }


    class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

        List<GetDeviceSubModel> demoModelList;

        public DeviceAdapter(List<GetDeviceSubModel> demoModelList) {
            this.demoModelList = demoModelList;
        }

        @NonNull
        @Override
        public DeviceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View vv = LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_items, viewGroup, false);
            return new MyViewHolder(vv);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceAdapter.MyViewHolder myViewHolder, int i) {
            GetDeviceSubModel demoModel = demoModelList.get(i);
            myViewHolder.deviceName.setText(demoModel.getBrandName());
            myViewHolder.imei.setText(demoModel.getImei());
            myViewHolder.deviceOs.setText(demoModel.getOpertingSystem());
            myViewHolder.model.setText(demoModel.getModelName());
            myViewHolder.detection_date.setText(demoModel.getDetectionTime());
            myViewHolder.address.setText(demoModel.getAddress());
            myViewHolder.manuf.setText(demoModel.getManufactureSerialNumber());
            myViewHolder.mark.setText(demoModel.getMarkOfCompliance());
            myViewHolder.mobile_statuss.setVisibility(View.GONE);

            /* myViewHolder.device_id.setText(demoModel.getProductDeviceId());

            if(demoModel.getImei().equals("lpshfg"))
            {
                myViewHolder.simLay.setVisibility(View.GONE);
                myViewHolder.imeiLay.setVisibility(View.GONE);
                myViewHolder.deviceLay.setVisibility(View.GONE);
            }else{
                myViewHolder.imei.setText(demoModel.getImei());
                myViewHolder.simNumber.setText(demoModel.getNetworkSimCode());
            }

            Log.d("TAG", "onBindViewHolder: "+demoModel.getType());*/

            if (demoModel.getType().equals("1")) {
                myViewHolder.typess.setText("Current Device");
                myViewHolder.status.setFocusableInTouchMode(true);
                myViewHolder.mobile_statuss.setVisibility(View.VISIBLE);
                myViewHolder.mark_text.setText("Network name");
            } else {
                myViewHolder.typess.setVisibility(View.GONE);
            }

            if (demoModel.getStatus() == 1) {
                myViewHolder.status.setText("Active");
            } else if (demoModel.getStatus() == 2) {
                myViewHolder.status.setText("Deactivate");
            } else if (demoModel.getStatus() == 3) {
                myViewHolder.status.setText("Reactivate");
            } else if (demoModel.getStatus() == 4) {
                myViewHolder.status.setText("Stolen");
            } else if (demoModel.getStatus() == 5) {
                myViewHolder.status.setText("Lost");
            } else {
                myViewHolder.status.setText("Damaged");
            }

            myViewHolder.status.setOnClickListener(v -> {
                BrandsPopup(strToken, 1, myViewHolder.status, demoModel.getImei(), demoModel.getMfgStatus(), demoModel.getType());

            });
            /* if(demoModel.getStatus().equals("1")){
                myViewHolder.status.setText("Active");
            }else if(demoModel.getStatus().equals("2")){
                myViewHolder.status.setText("Sold");
            }else{
                myViewHolder.status.setText("Stolen");
            }*/


        }

        @Override
        public int getItemCount() {
            return demoModelList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView device_id, mark_text, deviceName, imei, deviceOs, model, status, address, manuf, mark, detection_date, typess, simNumber;
            TextView mobile_statuss;
            LinearLayoutCompat simLay, imeiLay, deviceLay;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                typess = itemView.findViewById(R.id.typess);
                detection_date = itemView.findViewById(R.id.detection_date);
                address = itemView.findViewById(R.id.address);
                manuf = itemView.findViewById(R.id.manu_serial_no);
                mark = itemView.findViewById(R.id.mark_serial_no);
                deviceName = itemView.findViewById(R.id.device_name);
                imei = itemView.findViewById(R.id.imei);
                deviceOs = itemView.findViewById(R.id.device);
                model = itemView.findViewById(R.id.model);
                status = itemView.findViewById(R.id.status);
                mobile_statuss = itemView.findViewById(R.id.mobile_statuss);

                simNumber = itemView.findViewById(R.id.sim_number_items);
                simLay = itemView.findViewById(R.id.sim_lay);
                imeiLay = itemView.findViewById(R.id.imei_lay);
                mark_text = itemView.findViewById(R.id.mark_text);
                device_id = itemView.findViewById(R.id.device_id);
                deviceLay = itemView.findViewById(R.id.device_ly);

            }

        }

    }

    private void DeviceListCall(String tk, String userId) {
        RetrofitSingleton.showDialog(DeviceListScreens.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<GetDeviceModel> headmodel = requestInterface.getDeviceList(tk, userId);
        headmodel.enqueue(new Callback<GetDeviceModel>() {
            @Override
            public void onResponse(Call<GetDeviceModel> call, Response<GetDeviceModel> response) {
                RetrofitSingleton.hideDialog();
                try {
                    if (response.body() != null) {
                        String msg = response.body().getApiMessage();
                        int status = response.body().getApiStatus();
                        data_not_found.setVisibility(View.GONE);
                        if (status == 1) {
                            if (response.body().getData().size() > 0) {
                                DeviceAdapter adapter = new DeviceAdapter(response.body().getData());
                                rvDeviceList.setAdapter(adapter);
                            }
                        } else {
                            data_not_found.setVisibility(View.VISIBLE);
                            Toast.makeText(DeviceListScreens.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (NullPointerException ee) {
                    data_not_found.setVisibility(View.VISIBLE);
                    Log.d("exception", "onResponse: " + ee.getMessage());
                }

            }

            @Override
            public void onFailure(Call<GetDeviceModel> call, Throwable t) {
                RetrofitSingleton.hideDialog();
                data_not_found.setVisibility(View.VISIBLE);
                if (t.getMessage().equalsIgnoreCase("java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at line 1 column 51 path $.data")) {
                    data_not_found.setVisibility(View.VISIBLE);
                    // Toast.makeText(DeviceListScreens.this, "Not added yet any devices", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            lng = "" + loc.getLongitude();
            lat = "" + loc.getLatitude();
            if (lng != null) {
                // detectionPost(strToken,Integer.parseInt(Uid),"121323423232345","232123212345654",brandOs,dt.toString(),"",brandName,brandModel,lat,lng,address);
            }
            Log.d("Latitute", "onCreate: " + lat + "===" + lng + "====" + dt.toString());//2022-09-27 15:20:00
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

    private void statusDevice(String tk, int bId, TextView textView, RecyclerView rList, TextView sStatus) {
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<GetBrands> headmodel = requestInterface.getBrands(tk, bId);
        headmodel.enqueue(new Callback<GetBrands>() {
            @Override
            public void onResponse(Call<GetBrands> call, Response<GetBrands> response) {
                try {
                    if (response.body() != null) {
                        String msg = response.body().getApiMessage();
                        int status = response.body().getApiStatus();
                        if (status == 1) {
                            rList.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            List<GetSubBrands> resp1 = response.body().getSubBrands();
                            if (resp1 != null) {
                                //  statusAdapter = new StatsuAdapter(resp1,getApplicationContext(),sStatus);
                                //  statusRecyclerview.setAdapter(statusAdapter);
                            }
                            //Toast.makeText(AddAnotherDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            rList.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(msg);
                            Toast.makeText(DeviceListScreens.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception ff) {
                    ff.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetBrands> call, Throwable t) {
                Toast.makeText(DeviceListScreens.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BrandsPopup(String tk, int id, TextView setStatus, String imei, String mfg, String type) {
        statusModelList.clear();
        AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceListScreens.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        statusRecyclerview = vieww.findViewById(R.id.device_list);
        statusRecyclerview.setLayoutManager(new LinearLayoutManager(DeviceListScreens.this));
        statusRecyclerview.addItemDecoration(new DividerItemDecoration(statusRecyclerview.getContext(), DividerItemDecoration.VERTICAL));
        toList(setStatus, imei, mfg, type);
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }

    private void toList(TextView setStatus, String imei, String mfg, String type) {

        StatusModel statusModel = new StatusModel();
        statusModel.setId("1");
        statusModel.setName("ACTIVE");
        statusModelList.add(statusModel);

        StatusModel statusModel1 = new StatusModel();
        statusModel1.setId("2");
        statusModel1.setName("DEACTIVATE");
        statusModelList.add(statusModel1);

        StatusModel statusModel2 = new StatusModel();
        statusModel2.setId("3");
        statusModel2.setName("REACTIVATE");
        statusModelList.add(statusModel2);

        StatusModel statusModel3 = new StatusModel();
        statusModel3.setId("4");
        statusModel3.setName("STOLEN");
        statusModelList.add(statusModel3);

        StatusModel statusModel4 = new StatusModel();
        statusModel4.setId("5");
        statusModel4.setName("LOST");
        statusModelList.add(statusModel4);

        StatusModel statusModel5 = new StatusModel();
        statusModel5.setId("6");
        statusModel5.setName("DAMAGED");
        statusModelList.add(statusModel5);

        statusAdapter = new StatsuAdapter(statusModelList, getApplicationContext(), setStatus, imei, mfg, type);
        statusRecyclerview.setAdapter(statusAdapter);

    }

    class StatsuAdapter extends RecyclerView.Adapter<StatsuAdapter.MyStateHolder> {

        List<StatusModel> bList;
        Context context;
        TextView stxtView;
        String im, mfg, type;

        public StatsuAdapter(List<StatusModel> deviceList, Context context, TextView stxtView, String im, String mfg, String type) {
            this.bList = deviceList;
            this.context = context;
            this.stxtView = stxtView;
            this.im = im;
            this.mfg = mfg;
            this.type = type;
        }

        @NonNull
        @Override
        public StatsuAdapter.MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_itemss, parent, false);
            return new MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StatsuAdapter.MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final StatusModel gd = bList.get(position);
            holder.device_name.setText(gd.getName());

            holder.select_device.setOnClickListener(view -> {
                final StatusModel deviveM = bList.get(position);
                GPSTracker gps = new GPSTracker(DeviceListScreens.this);
                Log.d("TAG", "onClick: " + deviveM.getId() + "====" + deviveM.getName() + "===" + im + "====" + mfg + gps.latitude + gps.longitude + gps.getAddressLine(DeviceListScreens.this));
                if (gps.getAddressLine(DeviceListScreens.this) == null) {
                    String address =  "null";
                } else {
                    String address =  gps.getAddressLine(DeviceListScreens.this);
                }
                statusUpdate(strToken, Uid, im, String.valueOf(gps.latitude), String.valueOf(gps.longitude), "address", deviveM.getId(), type, mfg);
                alertDialog.dismiss();
                stxtView.setText(deviveM.getName());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitSingleton.hideDialog();
    }

    private void statusUpdate(String tk, String uid, String imei, String lat, String lng, String address, String status, String type, String mfg) {
        RetrofitSingleton.showDialog(DeviceListScreens.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<UpdateStatus> headmodel = requestInterface.updateStatus(tk, uid, imei, lat, lng, address, status, type, mfg);
        headmodel.enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {
                Log.i("TAG_MIHIR",response.toString());
                if (response.code() == 403) {
                    RetrofitSingleton.hideDialog();
                    String msg = "Having some issues from the server. Re-open the application";
                    Toast toast = Toast.makeText(DeviceListScreens.this, Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                } else {
                    try {
                        RetrofitSingleton.hideDialog();
                        if (response.body() != null) {
                            String msg = response.body().getApiMessage();
                            int status = response.body().getApiStatus();
                            if (status == 1) {
                                Toast.makeText(DeviceListScreens.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DeviceListScreens.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (NullPointerException ee) {
                        RetrofitSingleton.hideDialog();
                        Log.d("Throwable", "onFailure: " + response.code());
                        Toast.makeText(DeviceListScreens.this, "Having some error from the backend. Please contact support", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateStatus> call, Throwable t) {
                RetrofitSingleton.hideDialog();
                // Log.d("Throwable", "onFailure: "+t.getMessage());

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (doubleClick) {
            super.onBackPressed();
            return;
        }

        this.doubleClick = true;
        Toast toast = Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 50, 50);
        toast.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleClick = false, 2000);
    }

}