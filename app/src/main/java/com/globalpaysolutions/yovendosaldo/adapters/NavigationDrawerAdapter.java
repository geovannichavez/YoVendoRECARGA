package com.globalpaysolutions.yovendosaldo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.customs.DrawerMenuItem;

/**
 * Created by Josué Chávez on 15/04/2016.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<DrawerMenuItem>
{
    Context AdapterContext;
    int AdapResource;

    public NavigationDrawerAdapter(Context pContext, int pResource)
    {
        super(pContext, pResource);

        AdapterContext = pContext;
        AdapResource = pResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        final DrawerMenuItem currentItem = getItem(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity) AdapterContext).getLayoutInflater();
            row = inflater.inflate(AdapResource, parent, false);
        }

        row.setTag(currentItem);
        final ImageView ItemIcon = (ImageView) row.findViewById(R.id.ivDrawerMenuIcono);
        final TextView Title = (TextView) row.findViewById(R.id.tvDrawerTitulo);

        ItemIcon.setImageResource(currentItem.getIconID());
        Title.setText(currentItem.getName());

        return row;
    }
}
