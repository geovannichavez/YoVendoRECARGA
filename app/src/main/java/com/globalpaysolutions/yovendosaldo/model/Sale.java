package com.globalpaysolutions.yovendosaldo.model;

import java.util.Date;

/**
 * Created by Geovanni on 25/03/2016.
 */
public class Sale
{

    private int mID;
    private String mMSISDN;
    private double mAmount;
    private Date mDate;
    private String mStatus;
    private String mOperator;
    private boolean mPaymentStatus;

    public int getID()
    {
        return mID;
    }

    public String getMSISDN()
    {
        return mMSISDN;
    }

    public double getAmount()
    {
        return mAmount;
    }

    public Date getDate()
    {
        return mDate;
    }

    public String getStatus()
    {
        return mStatus;
    }

    public String getOperator()
    {
        return mOperator;
    }

    public boolean getPaymentStatus()
    {
        return  mPaymentStatus;
    }

    public void setID(int pID)
    {
        this.mID = pID;
    }

    public void setMSISDN(String pMSISDN)
    {
        this.mMSISDN = pMSISDN;
    }

    public void setAmount(double pAmount)
    {
        this.mAmount = pAmount;
    }

    public void setDate(Date pDate)
    {
        this.mDate = pDate;
    }

    public void setStatus(String pStatus)
    {
        this.mStatus = pStatus;
    }

    public void setOperator(String mOperator)
    {
        this.mOperator = mOperator;
    }

    public void setPaymentStatus(boolean pPaymentStatus)
    {
        this.mPaymentStatus = pPaymentStatus;
    }

    public Sale(int pID, String pMSISDN, double pAmount, Date pDate, String pStatus, String pOperator)
    {
        this.setID(pID);
        this.setMSISDN(pMSISDN);
        this.setAmount(pAmount);
        this.setDate(pDate);
        this.setStatus(pStatus);
        this.setOperator(pOperator);
    }

    public Sale(){}


    /*private int mID;
    private String mPhoneNumber;
    private String mOperatorName;
    private String mAmount;
    private int mState;

    public int getID()
    {
        return mID;
    }

    public String getPhoneNumber()
    {
        return mPhoneNumber;
    }

    public String getOperatorName()
    {
        return mOperatorName;
    }

    public String getAmount()
    {
        return mAmount;
    }

    public int getState()
    {
        return mState;
    }

    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setPhoneNumber(String mPhoneNumber)
    {
        this.mPhoneNumber = mPhoneNumber;
    }

    public void setOperatorName(String mOperatorName)
    {
        this.mOperatorName = mOperatorName;
    }

    public void setAmount(String mAmount)
    {
        this.mAmount = mAmount;
    }

    public void setState(int mState)
    {
        this.mState = mState;
    }

    public Sale(int pID, String pPhoneNumber, String pOperatorName, String pAmount, int pState)
    {
        this.setID(pID);
        this.setPhoneNumber(pPhoneNumber);
        this.setOperatorName(pOperatorName);
        this.setAmount(pAmount);
        this.setState(pState);
    }

    public Sale()
    {

    }*/
}
