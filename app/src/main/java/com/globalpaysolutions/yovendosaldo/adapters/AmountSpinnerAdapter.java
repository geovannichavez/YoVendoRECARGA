package com.globalpaysolutions.yovendosaldo.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.model.Amount;

import java.util.List;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class AmountSpinnerAdapter extends ArrayAdapter<Amount>
{
    Context adapContext;
    int adapResource;
    int adapTextViewReosurceId;

    public AmountSpinnerAdapter(Context context, int resource, int textViewResourceId, List<Amount> amountList)
    {
        super(context, resource, textViewResourceId, amountList);
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
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        final Amount currentItem = getItem(position);

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) adapContext).getLayoutInflater();
            row = inflater.inflate(adapResource, parent, false);
        }

        String StringAmount = currentItem.getDisplay();
        String Decimals = currentItem.getDecimal();
        String Texts = currentItem.getAditionalText();

        row.setTag(currentItem);
        row.setTag(position);
        final TextView label = (TextView) row.findViewById(R.id.tvAmount);
        //label.setText(Texts + " $ " + String.valueOf(currentItem.getAmount()) + "." + Decimals);
        label.setText(Texts + " " + StringAmount + "." + Decimals);

        return row;
    }
}
