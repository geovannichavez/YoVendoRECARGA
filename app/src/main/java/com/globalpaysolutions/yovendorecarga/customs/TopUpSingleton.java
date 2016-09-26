package com.globalpaysolutions.yovendorecarga.customs;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

/**
 * Created by Josué Chávez on 23/08/2016.
 */
public class TopUpSingleton
{
    private static TopUpSingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private TopUpSingleton(Context pContext)
    {
        TopUpSingleton.context = pContext;
        requestQueue = getRequestQueue();
    }

    public static synchronized TopUpSingleton getInstance(Context context)
    {
        if (singleton == null)
        {
            singleton = new TopUpSingleton(context);
        }
        return singleton;
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public  void addToRequestQueue(Request req, int pMaxRetries)
    {
        int socketTimeout = 0;

        //Setea el Timeout para la Request
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, pMaxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        int retryIntents = policy.getCurrentRetryCount();
        Log.i("CurrentRetryCount", String.valueOf(retryIntents));
        Log.i("RequestURL: ", req.getUrl());

        getRequestQueue().add(req);
    }
}
