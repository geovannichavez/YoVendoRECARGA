package com.globalpaysolutions.yovendorecarga.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.globalpaysolutions.yovendorecarga.FragmentHistorialAyer;
import com.globalpaysolutions.yovendorecarga.FragmentHistorialHoy;
import com.globalpaysolutions.yovendorecarga.FragmentHistorialSemana;

/**
 * Created by Geovanni on 24/03/2016.
 */
public class ViewPagerHistAdapter extends FragmentStatePagerAdapter
{
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerHistAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb)
    {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position)
    {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            FragmentHistorialHoy tab1 = new FragmentHistorialHoy();
            return tab1;
        }
        if(position == 1)            // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            FragmentHistorialAyer tab2 = new FragmentHistorialAyer();
            return tab2;
        }
        else         // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            FragmentHistorialSemana tab3 = new FragmentHistorialSemana();
            return tab3;
        }
        /*else          // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            FragmentHistorialTodo tab4 = new FragmentHistorialTodo();
            return tab4;
        }*/


    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position)
    {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount()
    {
        return NumbOfTabs;
    }
}
