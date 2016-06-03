package com.globalpaysolutions.yovendosaldo.customs;

import java.util.List;

/**
 * Created by Josué Chávez on 15/04/2016.
 */
public class DrawerMenuItem
{
    public int mIconID;
    public String mName;
    public List<String> mSubitems;

    public void setIconID(int mIconID)
    {
        this.mIconID = mIconID;
    }

    public void setName(String mName)
    {
        this.mName = mName;
    }

    public void setSubitems(List<String> pItems)
    {
        mSubitems.addAll(pItems);
    }

    public int getIconID()
    {
        return mIconID;
    }

    public String getName()
    {
        return mName;
    }

    public List<String> getSubitems()
    {
        return mSubitems;
    }
}
