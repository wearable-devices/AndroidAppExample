package com.example.androidexample;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.androidexample.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import mudraAndroidSDK.enums.Feature;
import mudraAndroidSDK.model.Mudra;

public class MainActivity extends AppCompatActivity {

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
        Mudra.getInstance().setLicense(Feature.RawData, "LicenseType::Main");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}