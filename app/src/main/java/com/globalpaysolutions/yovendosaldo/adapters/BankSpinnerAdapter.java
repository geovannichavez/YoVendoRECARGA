package com.globalpaysolutions.yovendosaldo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.model.Bank;

import java.util.List;

/**
 * Created by Josué Chávez on 05/05/2016.
 */
public class BankSpinnerAdapter extends ArrayAdapter<Bank>
{
    Context adapContext;
    int adapResource;
    int adapTextViewReosurceId;

    public BankSpinnerAdapter(Context context, int resource, int textViewResourceId, List<Bank> banksList)
    {
        super(context, resource, textViewResourceId, banksList);
        adapContext = context;
        adapResource = resource;
        adapTextViewReosurceId = textViewResourceId;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount()
    {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        final Bank currentItem = getItem(position);

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) adapContext).getLayoutInflater();
            row = inflater.inflate(adapResource, parent, false);
        }

        int ID = currentItem.getID();
        String Name = currentItem.getName();
        String Description = currentItem.getDescription();

        row.setTag(currentItem);
        row.setTag(position);
        final TextView label = (TextView) row.findViewById(R.id.tvBankName);
        label.setText(Name);

        return row;
    }
}
