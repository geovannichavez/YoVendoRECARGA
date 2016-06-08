package com.globalpaysolutions.yovendosaldo.customs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.globalpaysolutions.yovendosaldo.Home;
import com.globalpaysolutions.yovendosaldo.Login;
import com.globalpaysolutions.yovendosaldo.model.Amount;
import com.globalpaysolutions.yovendosaldo.model.PaymentItem;
import com.globalpaysolutions.yovendosaldo.model.Sale;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Josué Chávez on 30/05/2016.
 *
 * Data: Se almacena data temporal que se utilizarña únicamente
 * en tiempo de ejecución.
 */
public class Data
{
    public static List<PaymentItem> PaymentItems = new ArrayList<>();
    public static List<Amount> Amounts = new ArrayList<>();
    public static List<Amount> resultAmountList = new ArrayList<>();

    static SessionManager sessionManager;

    public static void ManagePaymentItems(PaymentItem pItem)
    {
        if (PaymentItems.contains(pItem))
        {
            // do not add
            //PaymentItem oldItem = PaymentItems.get(PaymentItems.indexOf(pItem));
            PaymentItems.set(PaymentItems.indexOf(pItem), pItem);
        }
        else
        {
            PaymentItems.add(pItem);
        }
    }

    public static PaymentItem GetItemStatus(String pItemID)
    {
        PaymentItem item;
        item = PaymentItems.get(PaymentItems.indexOf(pItemID));

        return item;
    }


    public static void SignOut(final Context pContext)
    {

        JSONObject jSignout = new JSONObject();
        try
        {
            jSignout.put("SessionID", RetrieveSessionID(pContext));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        StringsURL.SIGNOUT,
                        jSignout,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                NavigateLogin(pContext);
                                //ProcessVoucherResponse(response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                //HandleVolleyError(error);
                            }
                        }
                )
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        String pToken = RetrieveSavedToken(pContext);
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-autorization", pToken );
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1); //Parametro, de maximo de re-intentos
    }


    /*
    *
    *   RETRIEVE AMOUNTS
    *
    */

    public static void GetAmounts(final Context pContext)
    {
        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.PRODUCTS + "slv",
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessAmountResponse(response, pContext);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                ProcessAmountErrorResponse(error, pContext);
                            }
                        }
                )
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", RetrieveSavedToken(pContext));
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                , 1); //Parametro de número de re-intentos
    }

    public static void ProcessAmountResponse(JSONObject pResonse, Context pContext)
    {
        try
        {
            JSONArray jProducts = pResonse.getJSONArray("products");

            for(int i = 0; i < jProducts.length(); i++)
            {
                JSONObject JsonProductItem = jProducts.getJSONObject(i);
                JSONArray jDenomination = JsonProductItem.getJSONArray("denomination");

                for (int a = 0; a < jDenomination.length(); a++)
                {
                    JSONObject jDenominationItem = jDenomination.getJSONObject(a);
                    Amount amount = new Amount();

                    //Obtiene los valores del Item
                    String amountCode = jDenominationItem.has("Code") ? jDenominationItem.getString("Code") : "";
                    String amountDisplay = jDenominationItem.has("Description") ? jDenominationItem.getString("Description") : "";
                    String amountAmount = jDenominationItem.has("Amount") ? jDenominationItem.getString("Amount") : "";

                    if(amountDisplay.isEmpty())
                    {
                        amountDisplay = amountAmount;
                    }

                    //Setea el objeto Amount con las respectivas propiedades
                    amount.setMNO(JsonProductItem.has("mno") ? JsonProductItem.getString("mno") : "");
                    amount.setAditionalText("");
                    amount.setDecimal("");
                    amount.setCode(amountCode);
                    amount.setDisplay(amountDisplay);

                    //Remueve los decimales y converte el resultado en Int
                    String strIntAmount = StringUtils.removeEnd(amountAmount, ".00");
                    int intAmount = Integer.valueOf(strIntAmount);

                    //Lo añade al objeto amount
                    amount.setAmount(intAmount);

                    Data.Amounts.add(amount);
                    Log.i("Monto", amount.getDisplay() + " " + amount.getMNO() );

                }
            }

            /*try {
                JSONArray jAmounts =  JsonProductItem.getJSONArray("denomination");
                amount.setMNO(JsonProductItem.getString("mno"));
                for(int a = 0; a < jAmounts.length(); a++) {
                    amount.setAditionalText("");
                    amount.setDecimal("");
                    amount.setDisplay(jAmounts.getString(a));
                    String strIntAmount = StringUtils.removeEnd(jAmounts.getString(a), ".00");
                    int intAmount = Integer.valueOf(strIntAmount);
                    amount.setAmount(intAmount);
                    Amounts.add(amount);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }*/

            Amount Hint = new Amount();
            Hint.setDisplay(pContext.getString(R.string.spinner_hint));
            Hint.setAmount(0);
            Hint.setDecimal("");
            Hint.setAditionalText("");
            Amounts.add(Hint);

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

    }

    public static void ProcessAmountErrorResponse(VolleyError pError, Context pContex)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            //Toast.makeText(pContex, pContex.getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            Log.e("Montos: ","Ocurrió 'TimeoutError' o 'NoConnectionError'");
        }
        else if(pError instanceof ServerError)
        {
            if(statusCode == 502)
            {
               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(pContex);
                alertDialog.setTitle(pContex.getString(R.string.expired_session));
                alertDialog.setMessage(pContex.getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser();
                    }
                });
                alertDialog.show();*/
                Log.e("Montos: ","Ocurrió 'ServerError', sesion expirada");
            }
            else
            {
                //Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
                Log.e("Montos: ","Ocurrió un 'ServerError'.");
            }
        }
        else if (pError instanceof NetworkError)
        {
            //Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
            Log.e("Montos: ","Ocurrió un 'NetworkError'.");
        }
        else if(pError instanceof AuthFailureError)
        {
            /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle("ERROR");
            alertDialog.setMessage("Las credenciales son incorrectas");
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    sessionManager.LogoutUser();
                }
            });
            alertDialog.show();*/
            Log.e("Montos: ","Ocurrió un 'AuthFailureError'.");

        }
    }


    public static Amount AmountHint(Context pContex)
    {
        Amount Hint = new Amount();
        Hint.setDisplay(pContex.getString(R.string.spinner_hint));
        Hint.setAmount(0);
        Hint.setDecimal("");
        Hint.setAditionalText("");

        return Hint;
    }



    /*
    *
    *   OTHER METHODS
    *
    */

    public static int RetrieveSessionID(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        int sessionId;
        HashMap<String, Integer> SessionID = sessionManager.RetrieveSessionID();
        sessionId = SessionID.get(SessionManager.KEY_SESSION_ID);

        return sessionId;
    }

    public static String RetrieveSavedToken(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    public static void NavigateLogin(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        sessionManager.DeleteSavedToken();

        Intent i = new Intent(pContext, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(i);
        ((Activity)pContext).finish();
    }

}
