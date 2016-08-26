package com.globalpaysolutions.yovendosaldo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
import com.globalpaysolutions.yovendosaldo.customs.DeviceName;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.Utils;
import com.globalpaysolutions.yovendosaldo.customs.Validation;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity
{
    //Activity Layouts and Views
    EditText etRegPass;
    EditText etRegMail;
    TextView tvTerms;
    TextView tvPolitics;
    TextView tvForgottenPass;
    CheckBox chkRemember;
    android.app.ProgressDialog ProgressDialog;

    //Activity Global Variables
    private static final String TAG = Home.class.getSimpleName();
    public static String Token;
    public static String Balance;
    public static int SessionID;
    public static String UserEmail;
    public static String Pww;
    public static boolean VendorM;
    SessionManager sessionManager;
    Validation validator;
    TelephonyManager telephonyManager;
    String DeviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        DeviceID = getDeviceID();

        etRegPass = (EditText) findViewById(R.id.etRegPass);
        etRegMail = (EditText) findViewById(R.id.etRegMail);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);

        InitializeValidation();
        setClickeableTextView();
        chkRemember.setChecked(true);


        if (sessionManager.MustRememeberEmail())
        {
            String Email = RetrieveUserEmail();
            etRegMail.setText(Email);

        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (ProgressDialog != null && ProgressDialog.isShowing())
        {
            ProgressDialog.dismiss();
        }
    }

    public void Login(View view)
    {
        if (CheckValidation())
        {
            String Email = etRegMail.getText().toString().trim();
            String Pass = etRegPass.getText().toString().trim();

            UserEmail = Email;
            Pww = Pass;

            ProgressDialog = new ProgressDialog(Login.this);
            ProgressDialog.setMessage(getResources().getString(R.string.dialog_logging_in));
            ProgressDialog.show();
            ProgressDialog.setCancelable(false);
            ProgressDialog.setCanceledOnTouchOutside(false);

            if (chkRemember.isChecked())
            {
                sessionManager.RememberEmail(true);
            }
            else
            {
                sessionManager.RememberEmail(false);
            }

            SignIn(Email, Pass);
        }

    }

    public void SignIn(String pEmail, String pPass)
    {
        if (CheckConnection())
        {
            JSONObject jObject = new JSONObject();
            String deviceName = DeviceName();
            String IpAddress = getPublicIPAddress();

            try
            {
                jObject.put("email", pEmail);
                jObject.put("password", pPass);
                jObject.put("deviceInfo", deviceName);
                jObject.put("deviceIP", IpAddress);
                jObject.put("deviceID", DeviceID);
                System.out.println(jObject);
            } catch (JSONException e1)
            {
                e1.printStackTrace();
            }
            // Depurando objeto Json...
            Log.d(TAG, jObject.toString());

            // Envío de parámetros a servidor y obtención de respuesta
            YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, StringsURL.SIGNIN,
                    //StringsURL.TEST_TIMEOUT,
                    jObject, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.d("Mensaje JSON ", response.toString());
                    ProcessSigninResponse(response);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    HandleVolleyError(error);
                }
            }), 0);//Parametro, de maximo de re-intentos
        }
    }

    public void ProcessSigninResponse(JSONObject pResponse)
    {
        try
        {
            JSONObject SigninResponseObject = pResponse;
            Token = SigninResponseObject.has("token") ? SigninResponseObject.getString("token") : "";
            Balance = SigninResponseObject.has("AvailableAmount") ? SigninResponseObject.getString("AvailableAmount") : "";
            SessionID = SigninResponseObject.has("SesionID") ? SigninResponseObject.getInt("SesionID") : 0;
            VendorM = SigninResponseObject.has("VendorM") ? SigninResponseObject.getBoolean("VendorM") : false;

            sessionManager = new SessionManager(Login.this);
            sessionManager.CreateLoginSession(UserEmail, Token, Balance, Pww, SessionID, VendorM);


            //Hace el request para traer el perfil del usuario
            RequestProfile();

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    /*
    *
    *
    *   PERFIL DEL USUARIO
    *
    */

    public void RequestProfile()
    {
        YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, StringsURL.PROFILE, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("Mensaje JSON ", response.toString());
                ProcessProfileResponse(response);
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
                headers.put("Token-Autorization", Token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        }, 1); //Parametro de número de re-intentos
    }

    public void ProcessProfileResponse(JSONObject pResponse)
    {
        final JSONObject ProfileResponse = pResponse;

        try
        {

            JSONObject Profile = ProfileResponse.getJSONObject("profile");
            String FirstName = Profile.has("first_name") ? Profile.getString("first_name") : "";
            String LastName = Profile.has("last_name") ? Profile.getString("last_name") : "";

            sessionManager = new SessionManager(Login.this);
            sessionManager.SaveUserProfile(FirstName, LastName);

            //Intent para abrir la siguiente Activity
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }


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

        if (HaveNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = getString(R.string.no_internet_connection);
            Toast.makeText(getApplicationContext(), connectionMessage, Toast.LENGTH_LONG).show();
            ProgressDialog.dismiss();
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

    public void setClickeableTextView()
    {
        tvForgottenPass = (TextView) findViewById(R.id.tvForgottenPass);


        tvForgottenPass.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /*Intent intent = new Intent(this, OtherActivity.class);
                startActivity(intent);*/
            }
        });
    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        validator = new Validation(Login.this);

        if (!validator.IsEmailAddress(etRegMail, true))
        {
            ret = false;
        }
        if (!validator.HasText(etRegPass))
        {
            ret = false;
        }

        return ret;
    }

    public void InitializeValidation()
    {
        /*
        *
        *   VALIDATING EMAIL
        *
        */
        etRegPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etRegPass.setTransformationMethod(new PasswordTransformationMethod());
        etRegPass.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    validator = new Validation(Login.this);
                    validator.HasText(etRegPass);
                }
            }
        });

        /*
        *
        *   VALIDATING EMAIL
        *
        */
        etRegMail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etRegMail.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            /*  EMAIL */
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    validator = new Validation(Login.this);
                    validator.IsEmailAddress(etRegMail, true);
                    validator.HasText(etRegMail);
                }
            }
        });
    }

    public void HandleVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            ProgressDialog.dismiss();
            CreateDialog(getString(R.string.we_are_sorry_msg_title), getString(R.string.something_went_wrong_try_again));
        }
        else if (pError instanceof ServerError)
        {
            ProgressDialog.dismiss();

            //StatusCode 502 significa Token Inválido
            if (statusCode == 502)
            {
                Log.e("Error: ", networkResponse.toString());
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
                finish();
            }
            else
            {
                CreateDialog(getString(R.string.we_are_sorry_msg_title), getString(R.string.something_went_wrong_try_again));
            }
        }
        else if (pError instanceof NetworkError)
        {
            ProgressDialog.dismiss();
            CreateDialog(getString(R.string.internet_connecttion_title), getString(R.string.internet_connecttion_msg));
        }
        else if (pError instanceof AuthFailureError)
        {
            ProgressDialog.dismiss();
            CreateDialog("ERROR", "Las credenciales son incorrectas");
        }
    }

    public void CreateDialog(String pTitle, String pMessage)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setTitle(pTitle);
        alertDialog.setMessage(pMessage);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                etRegPass.setText("");
            }
        });
        alertDialog.show();
    }

    public String RetrieveUserEmail()
    {
        String UserEmail;
        HashMap<String, String> MapToken = sessionManager.GetUserEmail();
        UserEmail = MapToken.get(SessionManager.KEY_USER_EMAIL);
        return UserEmail;
    }

    public String getAndroidVersion()
    {
        String release = Build.VERSION.RELEASE;
        return "(Android " + " " + release + ")";
    }

    public String DeviceName()
    {
        String Manufacturer = Build.MANUFACTURER;
        String Model = DeviceName.getDeviceName();
        String versionRelease = Build.VERSION.RELEASE;

        Manufacturer = Manufacturer.substring(0, 1).toUpperCase() + Manufacturer.substring(1).toLowerCase();

        return Manufacturer + " " + Model + " (Android " + versionRelease + ")";
    }

    public String getPublicIPAddress()
    {
        String RequestURL = "http://myexternalip.com/json";

        String publicIpAddress = "";
        try
        {
            publicIpAddress = new RetrievePublicIPAddress().execute(RequestURL).get();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return publicIpAddress;
    }

    public String getDeviceID()
    {
        final int REQUEST_READ_PHONE_STATE = 2;
        final String tmDevice;
        final String tmSerial;
        final String androidId;
        String DeviceID = "";
        UUID deviceUuid = null;

        try
        {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            }
            else
            {
                tmDevice = "" + telephonyManager.getDeviceId();
                tmSerial = "" + telephonyManager.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                DeviceID = deviceUuid.toString().toUpperCase();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return DeviceID;
    }

    public class RetrievePublicIPAddress extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String ipAddress = "";
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(params[0]);
                HttpResponse response;

                response = httpclient.execute(httpget);

                HttpEntity entity = response.getEntity();

                if (entity != null)
                {
                    String retSrc = EntityUtils.toString(entity);
                    JSONObject result = new JSONObject(retSrc);
                    ipAddress = result.getString("ip");
                }

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return ipAddress;
        }

        @Override
        protected void onPostExecute(String pIPAddress)
        {
            super.onPostExecute(pIPAddress);
        }
    }

}
