package com.hajjhackathon.fifthpillar.broadcast.ui;

import android.annotation.SuppressLint;
import android.bluetooth.le.AdvertiseCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hajjhackathon.fifthpillar.R;
import com.hajjhackathon.fifthpillar.broadcast.service.AdvertisementService;
import com.hajjhackathon.fifthpillar.common.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hajjhackathon.fifthpillar.common.AppConstants.KEY_ADVERTISE_DATA;


@SuppressLint("InlinedApi")
public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver advertisingFailureReceiver;

    private boolean isAdvertising = false;
    // Initialize random Haji ID
    private String randomHajjId = Utils.getRandomHajjID();

    @BindView(R.id.txtBroadcastID)
    TextView txtBroadcastID;
    @BindView(R.id.btnSendData)
    Button btnSendData;
    @BindView(R.id.btnStopData)
    Button btnStopData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_broadcast);
        ButterKnife.bind(this);

        txtBroadcastID.setText(getString(R.string.next_id, randomHajjId));

        advertisingFailureReceiver = new BroadcastReceiver() {

            /**
             * Receives Advertising error codes from {@code AdvertiserService} and displays error messages
             * to the user. Sets the advertising toggle to 'false.'
             */
            @Override
            public void onReceive(Context context, Intent intent) {

                int errorCode = intent.getIntExtra(AdvertisementService.ADVERTISING_FAILED_EXTRA_CODE, -1);

                String errorMessage = getString(R.string.start_error_prefix);
                switch (errorCode) {
                    case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                        errorMessage += " " + getString(R.string.start_error_already_started);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                        errorMessage += " " + getString(R.string.start_error_too_large);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                        errorMessage += " " + getString(R.string.start_error_unsupported);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                        errorMessage += " " + getString(R.string.start_error_internal);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                        errorMessage += " " + getString(R.string.start_error_too_many);
                        break;
                    case AdvertisementService.ADVERTISING_TIMED_OUT:
                        errorMessage = " " + getString(R.string.advertising_timedout);
                        break;
                    default:
                        errorMessage += " " + getString(R.string.start_error_unknown);
                }

                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        };
    }


    @OnClick(R.id.btnSendData)
    public void startAdvertising(View view) {
        if(!isAdvertising){
            Intent newIntent = new Intent(MainActivity.this, AdvertisementService.class);
            newIntent.putExtra(KEY_ADVERTISE_DATA, randomHajjId);
            startService(newIntent);
            btnSendData.setVisibility(View.GONE);
            btnStopData.setVisibility(View.VISIBLE);
            isAdvertising = true;
        }
    }

    @OnClick(R.id.btnStopData)
    public void stopAdvertising(View view) {
        if(isAdvertising){
            stopService(new Intent(MainActivity.this, AdvertisementService.class));
            randomHajjId = Utils.getRandomHajjID();
            txtBroadcastID.setText(getString(R.string.next_id, randomHajjId));
            btnSendData.setVisibility(View.VISIBLE);
            btnStopData.setVisibility(View.GONE);
            isAdvertising = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter failureFilter = new IntentFilter(AdvertisementService.ADVERTISING_FAILED);
        registerReceiver(advertisingFailureReceiver, failureFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(advertisingFailureReceiver);
    }

}
