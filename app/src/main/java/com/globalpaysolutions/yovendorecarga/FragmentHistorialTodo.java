package com.globalpaysolutions.yovendorecarga;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.yovendosaldo.R;


public class FragmentHistorialTodo extends Fragment
{
    TextView tvYVS;

    public FragmentHistorialTodo()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_historial_todo, container, false);

        tvYVS = (TextView) view.findViewById(R.id.tvYVSurl);
        tvYVS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.yovendosaldo.com"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

}
