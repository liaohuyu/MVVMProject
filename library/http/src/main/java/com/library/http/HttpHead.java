package com.library.http;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * on 2020/4/20
 */
class HttpHead {
    private static final String CLIENT_TYPE = "4";
    private static Context context;

    public static void init(Context context) {
        HttpHead.context = context;
    }

}
