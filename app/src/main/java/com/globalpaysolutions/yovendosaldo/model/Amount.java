package com.globalpaysolutions.yovendosaldo.model;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class Amount
{
    private int mID;
    private int mAmount;
    private String mDisplay;
    private String mDecimal;
    private String mAditionalText;

    public int getID()
    {
        return mID;
    }

    public int getAmount()
    {
        return mAmount;
    }

    public String getDecimal()
    {
        return mDecimal;
    }

    public String getAditionalText()
    {
        return mAditionalText;
    }

    public String getDisplay()
    {
        return mDisplay;
    }

    public void setAmount(int mAmount)
    {
        this.mAmount = mAmount;
    }

    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setDecimal(String pDecimal)
    {
        this.mDecimal = pDecimal;
    }

    public void setAditionalText(String pText)
    {
        this.mAditionalText = pText;
    }

    public void setDisplay(String pDisplay)
    {
        this.mDisplay = pDisplay;
    }

    public Amount(int pID, int pAmount, String pDecimal, String pText, String pDisplay)
    {
        this.setID(pID);
        this.setAmount(pAmount);
        this.setDecimal(pDecimal);
        this.setAditionalText(pText);
        this.setDisplay(pDisplay);
    }

    public Amount()
        {}
}
