package com.globalpaysolutions.yovendosaldo.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.globalpaysolutions.yovendosaldo.customs.SessionManager;

/**
 * Created by Josué Chávez on 23/06/2016.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if(action.equals("notification_cancelled"))
        {
            // your code
            YvsNotificationsHandler.Counter = 0;
            YvsNotificationsHandler.notifications.clear();

        }
    }
}
