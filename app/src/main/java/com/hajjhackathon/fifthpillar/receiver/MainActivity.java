package com.hajjhackathon.fifthpillar.receiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.hajjhackathon.fifthpillar.R;
import com.hajjhackathon.fifthpillar.common.AppConstants;
import com.hajjhackathon.fifthpillar.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hajjhackathon.fifthpillar.common.AppConstants.REQUEST_LOCATION_PERMISSION;
import static com.hajjhackathon.fifthpillar.common.AppConstants.REQUEST_LOCATION_PERMISSION_START_SCANNING;


@SuppressLint("InlinedApi")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.txtReceived)
    TextView txtReceived;
    @BindView(R.id.btnStartReceiving)
    Button btnStartReceiving;
    @BindView(R.id.btnStopReceiving)
    Button btnStopReceiving;

    ArrayList<String> lstHajjIDs = new ArrayList<>();
    /**
     * Stops scanning after 5 seconds.
     */
    private static final long SCAN_PERIOD = 5000;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeScanner mBluetoothLeScanner;

    private ScanCallback mScanCallback;

    private Handler mHandler;

    private Runnable runnable;

    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_receiver);
        ButterKnife.bind(this);
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        // Is Bluetooth supported on this device?
        if (mBluetoothAdapter != null) {

            // Is Bluetooth turned on?
            if (mBluetoothAdapter.isEnabled()) {

                // Are Bluetooth Advertisements supported on this device?
                if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                    // Bluetooth Advertisements are not supported.
                    Toast.makeText(this, R.string.bt_ads_not_supported, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, AppConstants.REQUEST_ENABLE_BT);
            }
        } else {
            // Bluetooth is not supported.
            Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_SHORT).show();
        }
        mHandler = new Handler();
        checkLocationPermission(false);
    }

    @OnClick(R.id.btnStartReceiving)
    public void startReceiving(View view) {
        checkLocationPermission(true);
    }

    @OnClick(R.id.btnStopReceiving)
    public void stopReceiving(View view) {
        stopScanning();
    }

    private void checkLocationPermission(boolean startScanning){
        if (!Utils.isGpsPermissionGranted(this)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        startScanning ? REQUEST_LOCATION_PERMISSION_START_SCANNING : REQUEST_LOCATION_PERMISSION);
            }
        }else if(startScanning){
            startScanning();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // Bluetooth is now Enabled, are Bluetooth Advertisements supported on
                    // this device?
                    if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                        // Bluetooth Advertisements are not supported.
                        Toast.makeText(this, R.string.bt_ads_not_supported, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User declined to enable Bluetooth, exit the app.
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_LOCATION_PERMISSION_START_SCANNING && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startScanning();
        }
    }

    /**
     * Start scanning for BLE Advertisements (& set it up to stop after a set period of time).
     */
    public void startScanning() {
        if (mScanCallback == null) {
            btnStartReceiving.setVisibility(View.GONE);
            btnStopReceiving.setVisibility(View.VISIBLE);
            Log.d(TAG, "Starting Scanning");

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtReceived.setText(getString(R.string.received_count, String.valueOf(lstHajjIDs.size())));
                        }
                    });
                    lstHajjIDs.clear();
                    // Stop scan
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    mScanCallback = null;
                    // Kick off a new scan.
                    mScanCallback = new SampleScanCallback();
                    mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
                }
            }, 0, SCAN_PERIOD);

            /*mHandler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtReceived.setText(getString(R.string.received_count, String.valueOf(lstHajjIDs.size())));
                            lstHajjIDs.clear();

                        }
                    });
                    mHandler.postDelayed(runnable, SCAN_PERIOD);
                }
            }, SCAN_PERIOD);*/
        }
    }

    /**
     * Stop scanning for BLE Advertisements.
     */
    public void stopScanning() {
        Log.d(TAG, "Stopping Scanning");
        // Stop the scan, wipe the callback.
        mBluetoothLeScanner.stopScan(mScanCallback);
        mScanCallback = null;
        //mHandler.removeCallbacks(runnable);
        timer.cancel();
        btnStartReceiving.setVisibility(View.VISIBLE);
        btnStopReceiving.setVisibility(View.GONE);
    }

    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        //builder.setServiceUuid(AppConstants.Service_UUID);
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }

    /**
     * Custom ScanCallback object - adds to adapter on success, displays error on failure.
     */
    private class SampleScanCallback extends ScanCallback {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            for (ScanResult result : results) {
                lstHajjIDs.add("hello");
                //lstHajjIDs.add(new String(result.getScanRecord().getServiceData(AppConstants.Service_UUID)));
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            lstHajjIDs.add("hello");
            //lstHajjIDs.add(new String(result.getScanRecord().getServiceData(AppConstants.Service_UUID)));
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(MainActivity.this, "Scan failed with error: " + errorCode, Toast.LENGTH_LONG).show();
        }
    }
}
