package com.globalpaysolutions.yovendosaldo.customs;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.android.yovendosaldo.R;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Geovanni on 07/04/2016.
 */
public class Validation
{
    Context ValidationContext;

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{4}-\\d{4}";
    private static final String NAME_REGEX = "^[\\p{L} .'-]+$";
    private static final String USERNAME_REGEX = "^[_A-Za-z0-9-\\+]{3,15}$";
    private static final String AMOUNT_REGEX = "[0-9]+([,.][0-9]{1,2})?";
    private static final String VOUCHER_REGEX = "\\d+$";

    private static String REQUIRED_MSG = "Requerido";
    private static String EMAIL_MSG = "Email no v�lido";
    private static String PHONE_MSG = "5555-5555";
    private static String NAME_MSG = "Debe escribir un nombre v�lido";
    private static String USERNAME_MSG = "Nombre de usuario no v�lido";
    private static String AMOUNT_MSG = "No es un monto valido";
    private static String VOUCHER_MSG = "Comprobante no valido";


    public Validation(Context context)
    {
        this.ValidationContext = context;
        REQUIRED_MSG = ValidationContext.getResources().getString(R.string.validation_required);
        EMAIL_MSG = ValidationContext.getResources().getString(R.string.validation_not_valid_email);
        NAME_MSG = ValidationContext.getResources().getString(R.string.validation_no_valid_name);
        USERNAME_MSG = "Nombre de usuario no v�lido";
        PHONE_MSG = ValidationContext.getResources().getString(R.string.validation_not_valid_phone);
        AMOUNT_MSG = ValidationContext.getResources().getString(R.string.validation_not_valid_amount);
        VOUCHER_MSG = ValidationContext.getResources().getString(R.string.validation_not_valid_voucher);
    }

    // call this method when you need to check email validation
    public boolean IsEmailAddress(EditText editText, boolean required)
    {
        return IsValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    public boolean IsValidUsername(EditText editText, boolean required)
    {
        return IsValid(editText, USERNAME_REGEX, USERNAME_MSG, required);
    }

    //call this method to validate full name
    public boolean IsValidName(EditText editText, boolean required)
    {
        return IsValid(editText, NAME_REGEX, NAME_MSG, required);
    }

    // call this method when you need to check phone number validation
    public boolean IsPhoneNumber(EditText editText, boolean required)
    {
        return IsValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }

    public boolean PasswordsMatch(EditText pEditText1, EditText pEditText2)
    {
        boolean Valid = false;

        if(pEditText1.getText().toString().equals( pEditText2.getText().toString()))
        {
            Valid = true;
            pEditText2.setError(null);
        }
        else
        {
            pEditText1.setError(ValidationContext.getString(R.string.password_not_match));
        }

        return Valid;
    }

    public boolean PinCodesMatch(EditText pEditText1, EditText pEditText2)
    {
        boolean Valid = false;

        if(pEditText1.getText().toString().equals( pEditText2.getText().toString()))
        {
            Valid = true;
            pEditText2.setError(null);
        }
        else
        {
            Toast.makeText(ValidationContext, ValidationContext.getString(R.string.pin_not_match), Toast.LENGTH_LONG).show();
        }

        return Valid;
    }

    public boolean IsDifferentPassword(EditText pEditText1, EditText pEditText2)
    {
        boolean Valid = false;

        if(pEditText1.getText().toString().equals( pEditText2.getText().toString()))
        {
            pEditText1.setError(ValidationContext.getString(R.string.diff_password_required));
        }
        else
        {
            Valid = true;
            pEditText1.setError(null);
        }

        return Valid;
    }

    public boolean IsValidAmount(EditText editText, boolean required)
    {
        return IsValid(editText, AMOUNT_REGEX, AMOUNT_MSG, required);
    }

    public boolean IsValidVoucher(EditText editText, boolean required)
    {
        return IsValid(editText, VOUCHER_REGEX, VOUCHER_MSG, required);
    }

    public boolean IsValidMinLength(EditText editText, int pMinLength)
    {
        boolean valid = false;

        editText.setError(null);

        String LengthText = editText.getText().toString();
        if(LengthText.length() == pMinLength)
        {
            valid = true;
        }
        else
        {
            String strError = String.format(ValidationContext.getResources().getString(R.string.validation_not_valid_minimum), pMinLength);
            editText.setError(strError);
        }

        return valid;

    }

    public boolean IsValidDepositDate(Date pDepositDate)
    {
        boolean valid = false;

        if(pDepositDate != null)
        {
            if(pDepositDate.getTime() > new Date().getTime())
            {
                valid = false;
                Toast.makeText(ValidationContext, ValidationContext.getString(R.string.deposit_date_invalid), Toast.LENGTH_LONG).show();
            }
            else
            {
                valid = true;
            }
        }

        return valid;
    }

    // return true if the input field is valid, based on the parameter passed
    public boolean IsValid(EditText editText, String regex, String errMsg, boolean required)
    {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // Si el texto es requerido pero el EditText est� vac�o
        if (required && !HasText(editText))
            return false;

        // Si el patr�n no hace match devuelve false
        if (required && !Pattern.matches(regex, text))
        {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // Revisa si el campo est� vac�o
    // Retorna true si el campo tiene texto, si no false
    public boolean HasText(EditText editText)
    {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // Length 0 significa que no hay texto
        if (text.length() == 0)
        {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}
