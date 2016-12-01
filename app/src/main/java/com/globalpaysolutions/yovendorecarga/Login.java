package com.globalpaysolutions.yovendorecarga;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.text.InputType;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.DatabaseHandler;
import com.globalpaysolutions.yovendorecarga.customs.DeviceName;
import com.globalpaysolutions.yovendorecarga.customs.Encrypt;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.globalpaysolutions.yovendorecarga.model.Operator;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static String EncrptdPwd;
    public static boolean VendorM;
    public static String CountryID;
    public static String iso3Code;
    public static String PhoneCode;
    public String PublicIPAddress;
    public String lastEmailSignedin; //Para validar nuevo PIN
    public boolean lastRememberEmail;
    SessionManager sessionManager;
    Validation validator;
    TelephonyManager telephonyManager;
    String DeviceID;
    boolean isFirstTime;
    DatabaseHandler db;

    //Encriptacion
    String KEY = "8080808080808089"; //llave
    String IV = "8080808080808090"; // vector de inicialización

    public List<Operator> _listaOperadores = new ArrayList<>();
    int operatorsListCount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        db = new DatabaseHandler(this);
        telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        DeviceID = getDeviceID();

        etRegPass = (EditText) findViewById(R.id.etRegPass);
        etRegMail = (EditText) findViewById(R.id.etRegMail);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);

        InitializeValidation();
        setClickeableTextView();
        chkRemember.setChecked(true);

        isFirstTime = sessionManager.IsFirstTime();


        if (sessionManager.MustRememeberEmail())
        {
            String Email = RetrieveUserEmail();
            etRegMail.setText(Email);
        }

        try
        {
            lastEmailSignedin = RetrieveUserEmail();
            lastRememberEmail = sessionManager.MustRememeberEmail();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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



    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   LOGIN
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void Login(View view)
    {
        if (CheckValidation())
        {
            String Email = etRegMail.getText().toString().trim();
            String Pass = etRegPass.getText().toString().trim();

            UserEmail = Email;
            //Pww = Pass;

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

            //Obtiene la IP publica y despues arma la data
            // del dispositivo para enviarla en el SignIn
            ObtainPublicIPaddress(Email, Pass);


        }

    }

    public void SignIn(String pEmail, String pPass)
    {
        if (CheckConnection())
        {
            JSONObject jObject = new JSONObject();
            String deviceName = DeviceName();

            String encryptedPass = EncryptedPass(KEY, IV, pPass);
            EncrptdPwd = encryptedPass;

            try
            {
                jObject.put("email", pEmail);
                jObject.put("password", encryptedPass);
                jObject.put("deviceInfo", deviceName);
                jObject.put("deviceIP", PublicIPAddress);
                jObject.put("deviceID", DeviceID);
                System.out.println(jObject);
            } catch (JSONException e1)
            {
                e1.printStackTrace();
            }
            // Depurando objeto Json...
            Log.d(TAG, jObject.toString());

            // Envío de parámetros a servidor y obtención de respuesta
            YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, StringsURL.AUTH_SIGNIN,
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

            CountryID = SigninResponseObject.has("CountryID") ? SigninResponseObject.getString("CountryID") : "";
            iso3Code = SigninResponseObject.has("ISO3Code") ? SigninResponseObject.getString("ISO3Code") : "";
            PhoneCode =  SigninResponseObject.has("PhoneCode") ? SigninResponseObject.getString("PhoneCode") : "";

            sessionManager = new SessionManager(Login.this);
            sessionManager.CreateLoginSession(UserEmail, Token, Balance, EncrptdPwd, SessionID, VendorM, CountryID, iso3Code, PhoneCode);


            //Hace el request para traer el perfil del usuario
            RequestProfile();

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }




    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   PERFIL DEL USUARIO
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void RequestProfile()
    {
        YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, StringsURL.PROFILE, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("RequestProfile Resp. ", response.toString());
                ProcessProfileResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Log.e("RequestProfile Err. ", error.getLocalizedMessage());
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

            //INSERCION DE OPERADORES EN BD LOCAL
            /*int countryID = Integer.valueOf(RetrieveUserCountryID());
            List<Operator> userOperators = db.getUserOperators(countryID);
            if (userOperators.isEmpty())
            {
                Log.i("db: ", "Operators empty on BD");
                RetrieveOperators();
            }
            else
            {*/
                if(RetrieveUserPin().isEmpty())
                {
                    Intent pinIntent = new Intent(this, PIN.class);
                    pinIntent.putExtra("PIN_CONF", "SET_FIRST_TIME");
                    startActivity(pinIntent);
                    finish();
                }
                else if(!lastEmailSignedin.equals(RetrieveUserEmail()) || !lastRememberEmail ||  !sessionManager.MustRememeberEmail())
                {
                    Intent pinIntent = new Intent(this, PIN.class);
                    pinIntent.putExtra("PIN_CONF", "SET_NEW_EMAIL_PIN");
                    startActivity(pinIntent);
                    finish();
                }
                else
                {
                    //Intent para abrir la siguiente Activity
                    Intent intent = new Intent(this, Home.class);
                    startActivity(intent);
                    finish();
                }
            //}


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }


    public void lastEmailSignedin()
    {
        String email = RetrieveUserEmail();

    }

    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   OPERADORES
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void RetrieveOperators()
    {
        YVScomSingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.OPERATORS,
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessOperatorsResponse(response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                HandleSilentVolleyError(error);
                            }
                        }
                )
                {

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

    public void ProcessOperatorsResponse(JSONObject pResponse)
    {
        int countryID = Integer.valueOf(RetrieveUserCountryID());
        try
        {
            JSONObject operators = pResponse.getJSONObject("operators");
            JSONArray countryOperators = operators.getJSONArray("countryOperators");

            for (int i = 0; i < countryOperators.length(); i++)
            {
                final Operator operator = new Operator();

                try
                {
                    JSONObject jsonOperator = countryOperators.getJSONObject(i);
                    operator.setID(jsonOperator.has("operatorID") ? jsonOperator.getInt("operatorID") : 0);
                    operator.setOperatorName(jsonOperator.has("name") ? jsonOperator.getString("name") : "");
                    operator.setLogo(jsonOperator.has("operatorLogo") ? jsonOperator.getString("operatorLogo") : "");
                    operator.setLogoURL(jsonOperator.has("logoUrl") ? jsonOperator.getString("logoUrl") : "");
                    operator.setLogoVersion(jsonOperator.has("logoVersion") ? jsonOperator.getInt("logoVersion") : 0);
                    operator.setBrand(jsonOperator.has("brand") ? jsonOperator.getString("brand") : "");
                    operator.setCountryID(countryID);
                    _listaOperadores.add(operator);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            //Obtener imagenes de cada objeto aqui
            downloadLogos(_listaOperadores);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void downloadLogos(List<Operator> _lista)
    {
        operatorsListCount = _lista.size();

        for(final Operator item : _lista)
        {
            YVScomSingleton.getInstance(this).addToRequestQueue(
                    new ImageRequest(item.getLogoURL(), new Response.Listener<Bitmap>()
                    {
                        @Override
                        public void onResponse(Bitmap bitmapResponse)
                        {
                            Operator newObject = item;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmapResponse.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] image = stream.toByteArray();
                            newObject.setLogoImage(image);

                            InsertOperatorOnBD(newObject);
                            operatorsListCount = operatorsListCount -1;

                            if(operatorsListCount == 0)
                            {
                                Log.i("BD; ", "Items todos los items insertados, ahora hacer intent");
                                if(RetrieveUserPin().isEmpty())
                                {
                                    Intent pinIntent = new Intent(Login.this, PIN.class);
                                    pinIntent.putExtra("PIN_CONF", "SET_FIRST_TIME");
                                    startActivity(pinIntent);
                                    finish();
                                }
                                else
                                {
                                    //Intent para abrir la siguiente Activity
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }, 0, 0, null, null), 1); //Parametro de número de re-intentos
        }
    }

    public void HandleSilentVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            Log.e("Montos: ","Ocurrió 'TimeoutError' o 'NoConnectionError'");
        }
        else if(pError instanceof ServerError)
        {
            if(statusCode == 502)
            {
                Log.e("Montos: ","Ocurrió 'ServerError', sesion expirada");
            }
            else
            {
                Log.e("Montos: ","Ocurrió un 'ServerError'.");
            }
        }
        else if (pError instanceof NetworkError)
        {
            Log.e("Montos: ","Ocurrió un 'NetworkError'.");
        }
        else if(pError instanceof AuthFailureError)
        {
            Log.e("Montos: ","Ocurrió un 'AuthFailureError'.");

        }
    }


    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   OTROS MÉTODOS
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
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

    public String RetrieveUserPin()
    {
        String securityPin = "";
        HashMap<String, String> MapToken = sessionManager.GetSecurityPin();
        securityPin = MapToken.get(SessionManager.KEY_PIN_CODE);

        if(!StringUtils.isNotBlank(securityPin))
        {
            securityPin = "";
        }

        return securityPin;
    }

    public String RetrieveUserCountryID()
    {
        String countryID = "";
        HashMap<String, String> MapCountryID = sessionManager.GetCountryID();
        countryID = MapCountryID.get(SessionManager.KEY_COUNTRY_ID);
        return countryID;
    }

    public void InsertOperatorOnBD(Operator pOperator)
    {
        db.addOperator(pOperator);
    }

    public void ObtainPublicIPaddress(final String pEmail, final String pPass)
    {
        //String RequestURL = "http://myexternalip.com/json";
        String RequestURL = "https://api.ipify.org?format=json";

        YVScomSingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET, RequestURL, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Response PublicIP ", response.toString());
                                try
                                {
                                    PublicIPAddress = response.has("ip") ? response.getString("ip") : "";
                                    SignIn(pEmail, pPass);
                                }
                                catch (JSONException ex)
                                {
                                    ex.printStackTrace();
                                    SignIn(pEmail, pPass);
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                //Log.d("Err. PublicIP ", error.getLocalizedMessage());
                                HandleSilentVolleyError(error);
                            }
                        }
                )
                {

                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                , 1); //Parametro de número de re-intentos
    }

    public String EncryptedPass(String pKey, String pIV, String pClearText)
    {
        String _encrypted = "";
        try
        {
            _encrypted = Encrypt.encrypt(pKey, pIV, pClearText);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return _encrypted;
    }

}
