package com.globalpaysolutions.yovendorecarga.model;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class Operator
{
    private int mID;
    private String mOperatorName;
    private String mDescription;
    private String mBrand;
    private String mLogo;
    private String mMNC;
    private String mLogoURL;
    private int mState;
    private int mLogoVersion;
    private int mCountryID;
    private byte[] mLogoImage;

    //  GET
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

    public String getBrand()
    {
        return mBrand;
    }

    public String getMNC()
    {
        return mMNC;
    }

    public String getLogoURL()
    {
        return mLogoURL;
    }

    public int getLogoVersion()
    {
        return mLogoVersion;
    }

    public int getCountryID()
    {
        return mCountryID;
    }

    public byte[] getLogoImage()
    {
        return mLogoImage;
    }

    //  SET
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

    public void setBrand(String pBrand)
    {
        this.mBrand = pBrand;
    }

    public void setMNC(String pMnc)
    {
        this.mMNC = pMnc;
    }

    public void setLogoURL(String pLogoUrl)
    {
        this.mLogoURL = pLogoUrl;
    }

    public void setLogoVersion(int pLogoVersion)
    {
        this.mLogoVersion = pLogoVersion;
    }

    public void setCountryID(int pCountryID)
    {
        this.mCountryID = pCountryID;
    }

    public void setLogoImage(byte[] pImage)
    {
        this.mLogoImage = pImage;
    }

    public Operator(int pID, String pOperatorName, String pDescription, String pLogo,
                    int pState, String pBrand, String pMnc, String pLogoUrl, int pLogoVersion,
                    int pCoundtrId, byte[] pImage)
    {
        this.setID(pID);
        this.setOperatorName(pOperatorName);
        this.setDescription(pDescription);
        this.setLogo(pLogo);
        this.setState(pState);
        this.setBrand(pBrand);
        this.setMNC(pMnc);
        this.setLogoURL(pLogoUrl);
        this.setLogoVersion(pLogoVersion);
        this.setCountryID(pCoundtrId);
        this.setLogoImage(pImage);
    }

    public Operator(){}
}
