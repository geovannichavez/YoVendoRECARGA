package com.globalpaysolutions.yovendorecarga.customs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.globalpaysolutions.yovendorecarga.Login;
import com.globalpaysolutions.yovendorecarga.PIN;

import java.util.HashMap;

/**
 * Created by Geovanni on 05/04/2016.
 */
public class SessionManager
{
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    /*  PREFERENCIAS DEL PIN    */
    SharedPreferences pinPreferences;
    Editor pinEditor;

    /*  PREFERENCIAS DE NOTIFICACIONES  */
    SharedPreferences NotificationsSettings;
    Editor NotificationsEditor;


    public static final String PREF_NAME = "yvsPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_BALANCE = "availableBalance";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_TOKEN = "userToken";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_REMEMBER_EMAIL = "rememberEmail";
    public static final String KEY_PPW = "userOtherWayConffirmation";
    public static final String KEY_SESSION_ID = "sessionID";
    public static final String KEY_VENDOR_M = "vendorM";
    public static final String KEY_COUNTRY_ID = "countryId";
    public static final String KEY_ISO3_CODE = "iso3Code";
    public static final String KEY_PHONE_CODE = "PhoneCode";

    public static final String KEY_ACTIVATE_PIN = "securityPin";
    public static final String KEY_PIN_CODE = "pinCode";

    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        //Obtiene las preferencias guardadas en las Preferences
        pinPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        pinEditor = pinPreferences.edit();

        NotificationsSettings = _context.getSharedPreferences("NotificationsHubSettings", PRIVATE_MODE);
        NotificationsEditor = NotificationsSettings.edit();
    }

    /*
    *
    *   SALDO DISPONIBLE
    *
    */
    public void SaveAvailableBalance(String pBalance)
    {
        editor.putString(KEY_BALANCE, pBalance);
        editor.commit();
    }

    public HashMap<String, String> GetAvailableBalance()
    {
        HashMap<String, String> Balance = new HashMap<String, String>();
        Balance.put(KEY_BALANCE, pref.getString(KEY_BALANCE, "0.00"));
        return Balance;
    }

    /*
    *
    *   EMAIL DE USUARIO
    *
    */
    public void RememberEmail(boolean pRemember)
    {
        editor.putBoolean(KEY_REMEMBER_EMAIL, pRemember);
        editor.commit();
    }

    public HashMap<String, String> GetUserEmail()
    {
        HashMap<String, String> Email = new HashMap<String, String>();
        Email.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, ""));
        return Email;
    }

    //Guarda los datos de la sesión
    public void CreateLoginSession(String pEmail, String pToken, String pBalance, String pPww, int pSessionID, boolean pVendorM,
                                   String pCountryID, String pISO3code, String pPhoneCode)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_EMAIL, pEmail);
        editor.putString(KEY_TOKEN, pToken);
        editor.putString(KEY_BALANCE, pBalance);
        editor.putString(KEY_PPW, pPww);
        editor.putInt(KEY_SESSION_ID, pSessionID);
        editor.putBoolean(KEY_VENDOR_M, pVendorM);
        editor.putString(KEY_COUNTRY_ID, pCountryID);
        editor.putString(KEY_ISO3_CODE, pISO3code);
        editor.putString(KEY_PHONE_CODE, pPhoneCode);

        editor.commit();
    }

    //Guarda el nombre y apellido del usuario
    public void SaveUserProfile(String pFirstname, String pLastname)
    {
        editor.putString(KEY_FIRST_NAME, pFirstname);
        editor.putString(KEY_LAST_NAME, pLastname);
        editor.commit();
    }

    public void UpdateUserSessionInfo(String pToken, String pBalance)
    {
        editor.putString(KEY_TOKEN, pToken);
        editor.putString(KEY_BALANCE, pBalance);
        editor.apply();
    }

    public void UpdateSavedToken(String pToken)
    {
        editor.putString(KEY_TOKEN, pToken);
        editor.apply();
    }

    public HashMap<String, String> GetUserSessionInfo()
    {
        HashMap<String, String> User = new HashMap<String, String>();
        User.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, ""));
        User.put(KEY_BALANCE, pref.getString(KEY_BALANCE, "0.00"));
        User.put(KEY_TOKEN, pref.getString(KEY_TOKEN, ""));

        return User;
    }

    //Obtiene la información del perfil del usuario
    public HashMap<String, String> GetUserProfile()
    {
        HashMap<String, String> Profile = new HashMap<String, String>();
        Profile.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, ""));
        Profile.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, ""));
        return Profile;
    }

    public HashMap<String, String> GetSavedToken()
    {
        HashMap<String, String> UserToken = new HashMap<String, String>();
        UserToken.put(KEY_TOKEN, pref.getString(KEY_TOKEN, ""));
        return UserToken;
    }

    public HashMap<String, Integer> RetrieveSessionID()
    {
        HashMap<String, Integer> SessionID = new HashMap<String, Integer>();
        SessionID.put(KEY_SESSION_ID, pref.getInt(KEY_SESSION_ID, 0));
        return SessionID;
    }

    public HashMap<String, Boolean> GetVendorInfo()
    {
        HashMap<String, Boolean> VendorInfo = new HashMap<String, Boolean>();
        VendorInfo.put(KEY_VENDOR_M, pref.getBoolean(KEY_VENDOR_M, false));
        return VendorInfo;
    }

    public HashMap<String, String> GetUserISO3Code()
    {
        HashMap<String, String> userIso3Code = new HashMap<String, String>();
        userIso3Code.put(KEY_ISO3_CODE, pref.getString(KEY_ISO3_CODE, ""));
        return userIso3Code;
    }

    public HashMap<String, String> GetCountryPhoneCode()
    {
        HashMap<String, String> countryPhoneCode = new HashMap<String, String>();
        countryPhoneCode.put(KEY_PHONE_CODE, pref.getString(KEY_PHONE_CODE, ""));
        return countryPhoneCode;
    }

    public HashMap<String, String> GetCountryID()
    {
        HashMap<String, String> countryID = new HashMap<String, String>();
        countryID.put(KEY_COUNTRY_ID, pref.getString(KEY_COUNTRY_ID, ""));
        return countryID;
    }

    public void ClearUserSession()
    {
        editor.clear();
        editor.commit();
        /*pref.edit().remove("KEY_USER_EMAIL").apply();
        pref.edit().remove("KEY_TOKEN").apply();
        pref.edit().remove("KEY_BALANCE").apply();*/
    }

    public void CheckLogin()
    {
        if (!this.IsUserLoggedIn())
        {
            Intent i = new Intent(_context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            ((Activity)_context).finish();
        }
    }

    public boolean IsUserLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void LogoutUser()
    {
        /*
        BORRA TODOS LOS DATOS DE SHARED PREFERENCES
        editor.clear();
        editor.apply();*/

        Data.SignOut(_context);

        editor.remove(KEY_BALANCE);
        //editor.remove(KEY_TOKEN);
        editor.remove(KEY_FIRST_NAME);
        editor.remove(KEY_LAST_NAME);
        editor.remove(IS_LOGIN);
        editor.remove(KEY_SESSION_ID);
        editor.remove(KEY_VENDOR_M);
        editor.remove(KEY_COUNTRY_ID);
        editor.remove(KEY_ISO3_CODE);
        editor.remove(KEY_PHONE_CODE);
        editor.remove("RanBefore");
        pinEditor.remove("KEY_ACTIVATE_PIN");
        editor.apply();

        NotificationsEditor.remove("registrationID");
        NotificationsEditor.apply();


        editor = pref.edit();
        editor.putBoolean("RanBefore", true);
        editor.apply();


        /*Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
        ((Activity)_context).finish();*/
    }

    public boolean IsFirstTime()
    {

        boolean RanBefore = pref.getBoolean("RanBefore", false);
        if (!RanBefore)
        {
            if(IsUserLoggedIn())
            {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("RanBefore", true);
                editor.commit();
            }
        }
        return !RanBefore;
    }

    public boolean MustRememeberEmail()
    {
        boolean MustRemember = pref.getBoolean(KEY_REMEMBER_EMAIL, false);
        return  MustRemember;
    }

    /*
    *
    *   PIN DE SEGURIDAD
    *
    */


    public boolean IsSecurityPinActive()
    {
        SharedPreferences pinPreferences;
        pinPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        boolean pinActive = pinPreferences.getBoolean("KEY_ACTIVATE_PIN", false);

        return  pinActive;
    }

    public void setPinActive(boolean pValue)
    {
        pinEditor.putBoolean("KEY_ACTIVATE_PIN", pValue);
        pinEditor.apply();
    }

    public void AskForPIN()
    {
        if (this.IsSecurityPinActive())
        {
            /*Intent i = new Intent(_context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            ((Activity)_context).finish();*/

            Intent askPin = new Intent(_context, PIN.class);
            _context.startActivity(askPin);
        }
    }

    public void SetSecurityPin(String pCode)
    {
        pinEditor.putString(KEY_PIN_CODE, pCode);
        pinEditor.commit();
    }

    public HashMap<String, String> GetSecurityPin()
    {
        HashMap<String, String> UserPin = new HashMap<String, String>();
        UserPin.put(KEY_PIN_CODE, pinPreferences.getString(KEY_PIN_CODE, ""));
        return UserPin;
    }

    public void DeteleSecurityPIN()
    {
        pinEditor.remove(KEY_PIN_CODE);
        pinEditor.apply();
    }

    public boolean ValidEnteredPIN(String pPIN)
    {
        boolean Valid = false;
        String SavedPIN = pinPreferences.getString(KEY_PIN_CODE, "");
        if(SavedPIN.equals(pPIN))
        {
            Valid = true;
        }

        return Valid;
    }

    public boolean ValidPww(String pPww)
    {
        boolean Valid = false;
        String SavedPww = pref.getString(KEY_PPW, "");
        if(SavedPww.equals(pPww))
        {
            Valid = true;
        }

        return Valid;
    }

    public void DeleteSavedToken()
    {
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

}
