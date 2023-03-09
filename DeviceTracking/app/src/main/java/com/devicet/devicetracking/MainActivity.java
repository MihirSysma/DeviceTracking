package com.devicet.devicetracking;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.devicet.devicetracking.Utils.SharedPreferenceHelper;
import com.devicet.devicetracking.Models.RegisterModel;
import com.devicet.devicetracking.Utils.EndPoints;
import com.devicet.devicetracking.Utils.InternetStatus;
import com.devicet.devicetracking.Utils.RetrofitSingleton;
import com.devicet.devicetracking.Utils.isEmailValid;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.Header;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String etxUsername,etxMobile,etxPassword,etxEmail,strToken,tk,strGender,strCountry,strFullname;
    AppCompatEditText editUsername,editPassword,editEmail,editMobile,editFullname;
    TelephonyManager tm;
    static String imei;
    AppCompatSpinner spinner,spinerCountry;
    String[] gender = {"Select Gender", "Male", "Female", "Other"};
    String[] country = {"Select Country", "India", "South Africa", "USA","Japan"};
    AppCompatImageView btn;
    CountryCodePicker codePicker;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e) {
        }


        btn=findViewById(R.id.submit_btn);
        codePicker=findViewById(R.id.ccp);
        Log.d("MainActvity", "onCreate: "+codePicker.getSelectedCountryName());

        spinner=findViewById(R.id.select_gender);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinerCountry=findViewById(R.id.select_country);
        spinerCountry.setOnItemSelectedListener(this);
        ArrayAdapter ac = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        ac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinerCountry.setAdapter(ac);

        strToken= SharedPreferenceHelper.getPrefTokenKey(MainActivity.this,"token","tk");
        if(strToken!=null){
            tk=strToken;
            Log.d("TAG", "onCreate: "+strToken);
        }else {
            Toast.makeText(this, "Please try again. Something went wrong", Toast.LENGTH_SHORT).show();
        }


        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginScreesn.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


        editUsername=findViewById(R.id.edit_username);
        editPassword=findViewById(R.id.edit_pass);
        editEmail=findViewById(R.id.edit_email);
        editMobile=findViewById(R.id.edit_contact);
        editFullname=findViewById(R.id.edit_name);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cCode=codePicker.getSelectedCountryCode()+" "+codePicker.getSelectedCountryName();
                Log.d("TAG", "cCode: "+cCode);
                strCountry=codePicker.getSelectedCountryName();
                etxUsername=editUsername.getText().toString();
                etxEmail=editEmail.getText().toString();
                etxMobile= editMobile.getText().toString();
                etxPassword=editPassword.getText().toString();
                strFullname=editFullname.getText().toString();
                if(etxUsername.isEmpty()){
                    editUsername.setError("Username field empty");
                }else if(etxPassword.isEmpty()){
                    editPassword.setError("Password field empty");
                }else if(etxMobile.isEmpty()){
                    editMobile.setError("Mobile number field empty");
                }else if(etxMobile.length()<=7){
                    editMobile.setError("Enter valid mobile number");
                }else if(spinner.getSelectedItem().toString().equals("Select Gender")){
                    Toast.makeText(MainActivity.this, "Select Gender", Toast.LENGTH_SHORT).show();
                }else {
                    if(InternetStatus.getInternetStatus(MainActivity.this).getCheckInternet()){
                        if(tk!=null){
                            if(etxEmail.isEmpty()){
                                registerMethod(strToken,etxUsername,etxPassword,etxEmail,etxMobile,"nope",spinner.getSelectedItem().toString(),cCode);
                            }else{
                                if(!isEmailValid.isValid(etxEmail)) {
                                    editEmail.setError("Please enter valid email");
                                }else{
                                    registerMethod(strToken,etxUsername,etxPassword,etxEmail,etxMobile,"nope",spinner.getSelectedItem().toString(),cCode);
                                }
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Connect WIFI/ Internet", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void registerMethod(String tk, String uname, String pw, String em, String mb, String fname, String gn, String country) {
        RetrofitSingleton.showDialog(MainActivity.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        Log.d("TAG", "registerMethod: "+tk+"=="+uname+"==="+pw+"=="+em+"===="+mb+"===="+fname+"===="+gn+"===="+country+"===");
        final Call<RegisterModel> headmodel= requestInterface.register(tk,fname,em,pw,mb,gn,country,uname);
        headmodel.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse( Call<RegisterModel> call, Response<RegisterModel> response) {
                if(response.code()==403){
                    RetrofitSingleton.hideDialog();
                    String msg="Having some issues from the server. Re-open the application";
                    Toast toast = Toast.makeText(MainActivity.this, Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }else{
                    try{
                        RetrofitSingleton.hideDialog();
                        if (response.body() != null) {
                            String msg = response.body().getApiMessage();
                            int status = response.body().getApiStatus();
                            if(status == 1) {
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, LoginScreesn.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (NullPointerException ee){
                        RetrofitSingleton.hideDialog();
                        Log.d("Throwable", "onFailure: "+response.code());
                        Toast.makeText(MainActivity.this, "Having some error from the backend. Please contact support", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {
                RetrofitSingleton.hideDialog();
               // Log.d("Throwable", "onFailure: "+t.getMessage());

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("gender", "onItemSelected: "+spinner.getSelectedItem().toString());
        Log.d("country", "onItemSelected: "+spinerCountry.getSelectedItem().toString());
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitSingleton.hideDialog();
    }

}