package com.globalpaysolutions.yovendosaldo.notifications;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.Notificaciones;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josué Chávez on 10/06/2016.
 */
public class YvsNotificationsHandler extends NotificationsHandler
{
    public int NOTIFICATION_ID;
    private NotificationManager mNotificationManager;
    public static int Counter = 0;
    Context ctx;
    public static List<String> notifications = new ArrayList<>();

    final static String GROUP_KEY_NOTIF = "group_key_notif";


    @Override
    public void onReceive(Context context, Bundle bundle)
    {
        //NOTIFICATION_ID = (int)(System.currentTimeMillis() / 1000);
        NOTIFICATION_ID = 237;
        ctx = context;

        Counter = Counter + 1;

        String nhMessage = bundle.getString("message");
        String nhTitle= bundle.getString("title");
        BuildNotification(nhTitle, nhMessage);

    }


    public void BuildNotification(final String notificationTitle, final String notificationMessage )
    {

        /*//Registrando el BroadcastReceiver dinamicamente
        IntentFilter filter = new IntentFilter("notification_cancelled");
        NotificationBroadcastReceiver notifReceiver = new NotificationBroadcastReceiver();
        ctx.getApplicationContext().registerReceiver(notifReceiver, filter);
        ctx.getApplicationContext().unregisterReceiver(notifReceiver);*/


        Intent intent = new Intent(ctx, Notificaciones.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(Notificaciones.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT );
        //PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
        if(Counter > 1)
        {
            notifications.add(notificationMessage);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(String.valueOf(Counter) + " " + ctx.getResources().getString(R.string.n_new_notifications));
            inboxStyle.setSummaryText(ctx.getResources().getString(R.string.yvs));

            for (int i=0; i < notifications.size(); i++)
            {
                inboxStyle.addLine(notifications.get(i));
            }

            mBuilder.setContentTitle(ctx.getResources().getString(R.string.new_notifications))
                    .setStyle(inboxStyle)
                    .setNumber(Counter)
                    .setSmallIcon(getNotificationIcon())
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)
                    .setColor(ctx.getResources().getColor(R.color.AppGreen))
                    .setDeleteIntent(getDeleteIntent())
                    .setGroupSummary(true)
                    .setGroup(GROUP_KEY_NOTIF);
        }
        else
        {
            notifications.add(notificationMessage);

            mBuilder.setSmallIcon(getNotificationIcon())
                    .setContentTitle(notificationTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                    .setColor(ctx.getResources().getColor(R.color.AppGreen))
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)
                    .setDeleteIntent(getDeleteIntent())
                    .setGroup(GROUP_KEY_NOTIF)
                    .setGroupSummary(true)
                    .setContentText(notificationMessage);
        }

        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mBuilder.getNotification().flags |= Notification.FLAG_SHOW_LIGHTS;
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    protected PendingIntent getDeleteIntent()
    {
        Intent intent = new Intent(ctx, NotificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        return PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.drawable.ic_launcher;
    }

}
