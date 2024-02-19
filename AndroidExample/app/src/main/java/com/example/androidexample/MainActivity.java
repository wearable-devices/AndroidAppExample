package com.example.androidexample;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.androidexample.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import mudraAndroidSDK.enums.Feature;
import mudraAndroidSDK.enums.LoggingSeverity;
import mudraAndroidSDK.interfaces.callback.OnGetEmailLicensesCallback;
import mudraAndroidSDK.model.Mudra;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeMudra();
    }

    private void initializeMudra()
    {
        Mudra.getInstance().requestAccessPermissions(this);
        Mudra.getInstance().getLicenseForEmailFromCloud("-------@--------", (success, errorResult) -> {
            if( success ) {
                Log.d(TAG , "licenses set successfully.");
            } else {
                Log.d(TAG , "failed to set licenses : " + errorResult +".");
            }
        });
        Mudra.getInstance().setCoreLoggingSeverity(LoggingSeverity.Error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}