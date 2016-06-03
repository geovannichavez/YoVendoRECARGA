package com.globalpaysolutions.yovendosaldo.model;

/**
 * Created by Josué Chávez on 14/04/2016.
 */
public class Notification
{
    public int mID;
    public String mTitle;
    public String mContent;

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

    public Notification(int pID, String pTitle, String pContent)
    {
        this.setID(pID);
        this.setTitle(pTitle);
        this.setContent(pContent);
    }

    public Notification(){}
}
