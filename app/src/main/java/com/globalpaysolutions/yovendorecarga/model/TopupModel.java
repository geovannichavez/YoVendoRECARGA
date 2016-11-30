package com.globalpaysolutions.yovendorecarga.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Josué Chávez on 28/11/2016.
 */
public class TopupModel
{
    @SerializedName("Operador")
    private String Operator;

    @SerializedName("IdCountry")
    private String CountryID;

    TopupModel(){}

    public String getOperator()
    {
        return Operator;
    }

    public String getCountryID()
    {
        return CountryID;
    }

    public void setOperator(String operator)
    {
        Operator = operator;
    }

    public void setCountryID(String countryID)
    {
        CountryID = countryID;
    }

    public TopupModel(String pOperator, String pCountryId)
    {
        this.setOperator(pOperator);
        this.setCountryID(pCountryId);
    }
}
