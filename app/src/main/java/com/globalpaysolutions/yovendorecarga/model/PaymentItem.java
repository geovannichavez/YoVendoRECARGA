package com.globalpaysolutions.yovendorecarga.model;

/**
 * Created by Josué Chávez on 30/05/2016.
 */
public class PaymentItem
{
    private String mId;
    private String mTransactionId;
    private boolean mPaid;

    public String getId()
    {
        return mId;
    }

    public String getTransactionID()
    {
        return mTransactionId;
    }

    public boolean isPaid()
    {
        return mPaid;
    }

    public void setId(String id)
    {
        this.mId = id;
    }

    public void setTransactionId(String transaction_id)
    {
        this.mTransactionId = transaction_id;
    }

    public void setPaid(boolean paid)
    {
        this.mPaid = paid;
    }
}
