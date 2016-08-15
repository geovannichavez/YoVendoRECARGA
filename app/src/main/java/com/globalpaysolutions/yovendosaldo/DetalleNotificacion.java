package com.globalpaysolutions.yovendosaldo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.yovendosaldo.R;

import org.w3c.dom.Text;

public class DetalleNotificacion extends AppCompatActivity
{
    //Views and Layouts
    Toolbar toolbar;
    TextView tvTitle;
    TextView tvContent;

    //Global variables
    String mTitle;
    String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_notificacion);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra("notifTitle");
        mContent = intent.getStringExtra("notifMessage");

        tvTitle = (TextView) findViewById(R.id.notifTitle);
        tvContent = (TextView) findViewById(R.id.notifBody);

        tvTitle.setText(mTitle);
        tvContent.setText(mContent);


    }
}
