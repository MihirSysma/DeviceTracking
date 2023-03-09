package com.devicet.devicetracking;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.devicet.devicetracking.Models.TokenModel;
import com.devicet.devicetracking.Utils.EndPoints;
import com.devicet.devicetracking.Utils.InternetStatus;
import com.devicet.devicetracking.Utils.RetrofitSingleton;
import com.devicet.devicetracking.Utils.SharedPreferenceHelper;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashScreen extends AppCompatActivity {

    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_splash_screen);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        // catch block to handle NullPointerException
        catch (NullPointerException e) {
        }

        key=SharedPreferenceHelper.getKey(SplashScreen.this,"key","0");
        if(InternetStatus.getInternetStatus(SplashScreen.this).getCheckInternet()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    registerMethod("027763d9f25fbf14f9e4869fbda1f789");
                }
            }, 2*1000);
        }else {
            Toast.makeText(SplashScreen.this, "Connect WI-FI/ Internet. Try Again", Toast.LENGTH_SHORT).show();
        }


    }

    private void registerMethod(String secretKey) {
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<TokenModel> headmodel= requestInterface.getToken(secretKey);
        headmodel.enqueue(new Callback<TokenModel>() {
            @Override
            public void onResponse( Call<TokenModel> call, Response<TokenModel> response) {
                try{if(response.body()!=null){
                    String msg=response.body().getApiMessage();
                    int status=response.body().getApiStatus();
                    if(status == 1) {
                        SharedPreferenceHelper.setPrefTokenKey(SplashScreen.this,"token",response.body().getData().getAccessToken());
                        if(key.equals("1")){
                            Intent i = new Intent(SplashScreen.this, DeviceListScreens.class);
                            startActivity(i);
                            finish();
                        }else{
                            Intent i = new Intent(SplashScreen.this, LoginScreesn.class);
                            startActivity(i);
                            finish();
                        }


                        //Toast.makeText(SplashScreen.this, response.body().getData().getAccessToken(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SplashScreen.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
                }catch (Exception ff) {
                    ff.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {

            }
        });
    }
}