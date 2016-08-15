package com.globalpaysolutions.yovendosaldo.model;

/**
 * Created by Josué Chávez on 14/08/2016.
 */
public class LocationData
{
    private double Latitude;
    private double Longitude;

    public double getLatitude()
    {
        return Latitude;
    }

    public double getLongitude()
    {
        return Longitude;
    }

    public void setLatitude(double latitude)
    {
        Latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        Longitude = longitude;
    }

    public LocationData(double pLatitude, double pLongitude)
    {
        this.Latitude = pLatitude;
        this.Longitude = pLongitude;
    }


    public LocationData()
    {
    }

}
