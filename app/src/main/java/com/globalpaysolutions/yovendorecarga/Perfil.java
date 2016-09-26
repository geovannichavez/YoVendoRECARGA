package com.globalpaysolutions.yovendorecarga;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
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
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Perfil extends AppCompatActivity
{
    //Layouts and Views
    EditText etNewPass;
    EditText etConfirmPass;
    EditText etCurrentPass;
    ProgressDialog progressDialog;
    TextView tvEmailUsuario;
    TextView tvNombreUsuario;
    Button btnUpdate;
    Toolbar toolbar;

    //Global Fragment variables
    Validation Validator;
    SessionManager sessionManager;
    CustomFullScreenDialog FullScreenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbar = (Toolbar) findViewById(R.id.toolbarProf);
        toolbar.setTitle(getString(R.string.title_activity_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnUpdate = (Button) findViewById(R.id.btnChangePass);
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
        etCurrentPass = (EditText) findViewById(R.id.etCurrentPass);
        tvEmailUsuario = (TextView) findViewById(R.id.tvEmailUsuario);
        tvNombreUsuario = (TextView) findViewById(R.id.tvNombreUsuario);

        sessionManager = new SessionManager(Perfil.this);
        FullScreenDialog = new CustomFullScreenDialog(Perfil.this, this);

        HashMap<String, String> UserInfo = sessionManager.GetUserSessionInfo();
        String UserEmail = UserInfo.get(SessionManager.KEY_USER_EMAIL);
        tvEmailUsuario.setText(UserEmail);

        HashMap<String, String> UserNames = sessionManager.GetUserProfile();
        String FirstName = UserNames.get(SessionManager.KEY_FIRST_NAME);
        String LastName = UserNames.get(SessionManager.KEY_LAST_NAME);
        tvNombreUsuario.setText(FirstName + " " + LastName);


        InitializeValidation();

    }


    public void ChangePassword(View view)
    {
        if (CheckValidation())
        {
            if (CheckConnection())
            {
                progressDialog = new ProgressDialog(Perfil.this);
                progressDialog.setMessage("Espere...");
                progressDialog.show();

                String Current = etCurrentPass.getText().toString();
                String New = etNewPass.getText().toString();

                UpdatePassword(Current, New);
            }
        }
    }

    public void UpdatePassword(String pCurrentPass, String pNewPass)
    {
        JSONObject jObject = new JSONObject();

        try
        {
            jObject.put("oldpassword", pCurrentPass);
            jObject.put("newpassword", pNewPass);
            System.out.println(jObject);
        }
        catch (JSONException e1)
        {
            e1.printStackTrace();
        }

        YVScomSingleton.getInstance(Perfil.this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST,
                StringsURL.PASSWORD,
                jObject, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                //Procesar la respuesta del servidor
                Log.d("Mensaje JSON ", response.toString());
                ProcessResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HandleVolleyError(error);
                /*try
                {
                    String erroMessage = error.getMessage();
                    JSONObject errorResponse = new JSONObject(erroMessage);
                    ProcessErrorResponse(errorResponse);
                }
                catch (JSONException ex)
                {
                    ex.printStackTrace();
                }*/
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
        final JSONObject PassResponse = pResponse;

        try
        {
            String Token = PassResponse.has("token") ? PassResponse.getString("token") : "";
            sessionManager = new SessionManager(Perfil.this);
            sessionManager.UpdateSavedToken(Token);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        progressDialog.hide();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Perfil.this);
        alertDialog.setTitle(getString(R.string.succeed_title));
        alertDialog.setMessage(getString(R.string.password_changed_success));
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        alertDialog.show();

    }

    public void ProcessErrorResponse(JSONObject pResponse)
    {
        progressDialog.hide();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Perfil.this);
        alertDialog.setTitle(getString(R.string.dialog_password_update_error_title));
        alertDialog.setMessage(getString(R.string.try_again_message));
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                ClearEditTexts();
            }
        });
        alertDialog.show();
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
            progressDialog.dismiss();
            FullScreenDialog.CreateFullScreenDialog(getString(R.string.we_are_sorry_msg_title),
                    getString(R.string.something_went_wrong_try_again), null, null, "Aceptar", "NEWACTION", true, false );
        }
        else if(pError instanceof ServerError)
        {
            //StatusCode 502 significa Token Inválido
            if(statusCode == 502)
            {
                Log.e("Error: ", networkResponse.toString());
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.expired_session),
                        getString(R.string.dialog_error_topup_content), null, null, "Aceptar", "LOGOUT", true, false );
            }
            else
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.we_are_sorry_msg_title),
                        getString(R.string.something_went_wrong), getResources().getString(R.string.try_again_message), null, "Aceptar", "NEWACTION", true, false );
            }
        }
        else if (pError instanceof NetworkError)
        {
            progressDialog.dismiss();
            FullScreenDialog.CreateFullScreenDialog(getString(R.string.internet_connecttion_title),
                    getString(R.string.internet_connecttion_msg), null, null, "Aceptar", "NEWACTION", true, false );
        }
        else if(pError instanceof AuthFailureError)
        {
            progressDialog.dismiss();
            FullScreenDialog.CreateFullScreenDialog(getString(R.string.expired_session),
                    getString(R.string.dialog_error_topup_content), null, null, "Aceptar", "LOGOUT", true, false );
        }
    }

    /*
    *
    *   OTROS MÉTODOS
    *
    */
    public void InitializeValidation()
    {

        /*     CURRENT PASSWORD    */
        etCurrentPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etCurrentPass.setTransformationMethod(new PasswordTransformationMethod());
        etCurrentPass.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    Validator = new Validation(Perfil.this);
                    Validator.HasText(etCurrentPass);
                    //Validator.IsDifferentPassword(etNewPass, etCurrentPass);
                }
            }
        });
        etCurrentPass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                Validator = new Validation(Perfil.this);
                //Validator.IsDifferentPassword(etNewPass, etCurrentPass);
            }
        });

        /*     NEW PASSWORD    */
        etNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNewPass.setTransformationMethod(new PasswordTransformationMethod());
        etNewPass.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    Validator = new Validation(Perfil.this);
                    Validator.HasText(etNewPass);
                    Validator.IsDifferentPassword(etNewPass, etCurrentPass);
                }
            }
        });
        etNewPass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                Validator = new Validation(Perfil.this);
                Validator.IsDifferentPassword(etNewPass, etCurrentPass);
            }
        });

        /*     CONFIRM NEW PASSWORD    */
        etConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirmPass.setTransformationMethod(new PasswordTransformationMethod());
        etConfirmPass.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    Validator = new Validation(Perfil.this);
                    Validator.HasText(etConfirmPass);
                    Validator.PasswordsMatch(etConfirmPass, etNewPass);
                }
            }
        });
        etConfirmPass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                Validator = new Validation(Perfil.this);
                Validator.PasswordsMatch(etConfirmPass, etNewPass);
            }
        });
    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        Validator = new Validation(Perfil.this);

        if (!Validator.HasText(etCurrentPass))
        {
            ret = false;
        }
        if (!Validator.HasText(etNewPass))
        {
            ret = false;
        }
        if (!Validator.HasText(etConfirmPass))
        {
            ret = false;
        }

        if (!Validator.PasswordsMatch(etNewPass, etConfirmPass))
        {
            ret = false;
        }

        return ret;
    }

    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    public void ClearEditTexts()
    {
        etNewPass.setText("");
        etConfirmPass.setText("");
        etCurrentPass.setText("");

    }

    private boolean CheckConnection()
    {
        boolean connected;

        if (HaveNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = "No esta conectado a internet.";
            Toast.makeText(Perfil.this, connectionMessage, Toast.LENGTH_LONG).show();
        } else
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


}
