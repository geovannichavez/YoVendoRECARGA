package com.globalpaysolutions.yovendosaldo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yovendosaldo.R;

public class Configuracion extends AppCompatActivity
{
    Toolbar toolbar;
    String Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        //Fragment = getIntent().getStringExtra("ConfFragment");

        toolbar = (Toolbar) findViewById(R.id.toolbarConf);
        toolbar.setTitle(getString(R.string.title_activity_config));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetConfigurationFragment();

        /*switch (Fragment)
        {
            case "PIN":
                MenuManejoPINFragment manejoPin = new MenuManejoPINFragment();
                fragmentTransaction.add(R.id.frameConfig, manejoPin);
                fragmentTransaction.commit();
                break;
            case "CONF":
                ConfiguracionFragment fragment = new ConfiguracionFragment();
                fragmentTransaction.add(R.id.frameConfig, fragment);
                fragmentTransaction.commit();
                break;
            default:
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG).show();
                break;
        }*/

    }

    public static class ConfiguracionFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_configuracion);
        }
    }

    /*@Override
    protected void onResume()
    {
        super.onResume();
        SetConfigurationFragment();
    }*/

    public void SetConfigurationFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ConfiguracionFragment fragment = new ConfiguracionFragment();
        fragmentTransaction.add(R.id.frameConfig, fragment);
        fragmentTransaction.commit();
    }

    /*@Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        finish();
        Intent i = new Intent(this, Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }*/
}
