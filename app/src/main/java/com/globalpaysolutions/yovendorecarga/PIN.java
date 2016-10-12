package com.globalpaysolutions.yovendorecarga;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.Data;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.Validation;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class PIN extends AppCompatActivity
{
    //Objetos y otras clases
    SessionManager sessionManager;
    Validation Validator;

    //Vistas y Layout
    Toolbar toolbar;
    EditText etPincode;
    EditText etConfirmPincode;
    TextView tvPinDialogTitle;
    TextView tvContentPin;
    EditText etPin;
    EditText etEnterPww;

    //Variables globales
    String PinSecurityCode;
    boolean FirstTimeConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        String IntentExtra = getIntent().getStringExtra("PIN_CONF");

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras != null)
            {
                FirstTimeConfiguration= extras.getBoolean("FIRST_TIME_CONFIGURATION");
            }
        }
        else
        {
            FirstTimeConfiguration= (boolean) savedInstanceState.getSerializable("FIRST_TIME_CONFIGURATION");
        }

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarPin);
        toolbar.setTitle(getString(R.string.title_activity_menu_pin));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        tvContentPin = (TextView) findViewById(R.id.tvContentPin);

        if (IntentExtra.equals("CHANGE_PIN"))
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tvContentPin.setText(getString(R.string.content_change_pin));
        }
        else if(IntentExtra.equals("SET_FIRST_TIME"))
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            tvContentPin.setText(getString(R.string.content_insert_pin));
            FirstTimeConfiguration = true;
        }
        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tvContentPin.setText(getString(R.string.content_insert_pin));
        }


        sessionManager = new SessionManager(this);
        Validator = new Validation(this);
        InitialiceEditTexts();

        if(!RetrieveUserPin().isEmpty())
        {
            if (sessionManager.IsSecurityPinActive())
            {
                //AskPINDialog();
                AskPasswordDialog();
            }
        }
    }

    public void SetPinCode(View view)
    {
        if (CheckValidation())
        {
            PinSecurityCode = etPincode.getText().toString().trim();
            sessionManager.SetSecurityPin(PinSecurityCode);

            if(FirstTimeConfiguration)
            {
                Intent navigateHome = new Intent(this, Home.class);
                sessionManager.setPinActive(true);

                navigateHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                navigateHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                navigateHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                navigateHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                navigateHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Borra el stack completo de navegación:
                navigateHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);


                startActivity(navigateHome);
                finish();
            }
            else
            {
                finish();
            }
        }
    }

    public AlertDialog AskPINDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PIN.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.custom_pin_dialog, null);

        builder.setView(v);

        Button btnAccept = (Button) v.findViewById(R.id.btnAccept);
        final TextView tvContentPin = (TextView) v.findViewById(R.id.tvContentPin);

        //EditText donde se va a insertar el PIN
        etPin = (EditText) v.findViewById(R.id.etEnterPin);
        etPin.setTransformationMethod(new PasswordTransformationMethod());
        etPin.setTypeface(Typeface.DEFAULT);
        PinTextCounter();

        final AlertDialog PinDialog = builder.show();

        btnAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String strPIN = etPin.getText().toString().trim();
                if (sessionManager.ValidEnteredPIN(strPIN))
                {
                    PinDialog.dismiss();
                } else
                {
                    etPin.setText("");
                    GenerateIncorrectPINText(tvContentPin);
                }
            }
        });

        PinDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                PIN.this.finish();
            }
        });

        return builder.create();
    }


    public AlertDialog AskPasswordDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PIN.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.custom_pin_dialog, null);

        builder.setView(v);

        Button btnAccept = (Button) v.findViewById(R.id.btnAccept);
        final TextView tvContentPin = (TextView) v.findViewById(R.id.tvContentPin);

        tvPinDialogTitle = (TextView) v.findViewById(R.id.tvPinDialogTitle);
        tvPinDialogTitle.setText(getString(R.string.title_insert_pwd));
        tvContentPin.setText(getString(R.string.content_insert_pwd));

        etPin = (EditText) v.findViewById(R.id.etEnterPin);
        etPin.setVisibility(View.GONE);

        //EditText donde se va a insertar la password
        etEnterPww = (EditText) v.findViewById(R.id.etEnterPww);
        etEnterPww.setTypeface(Typeface.DEFAULT);
        etEnterPww.setVisibility(View.VISIBLE);
        etEnterPww.setHint(getString(R.string.title_insert_pwd));



        final AlertDialog PinDialog = builder.show();

        btnAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String strPwwd = etEnterPww.getText().toString().trim();
                if (sessionManager.ValidPww(strPwwd))
                {
                    Data.IntentCounter = 0;
                    PinDialog.dismiss();
                }
                else
                {
                    if(Data.IntentCounter < 3)
                    {
                        etEnterPww.setText("");
                        GenerateIncorrectPasswText(tvContentPin);
                        Data.IntentCounter = Data.IntentCounter +1;
                    }
                    else
                    {
                        Data.IntentCounter = 0;
                        sessionManager.LogoutUser();
                    }

                }
            }
        });

        PinDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                PIN.this.finish();
            }
        });

        return builder.create();
    }



    /*
    *
    *
    *   OTROS MÉTODOS
    *
    */

    public void GenerateIncorrectPINText(TextView pTextView)
    {
        pTextView.setText(getString(R.string.incorrect_pin_try_again));
    }

    public void GenerateIncorrectPasswText(TextView pTextView)
    {
        pTextView.setText(getString(R.string.incorrect_pww_try_again));
    }

    public void InitialiceEditTexts()
    {
        etPincode = (EditText) findViewById(R.id.etPincode);
        etConfirmPincode = (EditText) findViewById(R.id.etConfirmPincode);
        etPincode.setTransformationMethod(new PasswordTransformationMethod());
        etPincode.setTypeface(Typeface.DEFAULT);
        etConfirmPincode.setTransformationMethod(new PasswordTransformationMethod());
        etConfirmPincode.setTypeface(Typeface.DEFAULT);

        etPincode.addTextChangedListener(new TextWatcher()
        {
            int TextLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                String str = etPincode.getText().toString();
                TextLength = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String PinText = etPincode.getText().toString();

                //Esconde el teclado después que el EditText alcanzó los 4 dígitos
                if (PinText.length() == 4 && TextLength < PinText.length())
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });


        /*
        *     PIN CODE CONFFIRMATION
        */
        etConfirmPincode.addTextChangedListener(new TextWatcher()
        {
            int TextLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                String str = etConfirmPincode.getText().toString();
                TextLength = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String PinText = etConfirmPincode.getText().toString();

                //Esconde el teclado después que el EditText alcanzó los 4 dígitos
                if (PinText.length() == 4 && TextLength < PinText.length())
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
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

    public void PinTextCounter()
    {
        etPin.addTextChangedListener(new TextWatcher()
        {
            int TextLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                String str = etPin.getText().toString();
                TextLength = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String PinText = etPin.getText().toString();

                //Esconde el teclado después que el EditText alcanzó los 4 dígitos
                if (PinText.length() == 4 && TextLength < PinText.length())
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        if (!Validator.HasText(etConfirmPincode))
        {
            ret = false;
        }
        if (!Validator.HasText(etPincode))
        {
            ret = false;
        }

        if (!Validator.PinCodesMatch(etConfirmPincode, etPincode))
        {
            ret = false;
        }

        return ret;
    }

    public void deletePin(View view)
    {
        sessionManager.DeteleSecurityPIN();
        Toast.makeText(this, "PIN borrado", Toast.LENGTH_LONG).show();
    }
}
