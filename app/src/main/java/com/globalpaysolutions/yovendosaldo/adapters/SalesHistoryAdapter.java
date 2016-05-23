package com.globalpaysolutions.yovendosaldo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.model.Sale;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Geovanni on 25/03/2016.
 */
public class SalesHistoryAdapter extends ArrayAdapter<Sale>
{
    Context AdapterContext;
    int AdapResource;

    public SalesHistoryAdapter(Context pContext, int pResource)
    {
        super(pContext, pResource);

        AdapterContext = pContext;
        AdapResource = pResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        final Sale currentItem = getItem(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity) AdapterContext).getLayoutInflater();
            row = inflater.inflate(AdapResource, parent, false);
        }

        row.setTag(currentItem);
        final TextView tvPhoneNumber = (TextView) row.findViewById(R.id.tvSalePhoneNumber);
        final Switch swPago = (Switch) row.findViewById(R.id.swPago);
        final TextView tvSaleDate = (TextView) row.findViewById(R.id.tvSaleDate);
        final TextView tvAmount = (TextView) row.findViewById(R.id.tvSaleAmount);
        final ImageView ivStatus = (ImageView) row.findViewById(R.id.ivStatus);
        final TextView tvStatus = (TextView) row.findViewById(R.id.tvStatusRecarga);


        swPago.setChecked(true);
        swPago.setTextOff("NO");
        swPago.setTextOn("SI");

        String PhoneNumber = currentItem.getMSISDN().replace("503", "");
        if(!"".equals(PhoneNumber))
        {
            PhoneNumber = PhoneNumber.substring(0,4) + "-" + PhoneNumber.substring(4,PhoneNumber.length());
        }

        String Amount = String.valueOf(currentItem.getAmount());
        if(Amount.substring(Amount.lastIndexOf('.') + 1).equals("0"))
        {
            Amount = Amount + "0";
        }

        String Status = currentItem.getStatus();
        if(Status != null)
        {
            if(!"".equals(Status) || Status.isEmpty())
            {
                if(Status.equals("Fallida") )
                {
                    ivStatus.setImageResource(R.drawable.icono_failure);
                    tvStatus.setText(AdapterContext.getResources().getString(R.string.status_recarga_display_fail));
                }
                else
                {
                    ivStatus.setImageResource(R.drawable.icono_check_verde);
                    tvStatus.setText(AdapterContext.getResources().getString(R.string.status_recarga_display_success));
                }
            }
        }

        tvSaleDate.setText(new SimpleDateFormat("MMM d' - 'h:mm a", new Locale("es_ES")).format(currentItem.getDate()));

        tvPhoneNumber.setText(PhoneNumber);
        String Operator = currentItem.getOperator().equals("null") ? "" : currentItem.getOperator().toUpperCase() + " - ";
        tvAmount.setText(Operator + "$" + Amount);
        return row;
    }
}
