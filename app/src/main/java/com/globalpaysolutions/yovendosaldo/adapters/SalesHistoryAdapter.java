package com.globalpaysolutions.yovendosaldo.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.customs.Data;
import com.globalpaysolutions.yovendosaldo.model.PaymentItem;
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

    // our ViewHolder.
    // caches our all layouts
    static class ViewHolderItem
    {
        ImageView ivStatus;
        TextView tvStatusRecarga;
        TextView tvSalePhoneNumber;
        TextView tvSaleAmount;
        TextView tvSaleDate;
        SwitchCompat swPago;
    }

    public SalesHistoryAdapter(Context pContext, int pResource)
    {
        super(pContext, pResource);

        AdapterContext = pContext;
        AdapResource = pResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolderItem viewHolder;
        //viewHolder = new ViewHolderItem();

        final Sale currentItem = getItem(position);

        //Crea el objeto del tipo PaymentItem con los datos del
        //currentItem para añadirlo a lista
        final PaymentItem paymentCurrentItem = new PaymentItem();
        paymentCurrentItem.setId(currentItem.getID());
        paymentCurrentItem.setTransactionId(currentItem.getTransactionID());

        View row = convertView;

        if(row == null)
        {

            LayoutInflater inflater = ((Activity) AdapterContext).getLayoutInflater();
            row = inflater.inflate(AdapResource, parent, false);

            viewHolder = new ViewHolderItem();
            //row.setTag(currentItem);

            viewHolder.tvSalePhoneNumber = (TextView) row.findViewById(R.id.tvSalePhoneNumber);
            viewHolder.tvSaleDate = (TextView) row.findViewById(R.id.tvSaleDate);
            viewHolder.tvSaleAmount = (TextView) row.findViewById(R.id.tvSaleAmount);
            viewHolder.ivStatus = (ImageView) row.findViewById(R.id.ivStatus);
            viewHolder.tvStatusRecarga = (TextView) row.findViewById(R.id.tvStatusRecarga);
            viewHolder.swPago = (SwitchCompat) row.findViewById(R.id.swPago);


            row.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolderItem) row.getTag();
        }


        final ViewHolderItem finalViewHolder = viewHolder;





        /*final TextView tvPhoneNumber = (TextView) row.findViewById(R.id.tvSalePhoneNumber);
        final SwitchCompat swPago = (SwitchCompat) row.findViewById(R.id.swPago);
        final TextView tvSaleDate = (TextView) row.findViewById(R.id.tvSaleDate);
        final TextView tvAmount = (TextView) row.findViewById(R.id.tvSaleAmount);
        final ImageView ivStatus = (ImageView) row.findViewById(R.id.ivStatus);
        final TextView tvStatus = (TextView) row.findViewById(R.id.tvStatusRecarga);*/



        //Switch de Pago
        //final boolean isChecked = viewHolder.swPago.isChecked();
        viewHolder.swPago.setTextOff("NO");
        viewHolder.swPago.setTextOn("SI");
        viewHolder.swPago.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SwitchClick(paymentCurrentItem, finalViewHolder, currentItem);
            }
        });
        viewHolder.swPago.setChecked(currentItem.getPaymentStatus());

        //Número de Teléfono
        String PhoneNumber = currentItem.getMSISDN().replace("503", "");
        if(!"".equals(PhoneNumber))
        {
            PhoneNumber = PhoneNumber.substring(0,4) + "-" + PhoneNumber.substring(4,PhoneNumber.length());
        }

        //Monto
        String Amount = String.valueOf(currentItem.getAmount());
        if(Amount.substring(Amount.lastIndexOf('.') + 1).equals("0"))
        {
            Amount = Amount + "0";
        }

        //Estado de recarga
        String Status = currentItem.getStatus();
        if(Status != null)
        {
            if(!"".equals(Status) || Status.isEmpty())
            {
                if(Status.equals("Fallida") )
                {
                    viewHolder.ivStatus.setImageResource(R.drawable.icono_failure);
                    viewHolder.tvStatusRecarga.setText(AdapterContext.getResources().getString(R.string.status_recarga_display_fail));
                }
                else
                {
                    viewHolder.ivStatus.setImageResource(R.drawable.icono_check_verde);
                    viewHolder.tvStatusRecarga.setText(AdapterContext.getResources().getString(R.string.status_recarga_display_success));
                }
            }
        }

        //Fecha
        viewHolder.tvSaleDate.setText(new SimpleDateFormat("MMM d' - 'h:mm a", new Locale("es_ES")).format(currentItem.getDate()).toUpperCase());

        viewHolder.tvSalePhoneNumber.setText(PhoneNumber);
        String Operator = currentItem.getOperator().equals("null") ? "" : currentItem.getOperator().toUpperCase() + " - ";
        viewHolder.tvSaleAmount.setText(Operator + "$" + Amount);
        return row;
    }

    public  void SwitchClick(PaymentItem paymentCurrentItem, ViewHolderItem viewHolder, Sale currentItem )
    {
        if(!viewHolder.swPago.isChecked())
        {
            paymentCurrentItem.setPaid(false);
            currentItem.setPaymentStatus(false);
        }
        else
        {
            paymentCurrentItem.setPaid(true);
            currentItem.setPaymentStatus(true);
        }

        Data.ManagePaymentItems(paymentCurrentItem);
    }
}
