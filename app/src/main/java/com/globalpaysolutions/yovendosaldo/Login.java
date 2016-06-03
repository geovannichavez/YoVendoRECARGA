package com.globalpaysolutions.yovendosaldo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    SessionManager sessionManager;
    Validation validator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);

        etRegPass = (EditText) findViewById(R.id.etRegPass);
        etRegMail = (EditText) findViewById(R.id.etRegMail);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);

        InitializeValidation();
        setClickeableTextView();
        chkRemember.setChecked(true);


        if(sessionManager.MustRememeberEmail())
        {
            String Email = RetrieveUserEmail();
            etRegMail.setText(Email);

        }
    }

    public void Login(View view)
    {
        if(CheckValidation())
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

            if(chkRemember.isChecked())
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
            String deviceName = DeviceName.getDeviceName() ;

            try
            {
                jObject.put("email", pEmail);
                jObject.put("password", pPass);
                jObject.put("deviceInfo", deviceName);
                jObject.put("deviceIP", Utils.getIPAddress(true));
                System.out.println(jObject);
            }
            catch (JSONException e1)
            {
                e1.printStackTrace();
            }
            // Depurando objeto Json...
            Log.d(TAG, jObject.toString());

            // Envío de parámetros a servidor y obtención de respuesta
            YVScomSingleton.getInstance(this).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            StringsURL.SIGNIN,
                            //StringsURL.TEST_TIMEOUT,
                            jObject,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    Log.d("Mensaje JSON ", response.toString());
                                    ProcessSigninResponse(response);
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
            ,0);//Parametro, de maximo de re-intentos
        }
    }

    public void ProcessSigninResponse(JSONObject pResponse)
    {
        try
        {
            JSONObject SigninResponseObject = pResponse;
            Token = SigninResponseObject.has("token") ? SigninResponseObject.getString("token") : "";
            Balance = SigninResponseObject.has("AvailableAmount") ? SigninResponseObject.getString("AvailableAmount") : "";
            SessionID = SigninResponseObject.has("SesionID") ? SigninResponseObject.getInt("SesionID"): 0;

            sessionManager = new SessionManager(Login.this);
            sessionManager.CreateLoginSession(UserEmail, Token, Balance, Pww, SessionID);


            //Hace el request para traer el perfil del usuario
            RequestProfile();

        }
        catch (JSONException e)
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
        YVScomSingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.PROFILE,
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessProfileResponse(response);
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
                        headers.put("Token-Autorization", Token);
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                , 1); //Parametro de número de re-intentos
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
        }
        catch (JSONException e)
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

        if(HaveNetworkConnection() != true)
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

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            ProgressDialog.dismiss();
            CreateDialog(getString(R.string.we_are_sorry_msg_title), getString(R.string.something_went_wrong_try_again));
        }
        else if(pError instanceof ServerError)
        {
            ProgressDialog.dismiss();

            //StatusCode 502 significa Token Inválido
            if(statusCode == 502)
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
        else if(pError instanceof AuthFailureError)
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


}
