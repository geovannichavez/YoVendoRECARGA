package com.globalpaysolutions.yovendorecarga.model;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class Operator
{
    private int mID;
    private String mOperatorName;
    private String mDescription;
    private String mLogo;
    private int mState;

    public int getID()
    {
        return mID;
    }

    public String getOperatorName()
    {
        return mOperatorName;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public String getLogo()
    {
        return mLogo;
    }

    public int getState()
    {
        return mState;
    }

    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setOperatorName(String mOperatorName)
    {
        this.mOperatorName = mOperatorName;
    }

    public void setDescription(String mDescription)
    {
        this.mDescription = mDescription;
    }

    public void setLogo(String mLogo)
    {
        this.mLogo = mLogo;
    }

    public void setState(int mState)
    {
        this.mState = mState;
    }

    public Operator(int pID, String pOperatorName, String pDescription, String pLogo, int pState)
    {
        this.setID(pID);
        this.setOperatorName(pOperatorName);
        this.setDescription(pDescription);
        this.setLogo(pLogo);
        this.setState(pState);
    }

    public Operator(){}
}
