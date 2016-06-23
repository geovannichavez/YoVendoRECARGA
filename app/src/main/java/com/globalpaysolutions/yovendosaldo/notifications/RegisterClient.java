package com.globalpaysolutions.yovendosaldo.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Josué Chávez on 10/06/2016.
 */
public class RegisterClient
{
    SessionManager sessionManager;

    private static final String NOTIFICATIONS_SETTINGS = "NotificationsHubSettings";
    private static final String REGID_SETTING_NAME = "NotificationsHubRegistrationId";
    SharedPreferences NotificationsSettings;
    protected HttpClient httpClient;
    Context _context;

    public RegisterClient(Context context)
    {
        super();
        this.NotificationsSettings = context.getSharedPreferences(NOTIFICATIONS_SETTINGS, 0);
        _context = context;
        httpClient = new DefaultHttpClient();
        sessionManager = new SessionManager(_context);
    }

    public void RegisterDevice(String handle, Set<String> tags) throws ClientProtocolException, IOException, JSONException
    {
        //Obtiene o solicta el RegistrationID en el Hub
        String registrationId = RetrieveRegistrationIdOrRequestNewOne(handle);

        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("Platform", "gcm");
        deviceInfo.put("Handle", handle); //SenderID
        deviceInfo.put("Tags", new JSONArray(tags));

        //Registra el dispositvo en el Hub
        int statusCode = UpsertDeviceRegistration(registrationId, deviceInfo);

        if (statusCode == HttpStatus.SC_OK)
        {
            return;
        }
        else if (statusCode == HttpStatus.SC_GONE)
        {

            NotificationsSettings.edit().remove(REGID_SETTING_NAME).apply();
            registrationId = RetrieveRegistrationIdOrRequestNewOne(handle);
            statusCode = UpsertDeviceRegistration(registrationId, deviceInfo);
            if (statusCode != HttpStatus.SC_OK)
            {
                Log.e("RegisterClient", "Error upserting registration: " + statusCode);
                throw new RuntimeException("Error upserting registration");
            }
        }
        else
        {
            Log.e("RegisterClient", "Error upserting registration: " + statusCode);
            throw new RuntimeException("Error upserting registration");
        }
    }

    /*
    Hace el registro del dispositivo en el Hub usando el RegistrationID, devuelve el
    codigo de estatus del resultado del registro
    */
    private int UpsertDeviceRegistration(String registrationId, JSONObject deviceInfo) throws UnsupportedEncodingException, IOException, ClientProtocolException
    {
        HttpPut request = new HttpPut(StringsURL.DEVICEREGISTRATION + registrationId);
        request.setEntity(new StringEntity(deviceInfo.toString()));
        request.addHeader("Token-autorization", RetrieveSavedToken());
        request.addHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode;
    }


    /*
    Devuelve el registrationID, si no hay uno guardado localmente
    solicita uno desde el Hub
    */
    private String RetrieveRegistrationIdOrRequestNewOne(String handle) throws ClientProtocolException, IOException
    {
        if (NotificationsSettings.contains(REGID_SETTING_NAME))
            return NotificationsSettings.getString(REGID_SETTING_NAME, null);

        HttpUriRequest request = new HttpPost(StringsURL.DEVICEREGISTRATION + "?handle=" + handle);
        request.addHeader("Token-autorization", RetrieveSavedToken());
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
        {
            Log.e("RegisterClient", "Error creating registrationId: " + response.getStatusLine().getStatusCode());
            throw new RuntimeException("Error creating Notification Hubs registrationId");
        }
        String registrationId = EntityUtils.toString(response.getEntity());
        registrationId = registrationId.substring(1, registrationId.length() - 1);

        NotificationsSettings.edit().putString(REGID_SETTING_NAME, registrationId).apply();

        return registrationId;
    }







    /*
    *
    *   OTHER METHODS
    *
    */
    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    /*public void RegisterDevice(String handle, Set<String> tags)
    {
        String registrationId = RetrieveRegistrationIdOrRequestNewOne(handle);
        JSONObject deviceInfo = new JSONObject();
        try
        {
            deviceInfo.put("Platform", "gcm");
            deviceInfo.put("Handle", handle);
            deviceInfo.put("Tags", new JSONArray(tags));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        int statusCode = UpsertDeviceRegistration(registrationId, deviceInfo);

        if (statusCode == 200)
        {
            return;
        }
        else if (statusCode == 410) //GONE
        {
            NotificationsSettings.edit().remove(REGID_SETTING_NAME).apply();
            registrationId = RetrieveRegistrationIdOrRequestNewOne(handle);
            statusCode = UpsertDeviceRegistration(registrationId, deviceInfo);
            if (statusCode != 200)
            {
                Log.e("RegisterClient", "Error upserting registration: " + statusCode);
            }
        }
        else
        {
            Log.e("RegisterClient", "Error upserting registration: " + statusCode);
            throw new RuntimeException("Error upserting registration");
        }
    }

    private int UpsertDeviceRegistration(String pRegistrationID, JSONObject pDeviceInfo)
    {
        YVScomSingleton.getInstance(_context).addToRequestQueue(
                new JsonObjectRequest(Request.Method.PUT,
                        StringsURL.DEVICEREGISTRATION + pRegistrationID,
                        pDeviceInfo,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ResultDeviceRegistration = 200;
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                int statusCode = 0;
                                NetworkResponse networkResponse = error.networkResponse;

                                if(networkResponse != null)
                                {
                                    statusCode = networkResponse.statusCode;
                                }

                                ResultDeviceRegistration = statusCode;
                            }
                        }
                )
                {
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", RetrieveSavedToken());
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 0);

        return ResultDeviceRegistration;
    }

    private String RetrieveRegistrationIdOrRequestNewOne(String handle)
    {
        if (NotificationsSettings.contains(REGID_SETTING_NAME))
        {
            return NotificationsSettings.getString(REGID_SETTING_NAME, null);
        }

        YVScomSingleton.getInstance(_context).addToRequestQueue(
                new JsonObjectRequest(Request.Method.POST,
                        StringsURL.DEVICEREGISTRATION + "?handle=" + handle,
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                RegistrationId = response.toString();
                                RegistrationId = RegistrationId.substring(1, RegistrationId.length() - 1);
                                NotificationsSettings.edit().putString(REGID_SETTING_NAME, RegistrationId).apply();
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                ProcessErrorResponse(error);
                            }
                        }
                )
                {
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", RetrieveSavedToken());
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 0);


        return RegistrationId;
    }*/


    /*public void ProcessErrorResponse(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            Log.e("ANH Registration: ","Ocurrió 'TimeoutError' o 'NoConnectionError'");
        }
        else if(pError instanceof ServerError)
        {
            if(statusCode == 502)
            {
                Log.e("ANH Registration: ","Ocurrió 'ServerError', sesion expirada");
            }
            else
            {
                Log.e("ANH Registration: ","Ocurrió un 'ServerError'.");
            }
        }
        else if (pError instanceof NetworkError)
        {
            Log.e("ANH Registration: ","Ocurrió un 'NetworkError'.");
        }
        else if(pError instanceof AuthFailureError)
        {
            Log.e("ANH Registration: ","Ocurrió un 'AuthFailureError'.");
        }
    }*/


}
