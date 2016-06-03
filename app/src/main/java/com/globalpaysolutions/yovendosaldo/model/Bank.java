package com.globalpaysolutions.yovendosaldo.model;

/**
 * Created by Josué Chávez on 05/05/2016.
 */
public class Bank
{
    private int mID;
    private String mName;
    private String mDescription;

    public int getID()
    {
        return mID;
    }

    public String getName()
    {
        return mName;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setName(String mName)
    {
        this.mName = mName;
    }

    public void setDescription(String mDescription)
    {
        this.mDescription = mDescription;
    }

    public Bank(){}

    public Bank(int pID, String pName, String pDescription)
    {
        this.setID(pID);
        this.setName(pName);
        this.setDescription(pDescription);
    }
}
