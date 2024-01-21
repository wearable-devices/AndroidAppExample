package com.example.androidexample.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.ArrayList;
import java.util.HashMap;

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
        holder.switch_pressure.setOnCheckedChangeListener(switchListener(devices.get(position), holder.pressure_value));
        holder.battery_value.setText(Integer.toString(devices.get(position).getBatteryLevel()));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public abstract void disconnectDevice(MudraDevice device);
    public abstract void switchPressure(MudraDevice device, TextView textView, Boolean isOn);

    private CompoundButton.OnCheckedChangeListener switchListener(MudraDevice mudraDevice, TextView textView){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                switchPressure(mudraDevice, textView , isOn);
            }
        };
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public static class ConnectedDeviceViewHolder extends RecyclerView.ViewHolder
    {
        TextView device_name;
        Button disconnect_device_button;
        Switch switch_pressure;
        TextView pressure_value;
        TextView battery_value;

        public ConnectedDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            device_name = itemView.findViewById(R.id.connected_device_device_name);
            disconnect_device_button = itemView.findViewById(R.id.connected_device_disconnect_device_button);
            switch_pressure = itemView.findViewById(R.id.connected_device_switch_pressure);
            pressure_value = itemView.findViewById(R.id.connected_device_pressure_value);
            battery_value = itemView.findViewById(R.id.connected_device_battery_value);
        }
    }
}
