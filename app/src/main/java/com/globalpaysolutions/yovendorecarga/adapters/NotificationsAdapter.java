package com.globalpaysolutions.yovendorecarga.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.globalpaysolutions.yovendorecarga.model.Notification;

import com.android.yovendosaldo.R;
import com.google.android.gms.vision.Frame;

/**
 * Created by Josué Chávez on 14/04/2016.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification>
{
    Context _context;
    int AdapterResource;

    public NotificationsAdapter(Context pContext, int pResource)
    {
        super(pContext, pResource);

        this._context = pContext;
        this.AdapterResource = pResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        final Notification currentItem = getItem(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
            row = inflater.inflate(AdapterResource, parent, false);
        }

        row.setTag(currentItem);
        final TextView tvTitulo = (TextView) row.findViewById(R.id.tvTituloNotificacion);
        final TextView tvContenido = (TextView) row.findViewById(R.id.tvContenidoNotificacion);
        final FrameLayout frameContent = (FrameLayout) row.findViewById(R.id.frameContent);

        tvTitulo.setText(currentItem.getTitle());
        tvContenido.setText(currentItem.getContent());

        if(currentItem.getSeen())
        {
            frameContent.setBackgroundColor(_context.getResources().getColor(R.color.SuperUltraLightGray2));
        }
        else
        {
            frameContent.setBackgroundColor(_context.getResources().getColor(R.color.UltraLightGray));
        }


       /* public void btnRemoveClick(View v)
        {
            final int position = listviewItem.getPositionForView((View) v.getParent());
            listItem.remove(position);
            ItemAdapter.notifyDataSetChanged();

        }*/

        return row;
    }

}
