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

    public Notification(int pID, String pTitle, String pContent, Date pDate)
    {
        this.setID(pID);
        this.setTitle(pTitle);
        this.setContent(pContent);
        this.setDate(pDate);
    }

    public Notification(){}
}
