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

import mudraAndroidSDK.enums.AirTouchCommands;
import mudraAndroidSDK.enums.ModelType;
import mudraAndroidSDK.interfaces.MudraDelegate;
import mudraAndroidSDK.interfaces.callback.OnAirTouchClickRelease;
import mudraAndroidSDK.interfaces.callback.OnTensorFlowDataReady;
import mudraAndroidSDK.model.Mudra;
import mudraAndroidSDK.model.MudraDevice;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private final ArrayList<MudraDevice> pairedDevices = new ArrayList<>();
    private final ArrayList<MudraDevice> connectedDevices = new ArrayList<>();

    private UnconnectedDeviceAdapter pairedDeviceAdapter;
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

    private void setAdapters() {
        binding.PairedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.PairedDevicesRecycler.setHasFixedSize(true);
        binding.ConnectedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ConnectedDevicesRecycler.setHasFixedSize(true);
        pairedDeviceAdapter = new UnconnectedDeviceAdapter(pairedDevices, getContext()) {
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
            public void switchPressure(MudraDevice device, ConnectedDeviceViewHolder viewHolder, Boolean isOn) {
                if( isOn ) {
                    if( viewHolder.switch_air_mouse.isEnabled() ){
                        viewHolder.switch_air_mouse.setChecked(false);
                    }
                    device.setOnPressureReady( v -> {
                        ((Activity)(getContext())).runOnUiThread(()-> {
                            viewHolder.pressure_value.setText(String.format("%.1f", v));
                            viewHolder.progress_bar.setProgress((int)(v*100));
                        });
                    });
                } else {
                    device.setOnPressureReady(null);
                }
            }

            @Override
            public void switchAirMouse(MudraDevice device, ConnectedDeviceViewHolder viewHolder, Boolean isOn) {
                if ( isOn ) {
                    device.setFirmwareOnAndroidMode();
                    if( viewHolder.switch_pressure.isEnabled()){
                        viewHolder.switch_pressure.setChecked(false);
                    }
                }
                device.setAirMouseActive(isOn);
            }
        };
    }

    private void updatePairedDevicesRecycler(){
        pairedDevices.clear();
        pairedDevices.addAll(Mudra.getInstance().getBondedDevices(getContext()));
        binding.PairedDevicesRecycler.setAdapter(pairedDeviceAdapter);
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
        updatePairedDevicesRecycler();
        updateConnectedDevicesRecycler();
        setMudraDelegate();
    }

    @Override
    public void onPause() {
        super.onPause();
        for(MudraDevice device : connectedDevices){
            device.setOnPressureReady(null);
        }
    }

    private void setMudraDelegate() {
        Mudra.getInstance().setMudraDelegate(new MudraDelegate() {
            @Override
            public void onDeviceDiscovered(MudraDevice mudraDevice) {

            }

            @Override
            public void onDeviceConnected(MudraDevice mudraDevice) {
                ((Activity)(getContext())).runOnUiThread(()-> {
                    if( !connectedDevices.contains(mudraDevice) ){
                        connectedDevices.add(mudraDevice);
                    }
                    updateConnectedDevicesRecycler();
                    updatePairedDevicesRecycler();
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