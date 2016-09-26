package com.globalpaysolutions.yovendorecarga.customs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.yovendosaldo.R;

/**
 * Created by Josu� Ch�vez on 20/05/2016.
 */
public class PinDialogBuilder extends AlertDialog
{
    Context _context;
    EditText etPin;
    SessionManager sessionManager;
    boolean Allow = false;
    public Button btnAccept;
    String Phone;
    public String strPIN;

    public PinDialogBuilder(Context pContext, CustomOnClickListener pClick, String pPhone)
    {
        super(pContext);
        this._context = pContext;
        this.ClickListener = pClick;
        this.Phone = pPhone;
    }

    public CustomOnClickListener ClickListener;

    public interface CustomOnClickListener
    {
        void onAcceptClick();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        sessionManager = new SessionManager(_context);
        setContentView(R.layout.custom_pin_dialog);


        btnAccept = (Button) findViewById(R.id.btnAccept);
        final TextView tvContentPin = (TextView) findViewById(R.id.tvContentPin);
        TextView tvPinPhonenumber = (TextView) findViewById(R.id.tvPinPhonenumber);
        TextView tvContentPinTopup = (TextView) findViewById(R.id.tvContentPinTopup);

        //Anuncio de que se har� una recargar
        tvContentPinTopup.setVisibility(View.VISIBLE);

        //N�mero al que se har� la recarga
        tvPinPhonenumber.setVisibility(View.VISIBLE);
        tvPinPhonenumber.setText(Phone);


        //EditText donde se va a insertar el PIN
        etPin = (EditText) findViewById(R.id.etEnterPin);
        etPin.setTransformationMethod(new PasswordTransformationMethod());
        etPin.setTypeface(Typeface.DEFAULT);
        etPin.requestFocus();
        PinTextCounter();


        btnAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                strPIN = etPin.getText().toString().trim();
                //this.dismiss();
                ClickListener.onAcceptClick();
            }


        });

        /*this.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                ((Activity) _context).finish();
            }
        });*/


    }

    /*public AlertDialog AskPINDialog(boolean pFromTopup, String pPhone)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        sessionManager = new SessionManager(_context);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_pin_dialog, null);

        builder.setView(v);

        btnAccept = (Button) v.findViewById(R.id.btnAccept);
        final TextView tvContentPin = (TextView) v.findViewById(R.id.tvContentPin);
        TextView tvPinPhonenumber = (TextView) v.findViewById(R.id.tvPinPhonenumber);
        TextView tvContentPinTopup = (TextView) v.findViewById(R.id.tvContentPinTopup);

        //Anuncio de que se har� una recargar
        tvContentPinTopup.setVisibility(View.VISIBLE);

        //N�mero al que se har� la recarga
        tvPinPhonenumber.setVisibility(View.VISIBLE);
        tvPinPhonenumber.setText(pPhone);


        //EditText donde se va a insertar el PIN
        etPin = (EditText) v.findViewById(R.id.etEnterPin);
        etPin.setTransformationMethod(new PasswordTransformationMethod());
        etPin.setTypeface(Typeface.DEFAULT);
        PinTextCounter();

        final AlertDialog PinDialog = builder.show();

        btnAccept.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClickListener.onAcceptClick();

                *//*String strPIN = etPin.getText().toString().trim();
                if (sessionManager.ValidEnteredPIN(strPIN))
                {
                    PinDialog.dismiss();
                }
                else
                {
                    Allow = false;
                    etPin.setText("");
                    GenerateIncorrectPINText(tvContentPin, _context);
                }*//*
            }
        });

        PinDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                ((Activity) _context).finish();
            }
        });

        PinDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {

            }
        });

        return builder.create();
    }*/

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

                //Esconde el teclado despu�s que el EditText alcanz� los 4 d�gitos
                if (PinText.length() == 4 && TextLength < PinText.length())
                {
                    InputMethodManager imm = (InputMethodManager) _context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
    }

    public void GenerateIncorrectPINText()
    {
        etPin.setText("");
        TextView tvContentPin = (TextView) findViewById(R.id.tvContentPin);
        tvContentPin.setText(_context.getString(R.string.incorrect_pin_try_again));
    }

    public boolean AllowTopup()
    {
        return Allow;
    }
}
