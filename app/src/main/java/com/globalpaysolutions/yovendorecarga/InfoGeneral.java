package com.globalpaysolutions.yovendorecarga;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.yovendosaldo.R;


public class InfoGeneral extends AppCompatActivity
{
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_general);

        toolbar = (Toolbar) findViewById(R.id.toolbarInfo);
        toolbar.setTitle(getString(R.string.title_activity_infor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
