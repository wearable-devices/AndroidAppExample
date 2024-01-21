package com.example.androidexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidexample.adapters.ConnectedDeviceAdapter;
import com.example.androidexample.adapters.UnconnectedDeviceAdapter;
import com.example.androidexample.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import mudraAndroidSDK.interfaces.MudraDelegate;
import mudraAndroidSDK.model.Mudra;
import mudraAndroidSDK.model.MudraDevice;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private final ArrayList<MudraDevice> pairedDevices = new ArrayList<>();
    private final ArrayList<MudraDevice> scannedDevices = new ArrayList<>();
    private final ArrayList<MudraDevice> connectedDevices = new ArrayList<>();

    private UnconnectedDeviceAdapter pairedDeviceAdapter;
    private UnconnectedDeviceAdapter scannedDeviceAdapter;
    private ConnectedDeviceAdapter connectedDeviceAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setButtons() {
        binding.scanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                if( isOn ) {
                    Mudra.getInstance().scan(getContext());
                } else {
                    scannedDevices.clear();
                    Mudra.getInstance().stopScan();
                    updateScannedDevicesRecycler();
                }
            }
        });
    }

    private void setAdapters() {
        binding.PairedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.PairedDevicesRecycler.setHasFixedSize(true);
        binding.ScannedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ScannedDevicesRecycler.setHasFixedSize(true);
        binding.ConnectedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ConnectedDevicesRecycler.setHasFixedSize(true);
        pairedDeviceAdapter = new UnconnectedDeviceAdapter(pairedDevices, getContext()) {
            @Override
            public void connectDevice(MudraDevice device) {
                device.connectDevice(getContext());
            }
        };
        scannedDeviceAdapter = new UnconnectedDeviceAdapter(scannedDevices, getContext()) {
            @Override
            public void connectDevice(MudraDevice device) {
                device.connectDevice(getContext());
            }
        };

        connectedDeviceAdapter = new ConnectedDeviceAdapter(connectedDevices , getContext()) {
            @Override
            public void disconnectDevice(MudraDevice device) {
                device.disconnect();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void switchPressure(MudraDevice device, TextView textView, Boolean isOn) {
                if( isOn ) {
                    device.setOnPressureReady( v -> {
                        ((Activity)(getContext())).runOnUiThread(()-> {
                            textView.setText(Float.toString(v));
                        });
                    });
                } else {
                    device.setOnPressureReady(null);
                }
            }

        };
    }

    private void updatePairedDevicesRecycler(){
        pairedDevices.clear();
        pairedDevices.addAll(Mudra.getInstance().getBondedDevices(getContext()));
        binding.PairedDevicesRecycler.setAdapter(pairedDeviceAdapter);
    }

    private void updateScannedDevicesRecycler(){
        binding.ScannedDevicesRecycler.setAdapter(scannedDeviceAdapter);
    }

    private void updateConnectedDevicesRecycler(){
        binding.ConnectedDevicesRecycler.setAdapter(connectedDeviceAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Mudra.getInstance().setMudraDelegate(null);
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapters();
        setButtons();
        updatePairedDevicesRecycler();
        updateScannedDevicesRecycler();
        updateConnectedDevicesRecycler();
        setMudraDelegate();

    }

    private void setMudraDelegate() {
        Mudra.getInstance().setMudraDelegate(new MudraDelegate() {
            @Override
            public void onDeviceDiscovered(MudraDevice mudraDevice) {
                ((Activity)(getContext())).runOnUiThread(()-> {
                    if (!scannedDevices.contains(mudraDevice)) {
                        scannedDevices.add(mudraDevice);
                        updateScannedDevicesRecycler();
                    }
                });
            }

            @Override
            public void onDeviceConnected(MudraDevice mudraDevice) {
                ((Activity)(getContext())).runOnUiThread(()-> {
                    if( !connectedDevices.contains(mudraDevice) ){
                        connectedDevices.add(mudraDevice);
                        scannedDevices.remove(mudraDevice);
                    }
                    updateConnectedDevicesRecycler();
                    updatePairedDevicesRecycler();
                    updateScannedDevicesRecycler();
                });
            }

            @Override
            public void onDeviceDisconnected(MudraDevice mudraDevice) {
                ((Activity)(getContext())).runOnUiThread(()-> {
                    if( connectedDevices.contains(mudraDevice) ){
                        connectedDevices.remove(mudraDevice);
                    }
                    updateConnectedDevicesRecycler();
                    updatePairedDevicesRecycler();
                });
            }
        });
    }
}