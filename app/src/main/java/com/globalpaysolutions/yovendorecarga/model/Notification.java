package com.globalpaysolutions.yovendorecarga.model;

import java.util.Date;

/**
 * Created by Josué Chávez on 14/04/2016.
 */
public class Notification
{
    public int mID;
    public String mTitle;
    public String mContent;
    public Date mDate;
    public int mTrackingID;
    public boolean mSeen;

    //GET
    public int getID()
    {
        return mID;
    }

    public String getContent()
    {
        return mContent;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public Date getDate()
    {
        return this.mDate;
    }

    public int getTrackingID()
    {
        return mTrackingID;
    }

    public boolean getSeen()
    {
        return mSeen;
    }

    //SET
    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public void setContent(String mContent)
    {
        this.mContent = mContent;
    }

    public void setDate(Date pDate)
    {
        this.mDate = pDate;
    }

    public void setTrackingID(int pTrackingID)
    {
        this.mTrackingID = pTrackingID;
    }

    public void setSeen(boolean pSeen)
    {
        this.mSeen = pSeen;
    }

    public Notification(int pID, String pTitle, String pContent, Date pDate, int pTrackingID, boolean pSeen)
    {
        this.setID(pID);
        this.setTitle(pTitle);
        this.setContent(pContent);
        this.setDate(pDate);
        this.setTrackingID(pTrackingID);
        this.setSeen(pSeen);
    }

    public Notification(){}
}
