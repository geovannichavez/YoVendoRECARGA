package com.globalpaysolutions.yovendorecarga.engagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.util.Log;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.DatabaseHandler;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.model.LocalNotification;
import com.microsoft.azure.engagement.reach.CampaignId;
import com.microsoft.azure.engagement.reach.EngagementDefaultNotifier;
import com.microsoft.azure.engagement.reach.EngagementReachInteractiveContent;
import com.microsoft.azure.engagement.reach.v11.EngagementNotificationUtilsV11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josué Chávez on 07/08/2016.
 */
public class AzmeNotifier extends EngagementDefaultNotifier
{
    SessionManager sessionManager;
    NotificationManager mNotificationManager;
    DatabaseHandler db;

    final static String GROUP_KEY_NOTIF = "group_key_notif";
    public static int Counter = 0;
    public static List<String> notifications = new ArrayList<>();


    public int NOTIFICATION_ID;
    String NotificationTitle;
    boolean Announcement = false;


    public AzmeNotifier(Context context)
    {
        super(context);
        db = new DatabaseHandler(context);
    }

    @Override
    public Boolean handleNotification(EngagementReachInteractiveContent content) throws RuntimeException
    {
        CampaignId azmeCampaign =  content.getCampaignId();
        CampaignId.Kind campaignkind = azmeCampaign.getKind();

        if(campaignkind.getShortName().equals("a")) //"a" ANNOUNCEMENT
        {
            //SALVAR EL TITULO DE NOTIFICACION
            Announcement = true;
            NotificationTitle = content.getNotificationTitle();
        }

        return super.handleNotification(content);
    }

    protected boolean onNotificationPrepared(Notification notification, EngagementReachInteractiveContent content) throws RuntimeException
    {
        sessionManager = new SessionManager(mContext);
        NOTIFICATION_ID = 237;

        if (content.isSystemNotification() == true)
        {

            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

            //region MANEJO DE AGRUPAMIENTO DE NOTIFICACIONES
            if (Counter > 1)
            {

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(String.valueOf(Counter) + " " + mContext.getResources().getString(R.string.n_new_notifications));
                inboxStyle.setSummaryText(getUserEmail());


                for (int i=0; i < notifications.size(); i++)
                {
                    inboxStyle.addLine(notifications.get(i));
                }

                mBuilder.setContentTitle(mContext.getResources().getString(R.string.new_notifications))
                        .setStyle(inboxStyle)
                        .setNumber(Counter)
                        .setSmallIcon(getNotificationIcon())
                        .setSound(defaultSoundUri)
                        .setLights(Color.argb(102, 204, 0, 1), 3000, 3000)
                        .setAutoCancel(true)
                        .setContentText(mContext.getResources().getString(R.string.click_to_open_notifications))
                        .setColor(mContext.getResources().getColor(R.color.AppGreen))
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY_NOTIF);
            }
            else
            {
                mBuilder.setSmallIcon(getNotificationIcon())
                        .setContentTitle(content.getNotificationTitle())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content.getNotificationMessage()))
                        .setTicker(notification.tickerText)
                        .setColor(mContext.getResources().getColor(R.color.AppGreen))
                        .setSound(defaultSoundUri)
                        .setLights(Color.argb(102, 204, 0, 1), 3000, 3000)
                        .setAutoCancel(true)
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY_NOTIF)
                        .setContentText(content.getNotificationMessage());
            }
            //endregion


            // The notification settings
            mBuilder.setDefaults(notification.defaults);
            if (content.isNotificationCloseable() == false)
            {
                mBuilder.setAutoCancel(false);
            }
            else
            {
                mBuilder.setAutoCancel(true);
            }
            mBuilder.setContent(notification.contentView);
            mBuilder.setContentIntent(notification.contentIntent);
            mBuilder.setDeleteIntent(notification.deleteIntent); //PendingIntent para borrar notificaciones
            mBuilder.setWhen(notification.when);

            if (content.getNotificationBigText() != null)
            {
                final BigTextStyle bigTextStyle = new BigTextStyle();
                bigTextStyle.setBigContentTitle(content.getNotificationTitle());
                bigTextStyle.setSummaryText(content.getNotificationMessage());
                bigTextStyle.bigText(content.getNotificationBigText());
                mBuilder.setStyle(bigTextStyle);
            }
            else if (content.getNotificationBigPicture() != null)
            {
                final BigPictureStyle bigPictureStyle = new BigPictureStyle();
                final Bitmap bitmap = EngagementNotificationUtilsV11.getBigPicture(this.mContext,
                        content.getDownloadId().longValue());
                bigPictureStyle.bigPicture(bitmap);
                bigPictureStyle.setSummaryText(content.getNotificationMessage());
                mBuilder.setStyle(bigPictureStyle);
            }


            /* Dismiss option can be managed only after build */
            final Notification finalNotification = mBuilder.build();
            if (content.isNotificationCloseable() == false)
            {
                finalNotification.flags |= Notification.FLAG_NO_CLEAR;
            }
            else
            {
                mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            }

            /* Notify here instead of super class */
            final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            Integer NotificationID = getNotificationId(content);

            //si la notificacion ya existe, entonces no la vuelve a disparar
            LocalNotification existingNotif = db.getNotificationByAzmeID(NotificationID);
            Integer azmeID = existingNotif.getAzmeNotificationID();
            if(azmeID.equals(0))
            {
                manager.notify(NotificationID, finalNotification); // notice the call to get the right identifier

                if(Announcement)
                {
                    SaveNotification(NotificationID, NotificationTitle, content.getNotificationMessage());
                }
            }

            /* Return false, we notify ourselves */
            return false;
        }
        else
        {
            //return super.onNotificationPrepared(notification, content);
            return false;
        }
    }


    private int getNotificationIcon()
    {
        /*boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.drawable.ic_notification;*/
        int _icon = R.drawable.ic_notification;
        return _icon;
    }

    public String getUserEmail()
    {
        String Email;
        HashMap<String, String> MapEmail = sessionManager.GetUserEmail();
        Email = MapEmail.get(SessionManager.KEY_USER_EMAIL);

        return Email;
    }

    public void SaveNotification(int pAzmeID, String pTitle, String pMessage)
    {
        LocalNotification notification = new LocalNotification();
        notification.setAzmeNotificationID(pAzmeID);
        notification.setNotificationTitle(pTitle);
        notification.setNotificationMessage(pMessage);

        db.addNotification(notification);
    }
}
