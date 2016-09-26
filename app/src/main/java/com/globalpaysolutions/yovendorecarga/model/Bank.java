package com.globalpaysolutions.yovendorecarga.model;

/**
 * Created by Josu� Ch�vez on 05/05/2016.
 */
public class Bank
{
    private int mID;
    private String mName;
    private String mDescription;
    private int mMinLength;
    private int mMaxLength;

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

    public int getMinLength()
    {
        return mMinLength;
    }

    public int getMaxLength()
    {
        return mMaxLength;
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

    public void setMinLength(int pMinLength)
    {
        this.mMinLength = pMinLength;
    }

    public void setMaxLength(int pMaxLength)
    {
        this.mMaxLength = pMaxLength;
    }

    public Bank(){}

    public Bank(int pID, String pName, String pDescription, int pMinLength, int pMaxLength)
    {
        this.setID(pID);
        this.setName(pName);
        this.setDescription(pDescription);
        this.setMinLength(pMinLength);
        this.setMaxLength(pMaxLength);
    }
}
