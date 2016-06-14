package com.globalpaysolutions.yovendosaldo.notifications;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Josué Chávez on 10/06/2016.
 */
public class YvsInstanceIDService extends InstanceIDListenerService
{
    private static final String TAG = "YvsInstanceIDService";

    @Override
    public void onTokenRefresh()
    {

        Log.i(TAG, "Refreshing GCM Registration Token");

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
