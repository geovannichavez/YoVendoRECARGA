package com.globalpaysolutions.yovendosaldo.notifications;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.Home;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Josué Chávez on 10/06/2016.
 */
public class YvsNotificationsHandler extends NotificationsHandler
{
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Bundle bundle)
    {
        ctx = context;
        String nhMessage = bundle.getString("message");
        String nhTitle= bundle.getString("title");
        BuildNotification(nhMessage, nhTitle);

    }

    private void sendNotification(String msg)
    {

        Intent intent = new Intent(ctx, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Notification Hub Demo")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSound(defaultSoundUri)
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void BuildNotification(final String notificationMessage, final String notificationTitle)
    {

        Intent intent = new Intent(ctx, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("YoVendoSALDO")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setColor(ctx.getResources().getColor(R.color.AppGreen))
                .setSound(defaultSoundUri)
                .setContentText(notificationMessage);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


        /*runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(Home.this)
                                .setContentTitle(notificationMessage)
                                .setContentText("Azure Notificaations Hub");

                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(Home.this, Home.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(Home.this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(Home.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(123, mBuilder.build());
            }
        });*/
    }
}
