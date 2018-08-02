package com.hajjhackathon.fifthpillar.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.Random;

public class Utils {

    private static String[] randomIDs = {"8765234", "7510736", "9132092", "2014362"};

    public static String getRandomHajjID(){
        Random random = new Random();
        return randomIDs[random.nextInt(3)];
    }

    public static boolean isGpsPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
