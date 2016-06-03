package com.globalpaysolutions.yovendosaldo;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.adapters.ViewPagerHistAdapter;
import com.globalpaysolutions.yovendosaldo.customs.Data;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.SlidingTabLayout;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;
import com.globalpaysolutions.yovendosaldo.model.PaymentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentHistorialVentas extends Fragment
{

    ViewPager pager;
    ViewPagerHistAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Hoy", "Ayer", "Semana"};
    int Numboftabs = 3;

    //Global Variables
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_historial_ventas, container, false);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerHistAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(R.color.ApplicationGreenTheme);
            }


        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);



        sessionManager = new SessionManager(getActivity());


        return v;
    }

    //Hace la request antes que la vista haya sido quitada de la activity
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        CreateArrayUpdate();

    }

    public void CreateArrayUpdate()
    {
        if(!Data.PaymentItems.isEmpty())
        {
            JSONArray jsonPaymentArray = new JSONArray();

            for(PaymentItem item : Data.PaymentItems)
            {
                JSONObject jPaymentObject = new JSONObject();

                try
                {
                    jPaymentObject.put("id", item.getId());
                    jPaymentObject.put("transaction_id", item.getTransactionID());
                    jPaymentObject.put("paid", item.isPaid());
                    jsonPaymentArray.put(jPaymentObject);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            if (CheckConnection())
            {
                JSONObject PaymentItems = new JSONObject();

                try
                {
                    PaymentItems.put("PaymentItems", jsonPaymentArray);
                    System.out.println(PaymentItems);
                }
                catch (JSONException e1)
                {
                    e1.printStackTrace();
                }
                // Depurando objeto Json...
                Log.d("Payment", PaymentItems.toString());

                // Envío de parámetros a servidor y obtención de respuesta
                YVScomSingleton.getInstance(getActivity()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                StringsURL.TOPUPPAYMENT,
                                //StringsURL.TEST_TIMEOUT,
                                PaymentItems,
                                new Response.Listener<JSONObject>()
                                {
                                    @Override
                                    public void onResponse(JSONObject response)
                                    {
                                        Log.d("Mensaje JSON ", response.toString());
                                        Data.PaymentItems.clear();
                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        Log.d("Mensaje JSON ", error.toString());
                                        Data.PaymentItems.clear();
                                    }
                                }
                        )
                        {
                            //Se añade el header para enviar el Token
                            @Override
                            public Map<String, String> getHeaders()
                            {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Token-Autorization", RetrieveSavedToken());
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        }
                        ,1);//Parametro, de maximo de re-intentos
            }
        }

    }

    /*
    *
    *   OTROS METODOS
    *
    */

    private boolean CheckConnection()
    {
        boolean connected;

        if(HaveNetworkConnection() != true)
        {
            connected = false;
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

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
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

    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

}
