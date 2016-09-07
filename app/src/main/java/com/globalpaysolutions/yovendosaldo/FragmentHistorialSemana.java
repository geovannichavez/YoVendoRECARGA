package com.globalpaysolutions.yovendosaldo;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.adapters.SalesHistoryAdapter;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;
import com.globalpaysolutions.yovendosaldo.model.Sale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class FragmentHistorialSemana extends Fragment
{
    SalesHistoryAdapter HistoryAdapter;
    ListView SalesListView;
    ProgressBar progressBar;

    //Global Variables
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historial_semana, container, false);

        SalesListView = (ListView) view.findViewById(R.id.lvVentasSemana);
        HistoryAdapter = new SalesHistoryAdapter(getActivity(), R.layout.custom_sales_history_item);
        SalesListView.setAdapter(HistoryAdapter);
        progressBar = (ProgressBar) view.findViewById(R.id.pbLoadingWeek);

        sessionManager = new SessionManager(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        SetProgressBarVisible(true);

        if(CheckConnection())
        {
            RequestCurrentWeekSalesHistory();
        }
        else
        {
            SetProgressBarVisible(false);
        }
    }


    public void RequestCurrentWeekSalesHistory()
    {
        YVScomSingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.HISTORY_GMT0 + "/week",
                        //StringsURL.HISTORY + "/week",
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessResponse(response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                HandleVolleyError(error);
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
        , 1); //Parametro de número de re-intentos
    }

    public void ProcessResponse(JSONObject pResponse)
    {
        SetProgressBarVisible(false);
        try
        {
            JSONObject History = pResponse.getJSONObject("History");
            JSONArray Transactions = History.getJSONArray("transactions");

            for (int i = 0; i < Transactions.length(); i++)
            {
                Sale sale = new Sale();

                try
                {
                    JSONObject JsonSale = Transactions.getJSONObject(i);

                    String DateSaleString = JsonSale.getString("date");
                    SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    //SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    //Format.setTimeZone(TimeZone.getDefault());
                    Format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date DateSale = Format.parse(DateSaleString);

                    /*Calendar c = Calendar.getInstance();

                    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());

                    Date Monday = c.getTime();
                    Date NextMonday= new Date(Monday.getTime()+7*24*60*60*1000);

                    if(DateSale.after(Monday) && DateSale.before(NextMonday))
                    {*/
                        if(JsonSale.isNull("id"))
                        {
                            sale.setID("0");
                        }
                        else
                        {
                            sale.setID(JsonSale.has("id") ? JsonSale.getString("id") : "0");
                        }

                        sale.setStatus(JsonSale.has("status") ? JsonSale.getString("status") : "");
                        sale.setMSISDN(JsonSale.has("msisdn") ? JsonSale.getString("msisdn") : "[Sin número]");
                        sale.setAmount(JsonSale.has("Amount") ? JsonSale.getDouble("Amount") : 0);
                        sale.setOperator(JsonSale.has("Operator") ? JsonSale.getString("Operator"): "");
                        sale.setPaymentStatus(JsonSale.has("paid") ? JsonSale.getBoolean("paid"): true);
                        sale.setDate(DateSale);

                        HistoryAdapter.add(sale);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                catch (ParseException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void HandleVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("ALGO HA SALIDO MAL...");
            alertDialog.setMessage(getString(R.string.something_went_wrong_try_again));
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            alertDialog.show();
        }
        else if(pError instanceof ServerError)
        {
            SetProgressBarVisible(false);
            //StatusCode 502 significa Token Inválido
            if(statusCode == 502)
            {
                Log.e("Error: ", networkResponse.toString());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.expired_session));
                alertDialog.setMessage(getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser();
                    }
                });
                alertDialog.show();
            }
            else
            {
                SetProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("ALGO HA SALIDO MAL...");
                alertDialog.setMessage(getString(R.string.something_went_wrong_try_again));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                alertDialog.show();
            }
        }
        else if (pError instanceof NetworkError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getString(R.string.internet_connecttion_title));
            alertDialog.setMessage(getString(R.string.internet_connecttion_msg));
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            alertDialog.show();
        }
        else if(pError instanceof AuthFailureError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("ERROR");
            alertDialog.setMessage("Las credenciales son incorrectas");
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    sessionManager.LogoutUser();
                }
            });
            alertDialog.show();
        }
    }

    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    public void SetProgressBarVisible(boolean pVisible)
    {

        if(pVisible)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean CheckConnection()
    {
        boolean connected;

        if(HaveNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = getString(R.string.no_internet_connection);
            Toast.makeText(getActivity(), connectionMessage, Toast.LENGTH_LONG).show();
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

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static Date getFirstDayOfWeekDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,  cal.getActualMinimum(Calendar.DAY_OF_WEEK));
        Date now = new Date();
        cal.setTime(now);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        return new Date(now.getTime() - 24 * 60 * 60 * 1000 * (week - 1));
    }

}
