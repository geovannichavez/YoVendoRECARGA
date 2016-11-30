package com.globalpaysolutions.yovendorecarga.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Josué Chávez on 28/11/2016.
 */
public class TopupInfo
{
    @SerializedName("msisdn")
    private String MSISDN;

    @SerializedName("mno")
    private String MNO;

    @SerializedName("country")
    private String Country;

    public TopupInfo(){}

    public String getMSISDN()
    {
        return MSISDN;
    }

    public String getMNO()
    {
        return MNO;
    }

    public String getCountry()
    {
        return Country;
    }

    public void setMSISDN(String MSISDN)
    {
        this.MSISDN = MSISDN;
    }

    public void setMNO(String MNO)
    {
        this.MNO = MNO;
    }

    public void setCountry(String country)
    {
        Country = country;
    }

    public TopupInfo(String pMSISDN, String pMNO, String pCountry)
    {
        this.setMSISDN(pMSISDN);
        this.setMNO(pMNO);
        this.setCountry(pCountry);
    }
}


