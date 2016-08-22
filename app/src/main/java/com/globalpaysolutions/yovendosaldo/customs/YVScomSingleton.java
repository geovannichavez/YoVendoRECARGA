package com.globalpaysolutions.yovendosaldo.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Geovanni on 02/04/2016.
 */
public final class YVScomSingleton
{
    private ImageLoader imageLoader;
    private static YVScomSingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private YVScomSingleton(Context context)
    {
        YVScomSingleton.context = context;
        requestQueue = getRequestQueue();
        //Esta parte de Imagen Loader es para la optimizacion de la imagen
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache()
        {
            private final LruCache<String, Bitmap>  cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized YVScomSingleton getInstance(Context context)
    {
        if (singleton == null)
        {
            singleton = new YVScomSingleton(context);
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
        //int socketTimeout = 5000;//5 segundos
        //int socketTimeout = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48;
        //int socketTimeout = 10000;//10 segundos
        //int socketTimeout = 15000;//15 segundos
        int socketTimeout = 0;
        //int socketTimeout = 120000;

        //Setea el Timeout para la Request
        //int socketTimeout = 45000;//45 segundos
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, pMaxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        int retryIntents = policy.getCurrentRetryCount();
        Log.i("CurrentRetryCount", String.valueOf(retryIntents));

        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader()
    {
        return imageLoader;
    }
}
