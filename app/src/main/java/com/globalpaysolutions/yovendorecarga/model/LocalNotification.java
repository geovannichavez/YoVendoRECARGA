package com.globalpaysolutions.yovendorecarga.model;

/**
 * Created by Josué Chávez on 12/10/2016.
 */
public class LocalNotification
{
    public int mNotificationID;
    public int mAzmeNotificationID;
    public String mNotificationTitle;
    public String mNotificationMessage;


    //GET
    public int getNotificationID()
    {
        return mNotificationID;
    }

    public int getAzmeNotificationID()
    {
        return mAzmeNotificationID;
    }

    public String getNotificationTitle()
    {
        return mNotificationTitle;
    }

    public String getNotificationMessage()
    {
        return mNotificationMessage;
    }



    //SET
    public void setNotificationID(int pID)
    {
        this.mNotificationID = pID;
    }

    public void setAzmeNotificationID(int pAzmeID)
    {
        this.mAzmeNotificationID = pAzmeID;
    }

    public void setNotificationTitle(String pTitle)
    {
        this.mNotificationTitle = pTitle;
    }

    public void setNotificationMessage(String pMessage)
    {
        this.mNotificationMessage = pMessage;
    }

    public LocalNotification(){}

    public LocalNotification(int pID, int pAzmeID, String pTitle, String pMessage)
    {
        this.setNotificationID(pID);
        this.setAzmeNotificationID(pAzmeID);
        this.setNotificationTitle(pTitle);
        this.setNotificationMessage(pMessage);
    }
}
