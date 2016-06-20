package com.globalpaysolutions.yovendosaldo;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

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
import com.globalpaysolutions.yovendosaldo.adapters.NotificationsAdapter;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;
import com.globalpaysolutions.yovendosaldo.model.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notificaciones extends AppCompatActivity
{
    //Layout y vistas
    Toolbar toolbar;
    ListView NotifListView;
    ProgressBar progressBar;
    SwipeRefreshLayout SwipeRefresh;

    //Adapters
    NotificationsAdapter NotifAdapter;

    //Variables globales
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        toolbar = (Toolbar) findViewById(R.id.notifToolbar);
        toolbar.setTitle(getString(R.string.title_activity_notificaciones));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.pbLoadingNotif);
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_notifications);

        NotifListView = (ListView) findViewById(R.id.lvNotificaciones);
        NotifAdapter = new NotificationsAdapter(Notificaciones.this, R.layout.custom_notification_listview_item);
        NotifListView.setAdapter(NotifAdapter);

        /*
        *
        *   LISTVIEW
        *   Detecta si la primer fila del List está en la posición primer mas alta,
        *   entonces habilita el SwipeRefreshLayout, de lo contrario lo deshabilita.
        */
        NotifListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int topRowVerticalPosition = (NotifListView == null || NotifListView.getChildCount() == 0) ? 0 : NotifListView.getChildAt(0).getTop();
                SwipeRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        sessionManager = new SessionManager(this);



        /*
        *
        *   SWIPEREFRESH
        *
        */
        SwipeRefresh.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2, R.color.refresh_progress_3);
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                RequestNotificationsHistory(true);
            }
        });


        RequestNotificationsHistory(false);


    }

    public void RequestNotificationsHistory(boolean isSwipe)
    {
        if (isSwipe)
        {
            SetProgressBarVisible(false);
            SwipeRefresh.setRefreshing(true);
            NotifAdapter.clear();
            NotifAdapter.notifyDataSetChanged();
        }
        else
        {
            SetProgressBarVisible(true);
        }

        YVScomSingleton.getInstance(Notificaciones.this)
                .addToRequestQueue(new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.NOTIFICATIONSHISTORY,
                        null,
            new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("Mensaje JSON ", response.toString());
                ProcessResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HandleVolleyError(error);
            }
        })
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
        }, 1); //Parametro de número de re-intentos
    }


    public void ProcessResponse(JSONObject pResponse)
    {
        HideSwipe();
        SetProgressBarVisible(false);
        try
        {
            JSONObject NotificationsHistory = pResponse.getJSONObject("NotificationsHistory");
            JSONArray Notifications = NotificationsHistory.getJSONArray("notifications");

            for (int i = 0; i < Notifications.length(); i++)
            {
                Notification notification = new Notification();

                try
                {
                    JSONObject JsonNotification = Notifications.getJSONObject(i);

                    //Obtención de fecha
                    String StrNotificationDate = JsonNotification.has("Date") ? JsonNotification.getString("Date") : "";
                    SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date DateNotification = Format.parse(StrNotificationDate);

                    //Asignacion a objeto para Adapter
                    notification.setTitle(JsonNotification.has("Title") ? JsonNotification.getString("Title") : "");
                    notification.setContent(JsonNotification.has("Message") ? JsonNotification.getString("Message") : "");
                    notification.setDate(DateNotification);

                    NotifAdapter.add(notification);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void HandleVolleyError(VolleyError pError)
    {
        HideSwipe();

        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
        else if (pError instanceof ServerError)
        {
            SetProgressBarVisible(false);
            if (statusCode == 502)
            {
                Log.e("Error: ", networkResponse.toString());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
        else if (pError instanceof AuthFailureError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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


    /*
    *
    *   OTROS MÉTODOS
    *
    */

    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    public void SetProgressBarVisible(boolean pVisible)
    {

        if (pVisible)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void HideSwipe()
    {
        if (SwipeRefresh.isShown() && SwipeRefresh != null)
        {
            SwipeRefresh.setRefreshing(false);
        }
    }
}
