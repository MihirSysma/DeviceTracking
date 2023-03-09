package com.devicet.devicetracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.devicet.devicetracking.Models.LoginModel;
import com.devicet.devicetracking.Utils.SharedPreferenceHelper;
import com.devicet.devicetracking.Utils.isEmailValid;

import com.devicet.devicetracking.Models.RegisterModel;
import com.devicet.devicetracking.Utils.EndPoints;
import com.devicet.devicetracking.Utils.RetrofitSingleton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginScreesn extends AppCompatActivity {

    AppCompatEditText etxEmail, etxPassword;
    String strEmail, strPassword, strToken, tk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screesn);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
        }
        strToken = SharedPreferenceHelper.getPrefTokenKey(LoginScreesn.this, "token", "tk");
        if (strToken != null) {
            tk = strToken;
        } else {
            Toast.makeText(this, "Please try again. Something went wrong", Toast.LENGTH_SHORT).show();
        }
        etxEmail = findViewById(R.id.edit_email_login);
        etxPassword = findViewById(R.id.edit_password_login);

        //etxEmail.setText("testname");
        //etxPassword.setText("123456");

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginScreesn.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(LoginScreesn.this).toBundle());
            }
        });
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etxEmail.getText().toString().isEmpty()) {
                    etxEmail.setError("Email field empty");
                } else if (etxPassword.getText().toString().isEmpty()) {
                    etxPassword.setError("Password field empty");
                } else {
                    if (tk != null) {
                        toGivePermission();
                    }
                }

            }
        });
    }

    private void loginMethod(String tk, String em, String pw) {
        RetrofitSingleton.showDialog(LoginScreesn.this);
        Retrofit retrofit1 = RetrofitSingleton.getClient();
        final EndPoints requestInterface = retrofit1.create(EndPoints.class);
        final Call<LoginModel> headmodel = requestInterface.login(tk, em, pw);
        headmodel.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if (response.code() == 403) {
                    RetrofitSingleton.hideDialog();
                    String msg = "Having some issues from the server. Re-open the application";
                    Toast toast = Toast.makeText(LoginScreesn.this, Html.fromHtml("<font color='#FF0000' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                } else {
                    try {
                        if (response.body() != null) {
                            RetrofitSingleton.hideDialog();
                            String msg = response.body().getApiMessage();
                            int status = response.body().getApiStatus();
                            if (status == 1) {
                                SharedPreferenceHelper.setUsername(LoginScreesn.this, "nm", em);
                                SharedPreferenceHelper.setKey(LoginScreesn.this, "key", "1");
                                SharedPreferenceHelper.setUid(LoginScreesn.this, "UID", response.body().getData().getId().toString());
                                Toast.makeText(LoginScreesn.this, msg, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginScreesn.this, DeviceListScreens.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else if (status == 0) {
                                Toast.makeText(LoginScreesn.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ff) {
                        RetrofitSingleton.hideDialog();
                        ff.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                RetrofitSingleton.hideDialog();
                Toast.makeText(LoginScreesn.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitSingleton.hideDialog();
    }

    public void toGivePermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                ).withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            loginMethod(tk, etxEmail.getText().toString(), etxPassword.getText().toString());
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreesn.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }
}