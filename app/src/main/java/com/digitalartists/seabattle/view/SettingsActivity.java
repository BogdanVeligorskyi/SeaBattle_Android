package com.digitalartists.seabattle.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.digitalartists.seabattle.R;
import com.digitalartists.seabattle.model.FileProcessing;
import com.digitalartists.seabattle.model.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Settings Activity class
public class SettingsActivity extends AppCompatActivity {

    // settings object
    private Settings settings;
    private boolean isPaired;
    private BluetoothAdapter bluetoothAdapter;

    private ListView listPairedDevices;
    private ListView listAvailableDevices;

    private ArrayAdapter<String> adapterPairedDevices;
    private ArrayAdapter<String> adapterAvailableDevices;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);
        Context context = getApplicationContext();
        if (savedInstanceState != null) {
            settings = savedInstanceState.getParcelable(MainActivity.SETTINGS);
        } else {
            try {
                settings = FileProcessing.loadSettings(context);
                isPaired = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        IntentFilter intentFilter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter1);
        IntentFilter intentFilter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intentFilter2);

        listPairedDevices = findViewById(R.id.pairedDevices_id);
        listAvailableDevices = findViewById(R.id.availableDevices_id);

        adapterPairedDevices = new ArrayAdapter<String>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapterAvailableDevices = new ArrayAdapter<String>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        listPairedDevices.setAdapter(adapterPairedDevices);
        listAvailableDevices.setAdapter(adapterAvailableDevices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // prompt to enable Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Log.d("HERE_1", "ghghg");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
                Log.d("HERE_2", "ghghg");

            }
            Intent blueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueToothIntent, 0);
        }


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices != null && pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices)
                adapterPairedDevices.add(bt.getName() + "\n" + bt.getAddress());
        }

        TextView textViewIsPaired = findViewById(R.id.isPairedState_id);
        if (isPaired) {
            textViewIsPaired.setTextColor(Color.GREEN);
            textViewIsPaired.setText("State: paired");
        } else {
            textViewIsPaired.setTextColor(Color.RED);
            textViewIsPaired.setText("State: not paired");
        }

        if (!bluetoothAdapter.isDiscovering()) {
            Log.d("DISCOVERING (1)", "...");
            bluetoothAdapter.startDiscovery();
        }

        if (bluetoothAdapter.isDiscovering()) {
            Log.d("DISCOVERING (2)", "...");
        }
        //Intent startBluetooth = new Intent(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //startActivityForResult(startBluetooth, 0);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch isDarkModeOn = findViewById(R.id.switch_id);
        if (settings.getIsDarkMode() == 1) {
            isDarkModeOn.setChecked(true);
        }

        // handler for 'Save' button
        findViewById(R.id.saveSettings_id).setOnClickListener(butPlay -> {
            if (isDarkModeOn.isChecked()) {
                settings.setIsDarkMode(1);
            } else {
                settings.setIsDarkMode(0);
            }

            // save settings to file if data are correct
            try {
                FileProcessing.saveSettings(context, settings);
            } catch (IOException e) {
                e.printStackTrace();
            }

            finish();
        });

    }


    // save window state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.SETTINGS, settings);
    }


    // after return to this activity
    @Override
    public void onResume() {
        super.onResume();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    adapterAvailableDevices.add(device.getName() + "\n" + device.getAddress());
                }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (adapterAvailableDevices.getCount() == 0) {

                }
            }
        }
    };



}
