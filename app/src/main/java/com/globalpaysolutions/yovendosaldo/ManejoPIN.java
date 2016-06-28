package com.globalpaysolutions.yovendosaldo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.SwitchPreferenceCompat;
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

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.customs.Data;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class ManejoPIN extends AppCompatActivity
{
    Toolbar toolbar;
    public static SessionManager sessionManager;
    static SwitchPreference codeActive;
    static EditText etPin;
    static EditText etEnterPww;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manejo_pin);

        toolbar = (Toolbar) findViewById(R.id.toolbarManejoPIN);
        toolbar.setTitle(getString(R.string.title_activity_menu_pin));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this);

        SetConfigurationFragment();
    }

    public static class ManejoPINFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_manejo_pin);


            //codeActive = (SwitchPreferenceCompat) findPreference ("KEY_ACTIVATE_PIN");
            codeActive = (SwitchPreference) findPreference("KEY_ACTIVATE_PIN");

            if (codeActive != null)
            {
                if (RetrieveUserPin().isEmpty())
                {
                    codeActive.setChecked(false);
                    sessionManager.setPinActive(false);
                }

                codeActive.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue)
                    {
                        boolean commitChange = true;
                        boolean newPinValue = (Boolean) newValue;
                        boolean isPinActive = sessionManager.IsSecurityPinActive();
                        String SavedPin = RetrieveUserPin();

                        if (SavedPin.isEmpty())
                        {
                            if (newPinValue) //Si newPinValue es true, entonces se debe establecer
                            {
                                Intent setPin = new Intent(getActivity(), PIN.class);
                                setPin.putExtra("PIN_CONF", "SET_PIN");
                                getActivity().startActivity(setPin);
                            } else
                            {
                                sessionManager.setPinActive(newPinValue);
                            }
                        }
                        else
                        {
                            if (isPinActive)
                            {
                                /*
                                    Debe preguntar el Pin para poder quitarlo,
                                    debe aparecer la pantalla del pin
                                */
                                commitChange = false;
                                //AskPINDialog(getActivity(), newPinValue, codeActive); //Debe desactivar el Pin
                                AskPwdDialog(getActivity(), newPinValue, codeActive);
                            }
                            else
                            {
                                sessionManager.setPinActive(newPinValue); //Aquí lo va a activar
                            }
                        }

                        return commitChange;
                    }
                });
            }
        }


    }

    public void SetConfigurationFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ManejoPINFragment fragment = new ManejoPINFragment();
        fragmentTransaction.add(R.id.frameManejoPIN, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (RetrieveUserPin().isEmpty())
        {
            codeActive.setChecked(false);
            sessionManager.setPinActive(false);
        }
    }

    public static AlertDialog AskPINDialog(final Context _context, final boolean newValue, final SwitchPreference pCodePreference)
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(_context);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_pin_dialog, null);

        builder.setView(v);

        Button btnAccept = (Button) v.findViewById(R.id.btnAccept);
        final TextView tvContentPin = (TextView) v.findViewById(R.id.tvContentPin);

        //EditText donde se va a insertar el PIN
        etPin = (EditText) v.findViewById(R.id.etEnterPin);
        etPin.setTransformationMethod(new PasswordTransformationMethod());
        etPin.setTypeface(Typeface.DEFAULT);
        PinTextCounter(_context);

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
                    sessionManager.setPinActive(newValue);
                    pCodePreference.setChecked(false);
                } else
                {
                    etPin.setText("");
                    GenerateIncorrectPINText(tvContentPin, _context);
                    sessionManager.setPinActive(true);
                }
            }
        });

        PinDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                sessionManager.setPinActive(true);
                ((Activity) _context).finish();
            }
        });

        return builder.create();
    }

    public static AlertDialog AskPwdDialog(final Context _context, final boolean newValue, final SwitchPreference pCodePreference)
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(_context);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_pin_dialog, null);

        builder.setView(v);

        Button btnAccept = (Button) v.findViewById(R.id.btnAccept);
        final TextView tvContentPin = (TextView) v.findViewById(R.id.tvContentPin);

        final TextView tvPinDialogTitle = (TextView) v.findViewById(R.id.tvPinDialogTitle);
        tvPinDialogTitle.setText(_context.getString(R.string.title_insert_pwd));
        tvContentPin.setText(_context.getString(R.string.content_insert_pwd));

        etPin = (EditText) v.findViewById(R.id.etEnterPin);
        etPin.setVisibility(View.GONE);

        //EditText donde se va a insertar la password
        etEnterPww = (EditText) v.findViewById(R.id.etEnterPww);
        etEnterPww.setTypeface(Typeface.DEFAULT);
        etEnterPww.setVisibility(View.VISIBLE);
        etEnterPww.setHint(_context.getString(R.string.title_insert_pwd));

        final AlertDialog PinDialog = builder.show();

        btnAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String strPwd = etEnterPww.getText().toString().trim();
                if (sessionManager.ValidPww(strPwd))
                {
                    PinDialog.dismiss();
                    sessionManager.setPinActive(newValue);
                    pCodePreference.setChecked(false);
                    Data.IntentCounter = 0;
                }
                else
                {
                    if(Data.IntentCounter < 3)
                    {
                        etEnterPww.setText("");
                        GenerateIncorrectPasswText(tvContentPin, _context);
                        sessionManager.setPinActive(true);
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
                sessionManager.setPinActive(true);
                ((Activity) _context).finish();
            }
        });

        return builder.create();
    }

    public static void PinTextCounter(final Context _context)
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
                    InputMethodManager imm = (InputMethodManager) _context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
    }

    public static String RetrieveUserPin()
    {
        String securityPin = "";
        HashMap<String, String> MapToken = sessionManager.GetSecurityPin();
        securityPin = MapToken.get(SessionManager.KEY_PIN_CODE);

        if (!StringUtils.isNotBlank(securityPin))
        {
            securityPin = "";
        }

        return securityPin;
    }

    public static void GenerateIncorrectPINText(TextView pTextView, Context _context)
    {
        pTextView.setText(_context.getString(R.string.incorrect_pin_try_again));
    }

    public static void GenerateIncorrectPasswText(TextView pTextView, Context _context)
    {
        pTextView.setText(_context.getString(R.string.incorrect_pww_try_again));
    }

}

