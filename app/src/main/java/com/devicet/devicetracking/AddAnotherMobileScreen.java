package com.devicet.devicetracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;
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
import com.devicet.devicetracking.Utils.EndPoints;
import com.devicet.devicetracking.Utils.RetrofitSingleton;
import com.devicet.devicetracking.Utils.SharedPreferenceHelper;

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

public class AddAnotherMobileScreen extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    static String strModel, strOs, strToken, tk, Uid;
    String[] mobielStatus = {"Select Mobile Status", "Active", "Deactivate", "Reactivate", "Stolen", "Lost", "DAMAGED"};
    static String spinOsType, spinBrandType, spinModelType, strLat = "22.7196", strLng = "75.8577", spinMobileStatus;
    DatePickerDialog picker;
    LinearLayoutCompat lay_other, lay_add;
    List<StatusModel> statusModelList, networkModelList;
    StatsuAdapter statusAdapter;
    RecyclerView statusRecyclerview;
    RecyclerView networkList;

    ArrayAdapter aa;

    List<GetDeviceSubList> getDeviceList;
    AlertDialog alertDialog;
    List<GetSubBrands> BList;
    List<GetModelSub> getModelSubList;
    AppCompatEditText etxImeiNumber, etxImeiNumber2, etxSimNumber, etxSimNumber2, etxOs, etxDt, etxGps, etxManuSerialNumber, etxMarkComplience, etxEmailId;
    AppCompatSpinner etxModelName, etxDeviceType;

    RecyclerView deviceRList, brandRecyclerview, modelRecyclerview;
    DeviceAdapter deviceAdapter;
    BrandsAdapter brandAdapter;
    ModelAdapter modelAdapter;
    AppCompatTextView txtDeviceName, brand_txt, model_txt, txt_mobile_status_name, mobile_status_hint, network_text1, network_text2;


    LinearLayoutCompat imeiLy, imei_ly2, simeLy, deviceLy;
    TextView title;
    static int dId, bId;
    static String mobileStatusId, mobileStatuName;

    NetworkAdapter networkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_phone_screens);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
        }


        getDeviceList = new ArrayList<>();
        statusModelList = new ArrayList<>();
        BList = new ArrayList<>();
        getModelSubList = new ArrayList<>();

        mobile_status_hint = findViewById(R.id.mobile_status_hint);
        mobile_status_hint.setText("Status");

        deviceLy = findViewById(R.id.device_ly);
        deviceLy.setVisibility(View.GONE);

        title = findViewById(R.id.title_name);
        title.setText("Add another mobile device");
        findViewById(R.id.back).setOnClickListener(v -> finish());

        txt_mobile_status_name = findViewById(R.id.mobile_status_name);
        txt_mobile_status_name.setText("Select Status");
        txt_mobile_status_name.setOnClickListener(v -> StatusPopup(txt_mobile_status_name));

        txtDeviceName = findViewById(R.id.device_text);
        txtDeviceName.setOnClickListener(v -> DevicePopup());

        brand_txt = findViewById(R.id.brand_txt);
        brand_txt.setOnClickListener(v -> BrandsPopup());

        model_txt = findViewById(R.id.model_text);
        model_txt.setOnClickListener(v -> ModelPopup());

        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
        }
        strToken = SharedPreferenceHelper.getPrefTokenKey(AddAnotherMobileScreen.this, "token", "tk");
        Uid = SharedPreferenceHelper.getUid(AddAnotherMobileScreen.this, "UID", "0");
        if (strToken != null & Uid != null) {
            tk = strToken;
        } else {
            Toast.makeText(this, "Please try again. Something went wrong", Toast.LENGTH_SHORT).show();
        }

        etxOs = findViewById(R.id.operating_system);
        etxOs.setVisibility(View.GONE);
        etxDt = findViewById(R.id.date_time);
        etxGps = findViewById(R.id.gps_location);
        etxManuSerialNumber = findViewById(R.id.manu_serial_number);
        etxMarkComplience = findViewById(R.id.mark_of_comp);
        etxEmailId = findViewById(R.id.email_id);
        etxSimNumber = findViewById(R.id.sim_number);
        etxSimNumber2 = findViewById(R.id.sim_number_2);

        network_text1 = findViewById(R.id.network_text);
        network_text1.setOnClickListener(v -> NetWorkPopup(network_text1));

        network_text2 = findViewById(R.id.network_text_2);
        network_text2.setOnClickListener(v -> NetWorkPopup(network_text2));

        etxImeiNumber = findViewById(R.id.imei_number);
        etxImeiNumber2 = findViewById(R.id.imei_number_2);

        // etxModelName = findViewById(R.id.model_name);
        //  etxDeviceType=findViewById(R.id.device_type);
        // etxMobileStatus=findViewById(R.id.mobile_statuss);


        lay_add = findViewById(R.id.ly_add_device);
        lay_add.setVisibility(View.VISIBLE);
        lay_other = findViewById(R.id.ly_serial_number);

        String kk = SharedPreferenceHelper.getKey(AddAnotherMobileScreen.this, "key", "0");
        String dvId = getDeviceId(AddAnotherMobileScreen.this);
        //  String sim = getSimNumber(AddAnotherDevicesScreensOne.this);
        //  String imeiNumber = getImeiNumber(AddAnotherDevicesScreensOne.this);
        // etxImeiNumber.setText(imeiNumber);
        //  etxSimNumber.setText(sim);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Log.d("TAG", "onCreate: " + date);
        etxDt.setText(formatter.format(date));
        etxDt.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(AddAnotherMobileScreen.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        try {
                            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                            String inputText = dayOfMonth + "-" + monthOfYear + "-" + year1;
                            Date date1 = inputFormat.parse(inputText);
                            String outputText = outputFormat.format(date1);
                            etxDt.setText(outputText);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }

                    }, year, month, day);
            picker.show();
        });
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        Log.i("TAG", "SERIAL: " + Build.USER);
        Log.i("TAG", "MODEL: " + Build.MODEL);
        Log.i("TAG", "ID: " + Build.ID);
        Log.i("TAG", "Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG", "brand: " + Build.BRAND);
        Log.i("TAG", "type: " + Build.TYPE);
        Log.i("TAG", "user: " + Build.USER);
        Log.i("TAG", "BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG", "INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG", "SDK  " + Build.VERSION.SDK);
        Log.i("TAG", "BOARD: " + Build.BOARD);
        Log.i("TAG", "BRAND " + Build.BRAND);
        Log.i("TAG", "HOST " + Build.PRODUCT);
        Log.i("TAG", "FINGERPRINT: " + Build.FINGERPRINT);
        Log.i("TAG", "Version Code: " + Build.VERSION.RELEASE);


        // etxManuSerialNumber.setText(Build.SERIAL);


        findViewById(R.id.submit_add_device).setOnClickListener(v -> {
            //    Toast.makeText(AddAnotherScreensOne.this, "Click Me", Toast.LENGTH_SHORT).show();
            if (txtDeviceName.getText().toString().isEmpty()) {
                Toast.makeText(AddAnotherMobileScreen.this, "Select Device Type", Toast.LENGTH_SHORT).show();
            } else if (brand_txt.getText().toString().isEmpty()) {
                Toast.makeText(AddAnotherMobileScreen.this, "Select Brand Type", Toast.LENGTH_SHORT).show();
            } else if (model_txt.getText().toString().isEmpty()) {
                Toast.makeText(AddAnotherMobileScreen.this, "Select Model Type", Toast.LENGTH_SHORT).show();
            } else if (txt_mobile_status_name.getText().toString().isEmpty()) {
                Toast.makeText(AddAnotherMobileScreen.this, "Select Status", Toast.LENGTH_SHORT).show();
            } else if (etxManuSerialNumber.getText().toString().isEmpty()) {
                etxManuSerialNumber.setError("Field empty");
            } else if (etxMarkComplience.getText().toString().isEmpty()) {
                etxMarkComplience.setError("Field empty");
            } else if (etxGps.getText().toString().isEmpty()) {
                etxGps.setError("Field empty");
            } else {
                String imei = etxImeiNumber.getText().toString();
                String imei2 = etxImeiNumber2.getText().toString();

                detectionPost(tk, brand_txt.getText().toString(), model_txt.getText().toString(), etxOs.getText().toString(), imei, imei2, strLat, strLng, Integer.parseInt(Uid),
                        mobileStatusId, network_text1.getText().toString(), network_text2.getText().toString(), Integer.parseInt(etxSimNumber.getText().toString()),
                        Integer.parseInt(etxSimNumber2.getText().toString()), etxManuSerialNumber.getText().toString(), etxMarkComplience.getText().toString(), "", etxGps.getText().toString(), "2");
            }
        });
    }

    private void detectionPost(String auth, String device_brand, String device_model, String device_os,
                               String imei, String imei_2, String detection_lat, String detection_long,
                               int user_id, String status, String network_type_one,
                               String networkType2, int network_sim_code,
                               int network_sim_code_2, String manufacture_serial_number,
                               String mark_of_compliance, String product_device_id, String address, String type) {
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<DetectionModel> headmodel = requestInterface.detections(auth, device_brand, device_model, device_os, imei, imei_2,
                detection_lat, detection_long, user_id, status, network_type_one, networkType2, network_sim_code, network_sim_code_2, manufacture_serial_number,
                mark_of_compliance, product_device_id, address, type);
        headmodel.enqueue(new Callback<DetectionModel>() {
            @Override
            public void onResponse(Call<DetectionModel> call, Response<DetectionModel> response) {
                try {
                    if (response.body() != null) {
                        String msg = response.body().getApiMessage();
                        int status = response.body().getApiStatus();
                        if (status == 1) {
                            msgAPi(msg);
                        } else {
                            String msgs = String.valueOf(Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"));
                            msgAPi(msgs);
                        }
                    }
                } catch (Exception ff) {
                    ff.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DetectionModel> call, Throwable t) {
                Toast.makeText(AddAnotherMobileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // mobileStatusId=mobielStatus[parent.getSelectedItemPosition()];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void msgAPi(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddAnotherMobileScreen.this);
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
        closeBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent i = new Intent(AddAnotherMobileScreen.this, DeviceListScreens.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        });
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
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
            simNumber = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("TAG", "getDeviceId_Q: " + simNumber);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                simNumber = mTelephony.getSimSerialNumber();
            } else {
                simNumber = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.d("TAG", "getDeviceId_android_id: " + simNumber);
            }
        }
        return simNumber;
    }


    @SuppressLint({"HardwareIds", "NewApi"})
    public static String getImeiNumber(Context context) {
        String imeiNumber = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imeiNumber = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                imeiNumber = mTelephony.getImei();
            } else {
                imeiNumber = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.d("TAG", "getDeviceId_android_id: " + imeiNumber);
            }
        }
        return imeiNumber;
    }


    private void getDevice(String tk) {
        RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<GetDevice> headmodel = requestInterface.getDevice(tk);
        headmodel.enqueue(new Callback<GetDevice>() {
            @Override
            public void onResponse(Call<GetDevice> call, Response<GetDevice> response) {
                RetrofitSingleton.hideDialog();
                try {
                    if (response.body() != null) {
                        String msg = response.body().getApiMessage();
                        int status = response.body().getApiStatus();
                        if (status == 1) {
                            List<GetDeviceSubList> resp1 = response.body().getData();
                            if (resp1 != null) {
                                deviceAdapter = new AddAnotherMobileScreen.DeviceAdapter(resp1, getApplicationContext());
                                deviceRList.setAdapter(deviceAdapter);
                            }


                            // Toast.makeText(AddAnotherDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAnotherMobileScreen.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ff) {
                    ff.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetDevice> call, Throwable t) {
                RetrofitSingleton.hideDialog();
                Toast.makeText(AddAnotherMobileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DevicePopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddAnotherMobileScreen.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        SearchView searchView = vieww.findViewById(R.id.search_view);
        deviceRList = vieww.findViewById(R.id.device_list);
        deviceRList.setLayoutManager(new LinearLayoutManager(AddAnotherMobileScreen.this));
        deviceRList.addItemDecoration(new DividerItemDecoration(deviceRList.getContext(), DividerItemDecoration.VERTICAL));
        getDevice(tk);
        dialog.setView(vieww);
        dialog.setCancelable(true);

        alertDialog = dialog.create();
        alertDialog.show();
    }

    class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyStateHolder> {

        List<GetDeviceSubList> deviceList;
        Context context;

        public DeviceAdapter(List<GetDeviceSubList> deviceList, Context context) {
            this.deviceList = deviceList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_itemsss, parent, false);
            return new MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final GetDeviceSubList gd = deviceList.get(position);
            holder.device_name.setText(gd.getName());

            holder.select_device.setOnClickListener(view -> {
                final GetDeviceSubList deviveM = deviceList.get(position);
                Log.d("TAG", "onClick: " + deviveM.getId());
                dId = deviveM.getId();
                spinOsType = deviveM.getName();
                txtDeviceName.setText(deviveM.getName());
                alertDialog.dismiss();

            });

        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class MyStateHolder extends RecyclerView.ViewHolder {
            TextView device_name;
            LinearLayoutCompat select_device;

            public MyStateHolder(@NonNull View itemView) {
                super(itemView);
                device_name = itemView.findViewById(R.id.device_name);
                select_device = itemView.findViewById(R.id.select_device);

            }
        }
    }

    private void brandsapis(String tk, int bId) {
        RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<GetBrands> headmodel = requestInterface.getBrands(tk, bId);
        headmodel.enqueue(new Callback<GetBrands>() {
            @Override
            public void onResponse(Call<GetBrands> call, Response<GetBrands> response) {
                RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
                try {
                    if (response.body() != null) {
                        String msg = response.body().getApiMessage();
                        int status = response.body().getApiStatus();
                        if (status == 1) {
                            List<GetSubBrands> resp1 = response.body().getSubBrands();
                            if (resp1 != null) {
                                brandAdapter = new AddAnotherMobileScreen.BrandsAdapter(resp1, getApplicationContext());
                                brandRecyclerview.setAdapter(brandAdapter);
                            }

                        } else {
                            Log.d("getDeviceList", "onResponse: " + getDeviceList.isEmpty());
                            if (response.body().getSubBrands().size() <= 1) {
                                brand_txt.setText(null);
                            }
                            Toast.makeText(AddAnotherMobileScreen.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ff) {
                    ff.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetBrands> call, Throwable t) {
                RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
                Toast.makeText(AddAnotherMobileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BrandsPopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddAnotherMobileScreen.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        SearchView searchView = vieww.findViewById(R.id.search_view);
        brandRecyclerview = vieww.findViewById(R.id.device_list);
        brandRecyclerview.setLayoutManager(new LinearLayoutManager(AddAnotherMobileScreen.this));
        brandRecyclerview.addItemDecoration(new DividerItemDecoration(brandRecyclerview.getContext(), DividerItemDecoration.VERTICAL));
        brandsapis(tk, dId);
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }

    class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.MyStateHolder> {

        List<GetSubBrands> bList;
        Context context;

        public BrandsAdapter(List<GetSubBrands> deviceList, Context context) {
            this.bList = deviceList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.brands_itemsss, parent, false);
            return new MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final GetSubBrands gd = bList.get(position);
            holder.device_name.setText(gd.getName());

            holder.select_device.setOnClickListener(view -> {
                final GetSubBrands deviveM = bList.get(position);
                Log.d("TAG", "onClick: " + deviveM.getId());
                bId = deviveM.getId();
                spinBrandType = deviveM.getName();
                brand_txt.setText(deviveM.getName());
                alertDialog.dismiss();

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

    private void modelAPi(String tk, int bId) {
        RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<GetModel> headmodel = requestInterface.getModels(tk, bId);
        headmodel.enqueue(new Callback<GetModel>() {
            @Override
            public void onResponse(Call<GetModel> call, Response<GetModel> response) {
                RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
                try {
                    if (response.body() != null) {
                        String msg = response.body().getApiMessage();
                        int status = response.body().getApiStatus();
                        if (status == 1) {
                            List<GetModelSub> resp1 = response.body().getData();
                            if (resp1 != null) {
                                modelAdapter = new AddAnotherMobileScreen.ModelAdapter(resp1, getApplicationContext());
                                modelRecyclerview.setAdapter(modelAdapter);
                            }
                            //Toast.makeText(AddAnotherDevicesScreensOne.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAnotherMobileScreen.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ff) {
                    ff.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetModel> call, Throwable t) {
                RetrofitSingleton.showDialog(AddAnotherMobileScreen.this);
                Toast.makeText(AddAnotherMobileScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ModelPopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddAnotherMobileScreen.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        SearchView searchView = vieww.findViewById(R.id.search_view);
        modelRecyclerview = vieww.findViewById(R.id.device_list);
        modelRecyclerview.setLayoutManager(new LinearLayoutManager(AddAnotherMobileScreen.this));
        modelRecyclerview.addItemDecoration(new DividerItemDecoration(modelRecyclerview.getContext(), DividerItemDecoration.VERTICAL));
        modelAPi(tk, bId);
        dialog.setView(vieww);
        dialog.setCancelable(true);

        alertDialog = dialog.create();
        alertDialog.show();
    }

    class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.MyStateHolder> {

        List<GetModelSub> deviceList;
        Context context;

        public ModelAdapter(List<GetModelSub> deviceList, Context context) {
            this.deviceList = deviceList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_itemsss, parent, false);
            return new MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final GetModelSub gd = deviceList.get(position);
            holder.device_name.setText(gd.getName());
            Log.d("TAG", "getName888888: " + gd.getName());
            holder.select_device.setOnClickListener(view -> {
                final GetModelSub deviveM = deviceList.get(position);
                spinModelType = deviveM.getName();
                model_txt.setText(deviveM.getName());
                alertDialog.dismiss();

            });


        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class MyStateHolder extends RecyclerView.ViewHolder {
            TextView device_name;
            LinearLayoutCompat select_device;

            public MyStateHolder(@NonNull View itemView) {
                super(itemView);
                device_name = itemView.findViewById(R.id.device_name);
                select_device = itemView.findViewById(R.id.select_device);

            }
        }
    }


    private void toList(AppCompatTextView txt) {

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


        statusAdapter = new StatsuAdapter(statusModelList, getApplicationContext(), txt);
        statusRecyclerview.setAdapter(statusAdapter);

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
        public MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_itemss, parent, false);
            return new MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final StatusModel gd = bList.get(position);
            holder.device_name.setText(gd.getName());

            holder.select_device.setOnClickListener(view -> {
                final StatusModel deviveM = bList.get(position);
                mobileStatusId = deviveM.getId();
                mobileStatuName = deviveM.getName();
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

    private void StatusPopup(AppCompatTextView txt) {
        statusModelList.clear();
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddAnotherMobileScreen.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        statusRecyclerview = vieww.findViewById(R.id.device_list);
        statusRecyclerview.setLayoutManager(new LinearLayoutManager(AddAnotherMobileScreen.this));
        statusRecyclerview.addItemDecoration(new DividerItemDecoration(statusRecyclerview.getContext(), DividerItemDecoration.VERTICAL));
        toList(txt);
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }

    private void toNetworkList(AppCompatTextView txt) {

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

        networkAdapter = new NetworkAdapter(statusModelList, getApplicationContext(), txt);
        networkList.setAdapter(networkAdapter);


    }

    class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.MyStateHolder> {

        List<StatusModel> bList;
        Context context;
        AppCompatTextView txt;
        String im, mfg;

        public NetworkAdapter(List<StatusModel> deviceList, Context context, AppCompatTextView txt) {
            this.bList = deviceList;
            this.context = context;
            this.txt = txt;
        }

        @NonNull
        @Override
        public NetworkAdapter.MyStateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_itemss, parent, false);
            return new NetworkAdapter.MyStateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NetworkAdapter.MyStateHolder holder, @SuppressLint("RecyclerView") int position) {
            final StatusModel gd = bList.get(position);
            holder.device_name.setText(gd.getName());

            holder.select_device.setOnClickListener(view -> {
                final StatusModel deviveM = bList.get(position);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddAnotherMobileScreen.this);
        dialog.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View vieww = inflater.inflate(R.layout.device_dialog, null);
        networkList = vieww.findViewById(R.id.device_list);
        networkList.setLayoutManager(new LinearLayoutManager(AddAnotherMobileScreen.this));
        networkList.addItemDecoration(new DividerItemDecoration(networkList.getContext(), DividerItemDecoration.VERTICAL));
        toNetworkList(txt);
        dialog.setView(vieww);
        dialog.setCancelable(true);
        alertDialog = dialog.create();
        alertDialog.show();
    }

}