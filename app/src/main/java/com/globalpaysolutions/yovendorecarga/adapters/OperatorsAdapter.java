package com.globalpaysolutions.yovendorecarga.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.globalpaysolutions.yovendorecarga.model.Operator;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class OperatorsAdapter extends ArrayAdapter<Operator>
{
    Context AdapterContext;
    int AdapResource;
    int SelectedItem;

    public OperatorsAdapter(Context pContext, int pResource)
    {
        super(pContext, pResource);

        AdapterContext = pContext;
        AdapResource = pResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        final Operator currentItem = getItem(position);

        if (view == null)
        {
            LayoutInflater inflater = ((Activity) AdapterContext).getLayoutInflater();
            view = inflater.inflate(AdapResource, parent, false);
        }

        view.setTag(currentItem);

        //Seteo del logo
        NetworkImageView networkViewOperador = (NetworkImageView) view.findViewById(R.id.networkViewOperador);
        ImageView logoOperator = (ImageView) view.findViewById(R.id.ivOperador);

        if(currentItem.getLogoImage() != null)
        {
            networkViewOperador.setVisibility(View.GONE);
            //logoOperator.setVisibility(View.VISIBLE);
            byte[] image = currentItem.getLogoImage();
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            logoOperator.setImageBitmap(bmp);
        }
        else
        {
            logoOperator.setVisibility(View.GONE);
            networkViewOperador.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = YVScomSingleton.getInstance(getContext()).getImageLoader();
            networkViewOperador.setImageUrl(currentItem.getLogoURL(), imageLoader);
        }





        //GridView Item (image)
       /* ImageView OperatorLogo = (ImageView) view.findViewById(R.id.ivOperador);
        String Logo = currentItem.getOperatorName();
        switch (Logo)
        {
            case "Tigo":
                OperatorLogo.setImageResource((R.drawable.tigo_logo));
                break;
            case "Digicel":
                OperatorLogo.setImageResource((R.drawable.digicel_logo));
                break;
            case "Claro":
                OperatorLogo.setImageResource((R.drawable.claro_logo));
                break;
            case "Movistar":
                OperatorLogo.setImageResource((R.drawable.movistar_logo));
                break;
            default: OperatorLogo.setImageResource((R.drawable.ic_launcher));
                break;
        }*/


        return view;
    }


}
