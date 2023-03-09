package com.devicet.devicetracking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.devicet.devicetracking.Utils.SharedPreferenceHelper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

public class ChooseScreen extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton rb1,rb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_screen);

        try { Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {e.printStackTrace();}


        radioGroup=findViewById(R.id.rg_btn);
        findViewById(R.id.btn_pr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGivePermission();
            }
        });
    }


    public void toGivePermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                ).withListener(new MultiplePermissionsListener() {
            @SuppressLint("MissingPermission")
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

               // SharedPreferenceHelper.setKey(ChooseScreen.this,"key","1");
                Intent i = new Intent(ChooseScreen.this, DeviceListScreens.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);


               /* if(report.areAllPermissionsGranted()){
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    rb1 = (RadioButton) findViewById(selectedId);
                    if(selectedId==-1){
                        Toast.makeText(ChooseScreen.this,"Nothing selected. Please Select One", Toast.LENGTH_SHORT).show();
                    } else{
                        if(rb1.getText().equals("Add your own device")){
                            SharedPreferenceHelper.setKey(ChooseScreen.this,"key","2");
                            Intent i = new Intent(ChooseScreen.this, AddOwnDevicesScreensOne.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            //finish();
                        }else {
                            SharedPreferenceHelper.setKey(ChooseScreen.this,"key","1");
                            Intent i = new Intent(ChooseScreen.this, DeviceListScreens.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            //finish();
                        }

                        Toast.makeText(ChooseScreen.this,rb1.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
                if(report.isAnyPermissionPermanentlyDenied()){
                    showSettingsDialog();
                }*/

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
                //Toast.makeText(ChooseScreen.this,"rb1.getText()", Toast.LENGTH_SHORT).show();
            }
        }).check();






    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseScreen.this);
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