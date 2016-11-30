package com.globalpaysolutions.yovendorecarga.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Josué Chávez on 28/11/2016.
 */
public class TopupResult
{
    @SerializedName("id")
    private String Id;

    @SerializedName("code")
    private String Code;

    @SerializedName("message")
    private String Message;

    @SerializedName("info")
    private TopupInfo Info;

    @SerializedName("AvailableAmount")
    private String AvailableAmount;

    public TopupResult() {}

    public String getId()
    {
        return Id;
    }

    public String getCode()
    {
        return Code;
    }

    public String getMessage()
    {
        return Message;
    }

    public TopupInfo getInfo()
    {
        return Info;
    }

    public String getAvailableAmount()
    {
        return AvailableAmount;
    }

    public void setId(String id)
    {
        Id = id;
    }

    public void setCode(String code)
    {
        Code = code;
    }

    public void setMessage(String message)
    {
        Message = message;
    }

    public void setInfo(TopupInfo info)
    {
        Info = info;
    }

    public void setAvailableAmount(String availableAmount)
    {
        AvailableAmount = availableAmount;
    }

    public TopupResult(String pId, String pCode, String pMessage, TopupInfo pInfo, String pAmount)
    {

        this.setId(pId);
        this.setCode(pCode);
        this.setMessage(pMessage);
        this.setInfo(pInfo);
        this.setAvailableAmount(pAmount);
    }
}

