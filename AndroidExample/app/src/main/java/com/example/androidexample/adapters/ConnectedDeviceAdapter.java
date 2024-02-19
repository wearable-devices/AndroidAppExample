package com.example.androidexample.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.ArrayList;

import mudraAndroidSDK.interfaces.callback.OnFirmwareVersionRead;
import mudraAndroidSDK.model.MudraDevice;

public abstract class ConnectedDeviceAdapter extends RecyclerView.Adapter<ConnectedDeviceAdapter.ConnectedDeviceViewHolder>{

    private Context context;
    private ArrayList<MudraDevice> devices;

    public ConnectedDeviceAdapter(ArrayList<MudraDevice> devices, Context context){
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public ConnectedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.connected_device_row,parent,false);
        return new ConnectedDeviceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConnectedDeviceViewHolder holder, int position) {
        holder.device_name.setText(devices.get(position).getBluetoothDeviceName());
        holder.disconnect_device_button.setOnClickListener( v->disconnectDevice(devices.get(position)));
        holder.switch_pressure.setOnCheckedChangeListener(switchListenerForPressure(devices.get(position), holder));
        holder.switch_air_mouse.setOnCheckedChangeListener(switchListenerForAirMouse(devices.get(position), holder));
        holder.battery_value.setText(Integer.toString(devices.get(position).getBatteryLevel()));
        holder.firmware_value.setText(devices.get(position).getFirmwareVersion());
        holder.switch_pressure.setChecked(devices.get(position).isOnPressureReadySet());
        holder.switch_air_mouse.setChecked(devices.get(position).isAirMouseActive());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public abstract void disconnectDevice(MudraDevice device);
    public abstract void switchPressure(MudraDevice device, ConnectedDeviceViewHolder viewHolder, Boolean isOn);
    public abstract void switchAirMouse(MudraDevice device, ConnectedDeviceViewHolder viewHolder, Boolean isOn);

    private CompoundButton.OnCheckedChangeListener switchListenerForPressure(MudraDevice mudraDevice, ConnectedDeviceViewHolder viewHolder){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                switchPressure(mudraDevice, viewHolder , isOn);
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener switchListenerForAirMouse(MudraDevice mudraDevice, ConnectedDeviceViewHolder viewHolder){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                switchAirMouse(mudraDevice, viewHolder , isOn);
            }
        };
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public static class ConnectedDeviceViewHolder extends RecyclerView.ViewHolder
    {
        public TextView device_name;
        public Button disconnect_device_button;
        public Switch switch_pressure;
        public Switch switch_air_mouse;
        public TextView pressure_value;
        public ProgressBar progress_bar;
        public TextView battery_value;
        public TextView firmware_value;

        public ConnectedDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            device_name = itemView.findViewById(R.id.connected_device_device_name);
            disconnect_device_button = itemView.findViewById(R.id.connected_device_disconnect_device_button);
            switch_pressure = itemView.findViewById(R.id.connected_device_switch_pressure);
            switch_air_mouse = itemView.findViewById(R.id.connected_device_switch_airmouse);
            pressure_value = itemView.findViewById(R.id.connected_device_pressure_value);
            battery_value = itemView.findViewById(R.id.connected_device_battery_value);
            firmware_value = itemView.findViewById(R.id.connected_device_firmware_value);
            progress_bar = itemView.findViewById(R.id.connected_device_pressure_progress_bar);
            progress_bar.setMax(100);
        }
    }
}
