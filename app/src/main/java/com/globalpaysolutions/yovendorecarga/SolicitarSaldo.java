package com.globalpaysolutions.yovendorecarga;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.Data;

import org.json.JSONException;
import org.json.JSONObject;

public class SolicitarSaldo extends AppCompatActivity
{
    Toolbar toolbar;
    ProgressDialog ProgressDialog;
    CustomFullScreenDialog CustomDialogCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_saldo);

        toolbar = (Toolbar) findViewById(R.id.toolbarSaldo);
        toolbar.setTitle(getString(R.string.title_activity_request_airtime));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CustomDialogCreator = new CustomFullScreenDialog(this, this);


    }

    private void requestAirtime()
    {
        if (CheckConnection())
        {
            ProgressDialog = new ProgressDialog(SolicitarSaldo.this);
            ProgressDialog.setMessage("Espere...");
            ProgressDialog.show();
            ProgressDialog.setCancelable(false);
            ProgressDialog.setCanceledOnTouchOutside(false);

            Data.AirtimeRequest(this, new Data.VolleyCallback()
            {
                @Override
                public void onResult(boolean result, JSONObject response)
                {
                    String masterName = "";
                    ProgressDialog.dismiss();

                    if (result)
                    {
                        try
                        {
                            masterName = response.has("MasterName") ? response.getString("MasterName") : "";
                        } catch (JSONException ex)
                        {
                            ex.printStackTrace();
                        }

                        String contentMessage = getResources().getString(R.string.request_balance_success_message_master_name) + " " + masterName;
                        CustomDialogCreator.CreateFullScreenDialog(getResources().getString(R.string.request_balance_success), contentMessage, null, null, "Aceptar", "NAVIGATEHOME", false, false);
                    }
                    else
                    {
                        CustomDialogCreator.CreateFullScreenDialog(getResources().getString(R.string.we_are_sorry_msg_title), getResources().getString(R.string.something_went_wrong_try_again), null, null, "Aceptar", "NAVIGATEHOME", true, false);
                    }
                }

            });
        }

    }

    public void AskConfirmationDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.request_airtime_confirmation));
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                requestAirtime();
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    /*
    *
    *
    *   OTROS MÉTODOS
    *
    */

    private boolean CheckConnection()
    {
        boolean connected;

        if(HaveNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = getString(R.string.no_internet_connection);
            Toast.makeText(getApplicationContext(), connectionMessage, Toast.LENGTH_LONG).show();
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    private boolean HaveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
