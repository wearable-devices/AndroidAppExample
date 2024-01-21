package com.example.androidexample.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.ArrayList;

import mudraAndroidSDK.model.MudraDevice;

public abstract class UnconnectedDeviceAdapter extends RecyclerView.Adapter<UnconnectedDeviceAdapter.UnconnectDeviceViewHolder>{

    private Context context;
    private ArrayList<MudraDevice> devices;

    public UnconnectedDeviceAdapter(ArrayList<MudraDevice> devices, Context context){
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public UnconnectDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.unconnected_device_row,parent,false);
        return new UnconnectDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnconnectDeviceViewHolder holder, int position) {
        holder.device_name.setText(devices.get(position).getBluetoothDeviceName());
        holder.connection_status.setTextColor((devices.get(position).isConnected() ? Color.GREEN : Color.RED));
        holder.connection_status.setText(devices.get(position).isConnected() ? "Connected" : "Disconnected");
        holder.connect_device_button.setOnClickListener(v -> connectDevice(devices.get(position)));
    }

    public abstract void connectDevice(MudraDevice device);

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class UnconnectDeviceViewHolder extends RecyclerView.ViewHolder
    {
        TextView device_name;
        TextView connection_status;
        Button connect_device_button;

        public UnconnectDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            device_name = itemView.findViewById(R.id.device_name);
            connect_device_button = itemView.findViewById(R.id.connect_device_button);
            connection_status = itemView.findViewById(R.id.unconnected_device_connection_status);
        }
    }
}
