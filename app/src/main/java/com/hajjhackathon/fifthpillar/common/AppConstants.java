package com.hajjhackathon.fifthpillar.common;

import android.os.ParcelUuid;

public class AppConstants {

    /**
     * UUID identified with this app - set as Service UUID for BLE Advertisements.
     * Bluetooth requires a certain format for UUIDs associated with Services.
     */
    public static final ParcelUuid Service_UUID = ParcelUuid.fromString("0000b81d-0000-1000-8000-00805f9b34fb");

    public static final int REQUEST_ENABLE_BT = 1;
    public static final String NOTIFICATION_CHANNEL_GENERAL = "notify.channel.general";

    public static final int REQUEST_LOCATION_PERMISSION = 101;
    public static final int REQUEST_LOCATION_PERMISSION_START_SCANNING = 102;

    public static final String KEY_ADVERTISE_DATA = "key.advertise.data";
}
