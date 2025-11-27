package com.example.androidexample;

import static android.view.View.INVISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidexample.adapters.UnconnectedDeviceAdapter;
import com.example.androidexample.databinding.ConnectedDeviceRowBinding;
import com.example.androidexample.databinding.FragmentFirstBinding;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import mudraAndroidSDK.enums.AirMouseCommand;
import mudraAndroidSDK.enums.DeviceMode;
import mudraAndroidSDK.enums.FirmwareTarget;
import mudraAndroidSDK.enums.GestureType;
import mudraAndroidSDK.enums.HandType;
import mudraAndroidSDK.interfaces.MudraDelegate;
import mudraAndroidSDK.interfaces.callback.OnBatteryLevelChanged;
import mudraAndroidSDK.interfaces.callback.OnButtonChanged;
import mudraAndroidSDK.interfaces.callback.OnFirmwareStatusChanged;
import mudraAndroidSDK.interfaces.callback.OnFirmwareVersionRead;
import mudraAndroidSDK.interfaces.callback.OnGestureReady;
import mudraAndroidSDK.interfaces.callback.OnImuAccRawReady;
import mudraAndroidSDK.interfaces.callback.OnImuGyroReady;
import mudraAndroidSDK.interfaces.callback.OnImuQuaternionReady;
import mudraAndroidSDK.interfaces.callback.OnNavigationReady;
import mudraAndroidSDK.interfaces.callback.OnPressureReady;
import mudraAndroidSDK.interfaces.callback.OnSncReady;
import mudraAndroidSDK.model.FirmwareStatus;
import mudraAndroidSDK.model.Mudra;
import mudraAndroidSDK.model.MudraDevice;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ConnectedDeviceRowBinding connectedDeviceBinding;

    private final ArrayList<MudraDevice> pairedDevices = new ArrayList<>();
    private MudraDevice connectedDevices;

    private UnconnectedDeviceAdapter pairedDeviceAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        View includedView = binding.connectedDeviceScrollView.getChildAt(0);
        connectedDeviceBinding = ConnectedDeviceRowBinding.bind(includedView);

        setupConnectedDeviceListeners();

        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupConnectedDeviceListeners() {
        // Disconnect Button
        connectedDeviceBinding.connectedDeviceDisconnectDeviceButton.setOnClickListener(v -> {
            // Handle disconnect
            disconnectDevice();
        });

        // SNC Switch
        connectedDeviceBinding.sncSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON SNC
                enableSNC();
            } else {
                // Turn OFF SNC
                disableSNC();
            }
        });

        // Accelerometer Switch
        connectedDeviceBinding.accSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Accelerometer
                enableAccelerometer();
            } else {
                // Turn OFF Accelerometer
                disableAccelerometer();
            }
        });

        // Gyroscope Switch
        connectedDeviceBinding.gyroSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Gyroscope
                enableGyroscope();
            } else {
                // Turn OFF Gyroscope
                disableGyroscope();
            }
        });

        // Quaternium Switch
        connectedDeviceBinding.quaterniumSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Quaternium
                enableQuaternium();
            } else {
                // Turn OFF Quaternium
                disableQuaternium();
            }
        });

        // Pressure Switch
        connectedDeviceBinding.pressureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Pressure
                enablePressure();
            } else {
                // Turn OFF Pressure
                disablePressure();
            }
        });

        // Gesture Switch
        connectedDeviceBinding.gestureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Gesture
                enableGesture();
            } else {
                // Turn OFF Gesture
                disableGesture();
            }
        });

        // Gesture HID Switch
        connectedDeviceBinding.gestureHidSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Gesture HID
                enableGestureHID();
            } else {
                // Turn OFF Gesture HID
                disableGestureHID();
            }
        });

        // Navigation Switch
        connectedDeviceBinding.navigationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Navigation
                enableNavigation();
            } else {
                // Turn OFF Navigation
                disableNavigation();
            }
        });

        // Navigation App Switch
        connectedDeviceBinding.navigationAppSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Navigation App
                enableNavigationApp();
            } else {
                // Turn OFF Navigation App
                disableNavigationApp();
            }
        });

        // Navigation HID Switch
        connectedDeviceBinding.navigationHidSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Navigation HID
                enableNavigationHID();
            } else {
                // Turn OFF Navigation HID
                disableNavigationHID();
            }
        });

        // Air Touch Switch
        connectedDeviceBinding.airtouchSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Air Touch
                enableAirTouch();
            } else {
                // Turn OFF Air Touch
                disableAirTouch();
            }
        });

        connectedDeviceBinding.handLeftContainer.setOnClickListener(v -> {
            setHandType(HandType.LEFT);
        });

        // Right hand selection
        connectedDeviceBinding.handRightContainer.setOnClickListener(v -> {
            setHandType(HandType.RIGHT);
        });

        connectedDeviceBinding.cursorSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int speed = seekBar.getProgress();
                setPointerSpeed(speed);
            }
        });

    }

    private void setHandType(HandType handType) {
        connectedDevices.setHand(handType);
    }

    private void setPointerSpeed(int speed){
        connectedDevices.setPointerSpeed(speed);
    }

    private void disconnectDevice() {
        connectedDevices.disconnect();
    }

    private void enableSNC() {
        connectedDevices.setOnSncReady(new OnSncReady() {
            @Override
            public void run(long timestamp, float[] data, int frequency, float frequencySTD, float[] rms) {
                requireActivity().runOnUiThread(() -> {
                    if (connectedDeviceBinding == null) return;

                    // Update RMS values (0-100 range for progress bars)
                    if (rms != null && rms.length >= 3) {
                        // Convert from 0-1 to 0-100 and clamp values
                        int rms1 = (int) Math.min(100, Math.max(0, rms[0] * 100));
                        int rms2 = (int) Math.min(100, Math.max(0, rms[1] * 100));
                        int rms3 = (int) Math.min(100, Math.max(0, rms[2] * 100));

                        connectedDeviceBinding.sncRms1Progress.setProgress(rms1);
                        connectedDeviceBinding.sncRms1Value.setText(String.valueOf(rms1));

                        connectedDeviceBinding.sncRms2Progress.setProgress(rms2);
                        connectedDeviceBinding.sncRms2Value.setText(String.valueOf(rms2));

                        connectedDeviceBinding.sncRms3Progress.setProgress(rms3);
                        connectedDeviceBinding.sncRms3Value.setText(String.valueOf(rms3));
                    }

                    // Update frequency values
                    connectedDeviceBinding.sncFrequency.setText(String.valueOf(frequency));
                    connectedDeviceBinding.sncFrequencyStd.setText(String.format("%.2f", frequencySTD));
                });
            }
        });
    }

    private void disableSNC() {
        connectedDevices.setOnSncReady(null);
        // Clear SNC values
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.sncRms1Progress.setProgress(0);
                connectedDeviceBinding.sncRms1Value.setText("0");
                connectedDeviceBinding.sncRms2Progress.setProgress(0);
                connectedDeviceBinding.sncRms2Value.setText("0");
                connectedDeviceBinding.sncRms3Progress.setProgress(0);
                connectedDeviceBinding.sncRms3Value.setText("0");
                connectedDeviceBinding.sncFrequency.setText("0");
                connectedDeviceBinding.sncFrequencyStd.setText("0");
            });
        }
    }

    private void enableAccelerometer() {
        connectedDevices.setOnImuAccRawReady(new OnImuAccRawReady() {
            @Override
            public void run(long timestamp, float[] data, int frequency, float frequencySTD) {
                requireActivity().runOnUiThread(() -> {
                    if (connectedDeviceBinding == null) return;

                    // Update frequency values
                    connectedDeviceBinding.accFrequency.setText(String.valueOf(frequency));
                    connectedDeviceBinding.accFrequencyStd.setText(String.format("%.2f", frequencySTD));
                });
            }
        });
    }

    private void disableAccelerometer() {
        connectedDevices.setOnImuAccRawReady(null);
        // Clear Accelerometer values
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.accFrequency.setText("0");
                connectedDeviceBinding.accFrequencyStd.setText("0");
            });
        }
    }

    private void enableGyroscope() {
        connectedDevices.setOnImuGyroReady(new OnImuGyroReady() {
            @Override
            public void run(long timestamp, float[] data, int frequency, float frequencySTD) {
                requireActivity().runOnUiThread(() -> {
                    if (connectedDeviceBinding == null) return;

                    // Update frequency values
                    connectedDeviceBinding.gyroFrequency.setText(String.valueOf(frequency));
                    connectedDeviceBinding.gyroFrequencyStd.setText(String.format("%.2f", frequencySTD));
                });
            }
        });
    }

    private void disableGyroscope() {
        connectedDevices.setOnImuGyroReady(null);
        // Clear Gyroscope values
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.gyroFrequency.setText("0");
                connectedDeviceBinding.gyroFrequencyStd.setText("0");
            });
        }
    }

    private void enableQuaternium() {
        connectedDevices.setOnImuQuaternionReady(new OnImuQuaternionReady() {
            @Override
            public void run(long timestamp, float[] data, int frequency, float frequencySTD) {
                //DATA -> [0] : x / [1] : y / [2] : z / [3] : w /
                requireActivity().runOnUiThread(() -> {
                    if (connectedDeviceBinding == null) return;

                    // Update quaternion values
                    if (data != null && data.length >= 4) {
                        connectedDeviceBinding.quatX.setText(String.format("%.3f", data[0]));
                        connectedDeviceBinding.quatY.setText(String.format("%.3f", data[1]));
                        connectedDeviceBinding.quatZ.setText(String.format("%.3f", data[2]));
                        connectedDeviceBinding.quatW.setText(String.format("%.3f", data[3]));
                    }

                    // Update frequency values
                    connectedDeviceBinding.quatFrequency.setText(String.valueOf(frequency));
                    connectedDeviceBinding.quatFrequencyStd.setText(String.format("%.2f", frequencySTD));
                });
            }
        });
    }

    private void disableQuaternium() {
        connectedDevices.setOnImuQuaternionReady(null);
        // Clear Quaternium values
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.quatX.setText("0");
                connectedDeviceBinding.quatY.setText("0");
                connectedDeviceBinding.quatZ.setText("0");
                connectedDeviceBinding.quatW.setText("0");
                connectedDeviceBinding.quatFrequency.setText("0");
                connectedDeviceBinding.quatFrequencyStd.setText("0");
            });
        }
    }

    private void enablePressure() {
        connectedDevices.setOnPressureReady(new OnPressureReady() {
            @Override
            public void run(float pressure) {
                requireActivity().runOnUiThread(() -> {
                    if (connectedDeviceBinding == null) return;

                    // Update pressure value (0-100 range for progress bar)
                    int pressureValue = (int) Math.min(100, Math.max(0, pressure*100));
                    connectedDeviceBinding.pressureProgress.setProgress(pressureValue);
                    connectedDeviceBinding.pressureValue.setText(String.valueOf(pressureValue));
                });
            }
        });
    }

    private void disablePressure() {
        connectedDevices.setOnPressureReady(null);
        // Clear Pressure values
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.pressureProgress.setProgress(0);
                connectedDeviceBinding.pressureValue.setText("0");
            });
        }
    }

    private Handler gestureHandler = new Handler(Looper.getMainLooper());
    private Runnable clearGestureRunnable = new Runnable() {
        @Override
        public void run() {
            if (connectedDeviceBinding != null) {
                connectedDeviceBinding.gestureType.setText("");
            }
        }
    };

    private void enableGesture() {
        connectedDevices.setOnGestureReady(new OnGestureReady() {
            @Override
            public void run(GestureType gestureType) {
                requireActivity().runOnUiThread(() -> {
                    connectedDeviceBinding.gestureType.setText(gestureType.name());

                    // Cancel any pending clear operations
                    gestureHandler.removeCallbacks(clearGestureRunnable);

                    // Schedule clearing the text after 1 second
                    gestureHandler.postDelayed(clearGestureRunnable, 1000);
                });
            }
        });

        connectedDevices.setOnButtonChanged(new OnButtonChanged() {
            @Override
            public void run(AirMouseCommand airMouseCommand) {
                requireActivity().runOnUiThread(() -> {

                    // Press = Green, Release = Gray
                    int color = (airMouseCommand == AirMouseCommand.Press)
                            ? android.R.color.holo_blue_bright
                            : android.R.color.darker_gray;

                    connectedDeviceBinding.gestureImage.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
                    );
                });
            }
        });
    }

    private void disableGesture() {
        connectedDevices.setOnGestureReady(null);
        connectedDevices.setOnButtonChanged(null);

        // Clear Gesture values
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.gestureType.setText("");
                // Reset image to default state
                connectedDeviceBinding.gestureImage.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                );
                connectedDeviceBinding.gestureImage.setImageDrawable(null);
            });
        }
    }

    private void enableGestureHID() {
        connectedDevices.setFirmwareTarget(FirmwareTarget.GESTURE_TO_HID, true);
    }

    private void disableGestureHID() {
        connectedDevices.setFirmwareTarget(FirmwareTarget.GESTURE_TO_HID, false);
    }

    private void enableNavigation() {
        connectedDevices.setOnNavigationReady(new OnNavigationReady() {
            @Override
            public void run(int deltaX, int deltaY) {
                requireActivity().runOnUiThread(() -> {
                    if (connectedDeviceBinding == null) return;

                    // Update delta text values
                    connectedDeviceBinding.navigationDeltaX.setText(String.valueOf(deltaX));
                    connectedDeviceBinding.navigationDeltaY.setText(String.valueOf(deltaY));

                    // Update the red dot position
                    updateNavigationDotPosition(deltaX, deltaY);
                });
            }
        });
    }

    private void updateNavigationDotPosition(int deltaX, int deltaY) {
        View dot = connectedDeviceBinding.navigationPositionDot;
        FrameLayout parent = (FrameLayout) dot.getParent();

        if (parent == null) return;

        // Get the dimensions of the container
        int containerWidth = parent.getWidth();
        int containerHeight = parent.getHeight();

        if (containerWidth == 0 || containerHeight == 0) {
            // Layout not ready yet, try again after layout
            parent.post(() -> updateNavigationDotPosition(deltaX, deltaY));
            return;
        }

        // Define scale factor - adjust this to control sensitivity
        // Smaller scale = less movement, larger scale = more movement
        float scaleFactor = 2.0f;

        // Calculate the offset from center (in pixels)
        // Clamp values to prevent dot from going outside the container
        float maxOffsetX = containerWidth / 2f - dot.getWidth() / 2f;
        float maxOffsetY = containerHeight / 2f - dot.getHeight() / 2f;

        float offsetX = Math.max(-maxOffsetX, Math.min(maxOffsetX, deltaX * scaleFactor));
        float offsetY = Math.max(-maxOffsetY, Math.min(maxOffsetY, -deltaY * scaleFactor)); // Negative because Y axis is inverted in Android

        // Animate the dot to the new position
        dot.animate()
                .translationX(offsetX)
                .translationY(offsetY)
                .setDuration(50) // Fast animation for smooth movement
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    private void disableNavigation() {
        connectedDevices.setOnNavigationReady(null);

        // Clear Navigation values and reset dot position
        if (connectedDeviceBinding != null) {
            requireActivity().runOnUiThread(() -> {
                connectedDeviceBinding.navigationDeltaX.setText("0");
                connectedDeviceBinding.navigationDeltaY.setText("0");

                // Reset dot to center
                connectedDeviceBinding.navigationPositionDot.animate()
                        .translationX(0)
                        .translationY(0)
                        .setDuration(200)
                        .start();
            });
        }
    }

    private void enableNavigationApp() {
        connectedDevices.setFirmwareTarget(FirmwareTarget.NAVIGATION_TO_APP, true);
    }

    private void disableNavigationApp() {
        connectedDevices.setFirmwareTarget(FirmwareTarget.NAVIGATION_TO_APP, false);
    }

    private void enableNavigationHID() {
        connectedDevices.setFirmwareTarget(FirmwareTarget.NAVIGATION_TO_HID, true);
    }

    private void disableNavigationHID() {
        connectedDevices.setFirmwareTarget(FirmwareTarget.NAVIGATION_TO_HID, false);
    }

    private void enableAirTouch() {
        connectedDevices.setAirTouchActive(true);
    }

    private void disableAirTouch() {
        connectedDevices.setAirTouchActive(false);
    }

    private void enableOnFirmwareStatusUpdated() {
        connectedDevices.setOnFirmwareStatusChanged(new OnFirmwareStatusChanged() {
            @Override
            public void run(boolean updated) {
                updateUIFromFirmwareStatus(connectedDevices.getFirmwareStatus());
            }
        });
    }

    private void updateUIFromFirmwareStatus(FirmwareStatus firmwareStatus) {
        // Run on UI thread to ensure safe UI updates
        requireActivity().runOnUiThread(() -> {
            if (connectedDeviceBinding == null) return;

            // Update SNC
            boolean sncEnabled = firmwareStatus.isSncEnabled();
            updateIndicator(connectedDeviceBinding.sncIndicator, sncEnabled);

            // Update Accelerometer
            boolean accEnabled = firmwareStatus.isAccEnabled();
            updateIndicator(connectedDeviceBinding.accIndicator, accEnabled);

            // Update Gyroscope
            boolean gyroEnabled = firmwareStatus.isGyroEnabled();
            updateIndicator(connectedDeviceBinding.gyroIndicator, gyroEnabled);

            // Update Quaternium
            boolean quaternionEnabled = firmwareStatus.isQuaternionEnabled();
            updateIndicator(connectedDeviceBinding.quaterniumIndicator, quaternionEnabled);

            // Update Pressure
            boolean pressureEnabled = firmwareStatus.isPressureEnabled();
            updateIndicator(connectedDeviceBinding.pressureIndicator, pressureEnabled);

            // Update Gesture
            boolean gestureEnabled = firmwareStatus.isGestureEnabled();
            updateIndicator(connectedDeviceBinding.gestureIndicator, gestureEnabled);

            // Update Gesture HID
            boolean gestureHidEnabled = firmwareStatus.isSendsGestureToHIDEnabled();
            updateIndicator(connectedDeviceBinding.gestureHidIndicator, gestureHidEnabled);

            // Update Navigation
            boolean navigationEnabled = firmwareStatus.isNavigationEnabled();
            updateIndicator(connectedDeviceBinding.navigationIndicator, navigationEnabled);

            // Update Navigation App
            boolean navigationAppEnabled = firmwareStatus.isSendsNavigationToAppEnabled();
            updateIndicator(connectedDeviceBinding.navigationAppIndicator, navigationAppEnabled);

            // Update Navigation HID
            boolean navigationHidEnabled = firmwareStatus.isSendsNavigationToHIDEnabled();
            updateIndicator(connectedDeviceBinding.navigationHidIndicator, navigationHidEnabled);

            // Update Air Touch
            boolean airTouchEnabled = firmwareStatus.isAirTouchEnabled();
            updateIndicator(connectedDeviceBinding.airtouchIndicator, airTouchEnabled);

            // Update Hand Type
            updateHandIndicator(firmwareStatus.getHandType());

            // Update Pointer Speed
            UpdatePointerSpeedIndicator(firmwareStatus.getPointerSpeedX());
        });
    }

    private void disableOnFirmwareStatusUpdated() {
        connectedDevices.setOnFirmwareStatusChanged(null);
    }

    private void UpdatePointerSpeedIndicator(int pointerSpeedX) {
        connectedDeviceBinding.cursorSpeedValue.setText(String.valueOf(pointerSpeedX));
    }

    private void enableOnFirmwareVersionUpdated() {
        connectedDevices.setOnFirmwareVersionRead(new OnFirmwareVersionRead() {
            @Override
            public void run(String firmwareVersion) {
                connectedDeviceBinding.connectedDeviceFirmwareValue.setText(firmwareVersion);
            }
        });
    }

    private void disableOnFirmwareVersionUpdated() {
        connectedDevices.setOnFirmwareVersionRead(null);
    }

    private void enableOnBatteryLevelChanged() {
        connectedDevices.setOnBatteryLevelChanged(new OnBatteryLevelChanged() {
            @Override
            public void run(int battery) {
                connectedDeviceBinding.connectedDeviceBatteryValue.setText(String.valueOf(battery));
            }
        });
    }

    private void disableOnBatteryLevelChanged() {
        connectedDevices.setOnBatteryLevelChanged(null);
    }

    private void updateIndicator(View indicator, boolean isActive) {
        if (isActive) {
            indicator.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_light)
            ));
        } else {
            indicator.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            ));
        }
    }

    private void updateHandIndicator(HandType handType){
        if (handType == HandType.LEFT) {
            // Left is selected - green indicator
            connectedDeviceBinding.handLeftIndicator.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
            );
            // Right is not selected - gray indicator
            connectedDeviceBinding.handRightIndicator.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            );
        } else {
            // Right is selected - green indicator
            connectedDeviceBinding.handRightIndicator.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
            );
            // Left is not selected - gray indicator
            connectedDeviceBinding.handLeftIndicator.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            );
        }
    }

    private void updateOnDeviceConnected(){
        enableOnFirmwareStatusUpdated();
        enableOnFirmwareVersionUpdated();
        enableOnBatteryLevelChanged();
        requireActivity().runOnUiThread(() -> {
            connectedDeviceBinding.connectedDeviceFirmwareValue.setText(connectedDevices.getFirmwareVersion());
            connectedDeviceBinding.connectedDeviceBatteryValue.setText(String.valueOf(connectedDevices.getBatteryLevel()));
        });
    }

    private void updateOnDeviceDisconnected(){
        disableOnFirmwareStatusUpdated();
        disableOnFirmwareVersionUpdated();
        disableOnBatteryLevelChanged();
    }

    private void setAdapters() {
        binding.PairedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.PairedDevicesRecycler.setHasFixedSize(true);
        pairedDeviceAdapter = new UnconnectedDeviceAdapter(pairedDevices, getContext()) {
            @Override
            public void connectDevice(MudraDevice device) {
                device.connectDevice(getContext());
            }
        };
    }

    private void updatePairedDevicesRecycler(){
        pairedDevices.clear();
        pairedDevices.addAll(Mudra.getInstance().getBondedDevices(getContext()));
        binding.PairedDevicesRecycler.setAdapter(pairedDeviceAdapter);
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
        setMudraDelegate();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setMudraDelegate() {
        Mudra.getInstance().setMudraDelegate(new MudraDelegate() {
            @Override
            public void onDeviceDiscovered(MudraDevice mudraDevice) {

            }

            @Override
            public void onDeviceConnecting(MudraDevice mudraDevice) {

            }

            @Override
            public void onDeviceConnectedByAndroidOS(MudraDevice mudraDevice) {

            }

            @Override
            public void onDeviceFailedToConnect(MudraDevice mudraDevice) {

            }

            @Override
            public void onDeviceConnected(MudraDevice mudraDevice) {
                binding.connectedDeviceScrollView.setVisibility(View.VISIBLE);
                binding.PairedDevicesRecycler.setVisibility(View.INVISIBLE);
                connectedDevices = mudraDevice;
                updateOnDeviceConnected();
            }

            @Override
            public void onDeviceDisconnecting(MudraDevice mudraDevice) {

            }

            @Override
            public void onDeviceDisconnected(MudraDevice mudraDevice) {
                binding.connectedDeviceScrollView.setVisibility(View.INVISIBLE);
                updateOnDeviceDisconnected();
                connectedDevices = null;
            }
        });
    }
}