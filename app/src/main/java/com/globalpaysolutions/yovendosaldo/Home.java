package com.globalpaysolutions.yovendosaldo;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.globalpaysolutions.yovendosaldo.adapters.AmountSpinnerAdapter;
import com.globalpaysolutions.yovendosaldo.adapters.OperatorsAdapter;
import com.globalpaysolutions.yovendosaldo.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendosaldo.customs.Data;
import com.globalpaysolutions.yovendosaldo.customs.PinDialogBuilder;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.Validation;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;
import com.globalpaysolutions.yovendosaldo.model.Amount;
import com.globalpaysolutions.yovendosaldo.model.Operator;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//For Push Notifications
import com.globalpaysolutions.yovendosaldo.notifications.NotificationSettings;
import com.globalpaysolutions.yovendosaldo.notifications.RegisterClient;
import com.globalpaysolutions.yovendosaldo.notifications.RegistrationIntentService;
import com.globalpaysolutions.yovendosaldo.notifications.YvsNotificationsHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.*;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class Home extends AppCompatActivity
{
    //Adapters y Layouts
    OperatorsAdapter OpeAdapter;
    AmountSpinnerAdapter AmountAdapter;
    GridView GridViewOperators;
    Spinner SpinnerAmount;
    RelativeLayout rlMainHomeContent;
    Toolbar toolbar;
    EditText txtPhoneNumber;
    TextView tvBalance;
    ProgressDialog ProgressDialog;
    Button btnTopup;
    SwipeRefreshLayout SwipeRefresh;
    ScrollView scrollView;
    LinearLayout lnrBalance;

    //Objetos para el Drawer
    private NavigationView navigationView;
    private NavigationView navigationFooter;
    private DrawerLayout drawerLayout;

    //Objetos globales para activity
    List<Operator> ListaOperadores = new ArrayList<Operator>();
    List<Amount> ListaMontos = new ArrayList<Amount>();
    List<Amount> selectedOperatorAmounts = new ArrayList<>();
    private static final String TAG = Home.class.getSimpleName();
    public static String Token;
    boolean OperatorSelected = false;
    String SelectedOperatorName;
    int AmountTopup;
    SessionManager sessionManager;
    Validation validator;
    private ShowcaseView showcaseView;
    private int ShowCaseCounter = 0;
    private boolean IsExecuting = false;
    CustomFullScreenDialog CustomDialogCreator;
    PinDialogBuilder PinDialogBuilder;
    public PinDialogBuilder.CustomOnClickListener ClickListener;
    String mNMO;

    //Push Notifications
    public static Home homeActivity;
    public static Boolean isVisible = false;
    private GoogleCloudMessaging gcm;
    private RegisterClient registerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        sessionManager = new SessionManager(Home.this);
        CustomDialogCreator = new CustomFullScreenDialog(Home.this, Home.this);


        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        tvBalance = (TextView) findViewById(R.id.tvAvailableBalance);
        txtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnTopup = (Button) findViewById(R.id.btnEnvar);
        scrollView = (ScrollView) findViewById(R.id.homeScrollView);
        lnrBalance = (LinearLayout) findViewById(R.id.rectangle);


        InitializeValidation();

        CheckLogin();
        RetrieveSavedToken();
        RetrieveAmounts();

        /*
        * *****************************
        *   NAVIGATION DRAWER
        * *****************************
        * */

        //Navigation Drawer
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationFooter = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        rlMainHomeContent = (RelativeLayout) findViewById(R.id.rlMainHomeContent);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId())
                {
                    case R.id.Home:
                        finish();
                        Intent i = new Intent(getApplication().getApplicationContext(), Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        return true;

                    case R.id.ValidarDeposito:
                        FragmentDepositoBancario fragmentDepositoBancario = new FragmentDepositoBancario();
                        android.support.v4.app.FragmentTransaction fragmentTransactionDepo = getSupportFragmentManager().beginTransaction();
                        rlMainHomeContent.setVisibility(View.GONE);
                        toolbar.setTitle("");
                        fragmentTransactionDepo.replace(R.id.frame, fragmentDepositoBancario);
                        fragmentTransactionDepo.commit();
                        return true;

                    case R.id.Historial:
                        FragmentHistorialVentas fragmentHistorial = new FragmentHistorialVentas();
                        android.support.v4.app.FragmentTransaction fragmentTransactionAApp = getSupportFragmentManager().beginTransaction();
                        rlMainHomeContent.setVisibility(View.GONE);
                        toolbar.setTitle("");
                        fragmentTransactionAApp.replace(R.id.frame, fragmentHistorial);
                        fragmentTransactionAApp.commit();
                        return true;
                    case R.id.Alertas:
                        Intent notif = new Intent(getApplication().getApplicationContext(), Notificaciones.class);
                        startActivity(notif);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        navigationFooter.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(false);

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId())
                {
                    case R.id.Configuracion:
                        Intent i = new Intent(getApplication().getApplicationContext(), Configuracion.class);
                        startActivity(i);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });
        navigationFooter.getMenu().findItem(R.id.Configuracion).setChecked(false);


        // Inicializando Drawer Layout y ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        {

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        /*
        * *****************************
        *   END NAVIGATION DRAWER
        * *****************************
        * */


        SetBalanceTextView();
        getOperators();
        //getLocalAmounts();

        if (sessionManager.IsFirstTime())
        {
            ExecuteShowcase();
        }

        /*
        *
        *   SWIPEREFRESH
        *
        */
        SwipeRefresh.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                GetUserBag(true);
            }
        });

        if (sessionManager.IsUserLoggedIn())
        {
            GetUserBag(false);
        }



        /*
        *
        *   SCROLL VIEW
        *   Detecta si el scroll view está a 0 en Y
        */

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener()
        {

            @Override
            public void onScrollChanged()
            {
                int scrollY = scrollView.getScrollY();
                if (scrollY == 0)
                    SwipeRefresh.setEnabled(true);
                else
                    SwipeRefresh.setEnabled(false);

            }
        });


        /*
        *
        *   BALANCE LAYOUT
        *
        */
        lnrBalance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SetHistoryOnFragment();
            }
        });


        /*
        *
        *   PUSH NOTIFICATIONS
        *
        */
        homeActivity = this;
        if (sessionManager.IsUserLoggedIn())
        {
            NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, YvsNotificationsHandler.class);
            gcm = GoogleCloudMessaging.getInstance(this);
            registerClient = new RegisterClient(this);

            if(CheckGooglePlayServices())
            {
                BeginDeviceRegistration();
            }
        }

    }


    public void RequestTopUp(View view)
    {
        EnableTopupButton(false);
        Log.i("Print click", "Para saber cuantas veces se ha hecho click en el botón.");

        //Esconderá el teclado al presionar el botón
        /*InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/

        final String PhoneNumber = txtPhoneNumber.getText().toString();


        if (!RetrieveUserPin().isEmpty())
        {
            if (sessionManager.IsSecurityPinActive())
            {

                //Construye el dialogo, sobreescribe el método del click
                //y lo muestra
                ClickListener = new PinDialogBuilder.CustomOnClickListener()
                {
                    @Override
                    public void onAcceptClick()
                    {
                        final String strPIN = PinDialogBuilder.strPIN;
                        if (sessionManager.ValidEnteredPIN(strPIN))
                        {
                            PinDialogBuilder.dismiss();
                            BeginTopup(PhoneNumber);
                            Data.IntentCounter = 0;
                        }
                        else
                        {
                            //Valida que los intentos no hayan sido más de 4
                            if(Data.IntentCounter < 3)
                            {
                                PinDialogBuilder.GenerateIncorrectPINText();
                                Data.IntentCounter = Data.IntentCounter +1;
                            }
                            else
                            {
                                Data.IntentCounter = 0;
                                sessionManager.LogoutUser();
                            }

                        }
                    }
                };
                PinDialogBuilder = new PinDialogBuilder(Home.this, ClickListener, PhoneNumber);

                //Muestra el teclado al aparecer el dialogo
                PinDialogBuilder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                PinDialogBuilder.show();
                EnableTopupButton(true);

            }
            else
            {
                BeginTopup(PhoneNumber);
            }
        }
        else
        {
            BeginTopup(PhoneNumber);
        }
    }

    public void BeginTopup(String PhoneNumber)
    {
        if (CheckConnection())
        {
            PhoneNumber = PhoneNumber.replace("-", "");

            if (CheckValidation())
            {
                String Amount = String.valueOf(AmountTopup);
                ProgressDialog = new ProgressDialog(Home.this);
                ProgressDialog.setMessage("Enviando Recarga...");
                ProgressDialog.show();
                ProgressDialog.setCancelable(false);
                ProgressDialog.setCanceledOnTouchOutside(false);

                //***   ALERTA!! CON ESTE METODO SE ENVIA               ***
                //***   LA RECARGA, TENER CUIDADO CON IMPLEMENTACION    ***
                TopUp(PhoneNumber, Amount);
            }
            else
            {
                EnableTopupButton(true);
            }
        }
    }


    public void TopUp(String pPhoneNumber, String pAmount)
    {
        synchronized (this)
        {
            if (IsExecuting)
                return;
        }
        IsExecuting = true;

        JSONObject jTopUp = new JSONObject();
        try
        {
            jTopUp.put("Operador", SelectedOperatorName);
            jTopUp.put("IdCountry", "69");
        } catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, StringsURL.TOPUP + pPhoneNumber + "/" + pAmount,
                //StringsURL.TEST_TOPUP_GATS_ERROR,
                jTopUp, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                //Procesar la respuesta del servidor
                Log.d("Mensaje JSON ", response.toString());
                //Esconde el Progress Dialog
                ProgressDialog.dismiss();
                ProcessTopupResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ProcessTopupVolleyError(error);
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
        }, 0);
    }

    /*
    * ***************************
    *   PROCESAMIENTO DE RESPUESTAS
    * ***************************
    */
    //Procesa Respuesta de TopUp
    public void ProcessTopupResponse(JSONObject pResponse)
    {
        final JSONObject topupResponse = pResponse;
        String PhoneUsed = txtPhoneNumber.getText().toString();
        String Operator = "";
        try
        {
            String Message = topupResponse.getString("message").toString();
            String Balance = topupResponse.getString("AvailableAmount").toString();
            Operator = topupResponse.has("mno") ? topupResponse.getString("mno") : "";

            sessionManager.SaveAvailableBalance(Balance);
            SetBalanceTextView();
            Log.d("Resultado: ", Message);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        CustomDialogCreator.CreateFullScreenDialog(getString(R.string.dialog_succeed_topoup_title),
                getString(R.string.dialog_succeed_topup_content),
                PhoneUsed, Operator, "Enviar Otra Recarga", "NEWACTION", false, true);

        //Resetea todos los controles
        IsExecuting = false;
        EnableTopupButton(true);
        ResetControls();


    }

    //Procesa Respuesta de TopUp con Error
    public void ProcessTopupVolleyError(VolleyError pError)
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
            String Titulo = getString(R.string.check_history_title);
            String Linea1 = getString(R.string.check_history_advice_ln1);
            String Linea2 = getString(R.string.check_history_advice_ln2);
            String Button = "OK";
            txtPhoneNumber.setText("");
            IsExecuting = false;
            ResetControls();

            EnableTopupButton(true);
            CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, Button, "NEWACTION", true, true);
        }
        else if (pError instanceof ServerError)
        {
            ProgressDialog.dismiss();

            //Token Inválido
            if (statusCode == 502)
            {
                String Titulo = getString(R.string.expired_session);
                String Linea1 = getString(R.string.dialog_error_topup_content);
                String Button = "OK";
                txtPhoneNumber.setText("");

                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "LOGOUT", true, true);
            }
            //Insuficiente Saldo
            else if (statusCode == 503)
            {
                String Titulo = getString(R.string.insufficent_balance_title);
                String Linea1 = getString(R.string.insufficent_balance_ln1);
                String Linea2 = getString(R.string.insufficent_balance_ln2);
                String Button = "OK";
                ResetControls();

                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, Button, "NAVIGATEHOME", true, true);
            }
            //Error General
            else
            {
                String Titulo = "ALGO HA SALIDO MAL...";
                String Linea1 = getString(R.string.something_went_wrong_try_again);
                String Button = "OK";
                ProgressDialog.dismiss();
                ResetControls();

                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true);
            }
        }
        else if (pError instanceof NetworkError)
        {
            ProgressDialog.dismiss();
            String Titulo = getString(R.string.internet_connecttion_title);
            String Linea1 = getString(R.string.internet_connecttion_msg);
            String Button = "OK";
            txtPhoneNumber.setText("");

            IsExecuting = false;
            EnableTopupButton(true);
            CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true);

        }
        else if (pError instanceof AuthFailureError)
        {
            ProgressDialog.dismiss();

            String Titulo = "ALGO HA SALIDO MAL...";
            String Linea1 = getString(R.string.something_went_wrong_try_again);
            String Button = "OK";
            ProgressDialog.dismiss();
            ResetControls();

            IsExecuting = false;
            EnableTopupButton(true);
            CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true);
        }
    }


    /*
    * ***************************
    *   LLENADO DE SPINNER Y GRIDVIEW
    * ***************************
    */
    public void getOperators()
    {
        //Setea el hint cuando no se ha seleccionado un operador
        //Se añade 2 veces porque pone el último como seleccionado
        for (int i = 0; i < 2; i++)
        {
            selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        }
        AmountAdapter = new AmountSpinnerAdapter(this, R.layout.custom_amount_spinner_item, R.id.tvAmount, selectedOperatorAmounts);
        AmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerAmount = (Spinner) this.findViewById(R.id.spMontoRecarga);
        SpinnerAmount.setAdapter(AmountAdapter);
        SpinnerAmount.setSelection(AmountAdapter.getCount());


        OperatorSelected = false;
        //Seteando el adapter de Operadores
        GridViewOperators = (GridView) findViewById(R.id.gvOperadores);
        OpeAdapter = new OperatorsAdapter(this, R.layout.custom_operator_gridview_item);
        GridViewOperators.setAdapter(OpeAdapter);

        GridViewOperators.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                OperatorSelected = true;
                SelectedOperatorName = ((Operator) parent.getItemAtPosition(position)).getOperatorName();
                for (int i = 0; i < GridViewOperators.getAdapter().getCount(); i++)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        GridViewOperators.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
                    }
                    else
                    {
                        GridViewOperators.getChildAt(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    GridViewOperators.getChildAt(position).setBackground(getResources().getDrawable(R.drawable.custom_rounded_corner_selected));
                }
                else
                {
                    GridViewOperators.getChildAt(position).setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_rounded_corner_selected));
                }

                switch (SelectedOperatorName)
                {
                    case "Tigo":
                        mNMO = "Tigo El Salvador";
                        break;
                    case "Claro":
                        mNMO = "Claro El Salvador";
                        break;
                    case "Movistar":
                        mNMO = "Movistar El Salvador";
                        break;
                    case "Digicel":
                        mNMO = "Digicel El Salvador";
                        break;
                    default:
                        mNMO = "";
                        break;
                }

                getServerAmounts(mNMO);

            }
        });



        Operator op1 = new Operator();
        op1.setOperatorName("Tigo");
        op1.setDescription("");
        op1.setLogo("");
        op1.setID(1);
        ListaOperadores.add(op1);

        Operator op2 = new Operator();
        op2.setOperatorName("Digicel");
        op2.setDescription("");
        op2.setLogo("");
        op2.setID(2);
        ListaOperadores.add(op2);

        Operator op3 = new Operator();
        op3.setOperatorName("Movistar");
        op3.setDescription("");
        op3.setLogo("");
        op3.setID(3);
        ListaOperadores.add(op3);

        Operator op4 = new Operator();
        op4.setOperatorName("Claro");
        op4.setDescription("");
        op4.setLogo("");
        op4.setID(4);
        ListaOperadores.add(op4);

        for (Operator item : ListaOperadores)
        {
            OpeAdapter.add(item);
        }

    }

    public void getLocalAmounts()
    {
        //PRODUCCION
        /*Amount am1 = new Amount();
        am1.setDisplay("$ 1");
        am1.setAmount(1);
        am1.setDecimal("00");
        am1.setAditionalText("");
        ListaMontos.add(am1);

        Amount am5 = new Amount();
        am5.setDisplay("$ 5");
        am5.setAmount(5);
        am5.setDecimal("00");
        am5.setAditionalText("");
        ListaMontos.add(am5);

        Amount Hint = new Amount();
        Hint.setDisplay(getString(R.string.spinner_hint));
        Hint.setAmount(0);
        Hint.setDecimal("");
        Hint.setAditionalText("");
        ListaMontos.add(Hint);*/

        //PRUEBAS
        Amount am1 = new Amount();
        am1.setDisplay("$ 1");
        am1.setAmount(10);
        am1.setDecimal("05");
        am1.setAditionalText("");
        ListaMontos.add(am1);

        Amount am2 = new Amount();
        am2.setDisplay("$ 2");
        am2.setAmount(10);
        am2.setDecimal("10");
        am2.setAditionalText("");
        ListaMontos.add(am2);

        Amount am3 = new Amount();
        am3.setDisplay("$ 3");
        am3.setAmount(10);
        am3.setDecimal("50");
        am3.setAditionalText("");
        ListaMontos.add(am3);

        Amount am5 = new Amount();
        am5.setDisplay("$ 5");
        am5.setAmount(10);
        am5.setDecimal("00");
        am5.setAditionalText("");
        ListaMontos.add(am5);

        Amount am7 = new Amount();
        am7.setDisplay("$ 7");
        am7.setAmount(10);
        am7.setDecimal("00");
        am7.setAditionalText("");
        ListaMontos.add(am7);

        Amount am10 = new Amount();
        am10.setDisplay("$ 10");
        am10.setAmount(10);
        am10.setDecimal("00");
        am10.setAditionalText("");
        ListaMontos.add(am10);

        Amount Intern = new Amount();
        Intern.setDisplay("$ 1");
        Intern.setAmount(10);
        Intern.setDecimal("00");
        Intern.setAditionalText(getString(R.string.demo_string_1));
        ListaMontos.add(Intern);

        Amount Paq = new Amount();
        Paq.setDisplay("$ 0");
        Paq.setAmount(10);
        Paq.setDecimal("99");
        Paq.setAditionalText(getString(R.string.demo_string_2));
        ListaMontos.add(Paq);

        Amount Hint = new Amount();
        Hint.setDisplay(getString(R.string.spinner_hint));
        Hint.setAmount(0);
        Hint.setDecimal("");
        Hint.setAditionalText("");
        ListaMontos.add(Hint);

        AmountAdapter = new AmountSpinnerAdapter(this, R.layout.custom_amount_spinner_item, R.id.tvAmount, ListaMontos);
        AmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpinnerAmount.setAdapter(AmountAdapter);
        SpinnerAmount.setSelection(AmountAdapter.getCount());
        SpinnerAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {

                AmountTopup = ((Amount) parentView.getItemAtPosition(position)).getAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }

        });

    }

    public void getServerAmounts(String pOperatorName)
    {
        selectedOperatorAmounts.clear();

        //Escenario: si no hay conexion a internet
        //y el usuario clickeo un operador, entonces
        //como la lista 'Data.Amounts' está vacía, no pone nada en el spinner
        //por eso cuando esté vacía la llenará con el Hint.
        if (!Data.Amounts.isEmpty())
        {
            for (Amount item : Data.Amounts)
            {
                //Valida que MNO no venga Null, para evitar NullPointerException
                if (item.getMNO() != null && !item.getMNO().isEmpty())
                {
                    if (item.getMNO().equals(pOperatorName))
                    {
                        selectedOperatorAmounts.add(item);
                    }
                }
            }
        }
        else
        {
            selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        }


        selectedOperatorAmounts.add(Data.AmountHint(Home.this));

        AmountAdapter = new AmountSpinnerAdapter(this, R.layout.custom_amount_spinner_item, R.id.tvAmount, selectedOperatorAmounts);
        AmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerAmount = (Spinner) this.findViewById(R.id.spMontoRecarga);
        SpinnerAmount.setAdapter(AmountAdapter);
        SpinnerAmount.setSelection(AmountAdapter.getCount());
        SpinnerAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                AmountTopup = ((Amount) parentView.getItemAtPosition(position)).getAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });
    }

    public void RetrieveAmounts()
    {
        if (Data.Amounts.isEmpty())
        {
            Log.i("Amounts", "Request para traer montos");
            Data.GetAmounts(Home.this);
        }
    }


    /*
    * ***************************
    *   METODOS DE MENU
    * ***************************
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            ProgressDialog = new ProgressDialog(Home.this);
            ProgressDialog.setMessage(getString(R.string.dialog_signing_out));
            ProgressDialog.show();
            ProgressDialog.setCancelable(false);
            ProgressDialog.setCanceledOnTouchOutside(false);

            sessionManager.LogoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (ProgressDialog != null && ProgressDialog.isShowing())
        {
            ProgressDialog.dismiss();
        }
        isVisible = false;
    }

    /*
    * ***********************
    * SWIPE LAYOUT
    * ***********************
    */

    public void GetUserBag(boolean isSwipe)
    {
        if (isSwipe)
        {
            SwipeRefresh.setRefreshing(true);
        }

        YVScomSingleton.getInstance(Home.this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, StringsURL.USERBAG, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                //SwipeRefresh.setRefreshing(false);
                HideSwipe();
                Log.d("Mensaje JSON ", response.toString());
                ProcessBagResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HideSwipe();
                ProcessBagErrorResponse(error);
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

    public void ProcessBagResponse(JSONObject pResponse)
    {
        String NewBalance = "0.00";
        try
        {
            JSONObject Bag = pResponse.getJSONObject("userBag");
            NewBalance = Bag.has("AvailableAmount") ? Bag.getString("AvailableAmount") : "";
        } catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        sessionManager.SaveAvailableBalance(NewBalance);
        SetBalanceTextView();
    }

    public void ProcessBagErrorResponse(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError)
        {
            Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
        }
        if (pError instanceof NoConnectionError)
        {
            Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
        }
        else if (pError instanceof ServerError)
        {
            if (statusCode == 502)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
                alertDialog.setTitle(getString(R.string.expired_session));
                alertDialog.setMessage(getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser();
                    }
                });
                alertDialog.show();
            }
            else
            {
                Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        }
        else if (pError instanceof NetworkError)
        {
            Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
        }
        else if (pError instanceof AuthFailureError)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle("ERROR");
            alertDialog.setMessage("Las credenciales son incorrectas");
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    sessionManager.LogoutUser();
                }
            });
            alertDialog.show();
        }
    }

    public void HideSwipe()
    {
        if (SwipeRefresh.isShown() && SwipeRefresh != null)
        {
            SwipeRefresh.setRefreshing(false);
        }
    }


    /*
    * ***************************
    *   OTROS METODOS
    * ***************************
    */

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

    private boolean CheckConnection()
    {
        boolean connected;

        if (HaveNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = "No esta conectado a internet.";
            Toast.makeText(getApplicationContext(), connectionMessage, Toast.LENGTH_LONG).show();
            EnableTopupButton(true);
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    public void SetBalanceTextView()
    {
        HashMap<String, String> Balance = sessionManager.GetAvailableBalance();
        String strBalance = Balance.get(SessionManager.KEY_BALANCE);

        double amount = Double.parseDouble(strBalance);
        DecimalFormat formatter = new DecimalFormat("#,###.##");

        tvBalance.setText(formatter.format(amount));
    }

    public void RetrieveSavedToken()
    {
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);
    }

    public void CheckLogin()
    {
        sessionManager = new SessionManager(Home.this);
        sessionManager.CheckLogin();
    }

    public void CheckPINSecurity()
    {
        sessionManager = new SessionManager(Home.this);
        sessionManager.AskForPIN();
    }

    public void InitializeValidation()
    {

        txtPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    validator = new Validation(Home.this);
                    validator.IsPhoneNumber(txtPhoneNumber, true);
                    validator.HasText(txtPhoneNumber);
                }
            }
        });
        txtPhoneNumber.addTextChangedListener(new TextWatcher()
        {

            int TextLength = 0;
            private static final char space = '-';

            @Override
            public void afterTextChanged(Editable text)
            {

                String NumberText = txtPhoneNumber.getText().toString();
               /* if (NumberText.length() == 4 && TextLength < NumberText.length() )
                {
                    txtPhoneNumber.append("-");
                }*/

                //Esconde el teclado después que el EditText alcanzó los 9 dígitos
                if (NumberText.length() == 9 && TextLength < NumberText.length())
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                // Remove spacing char
                if (text.length() > 0 && (text.length() % 5) == 0)
                {
                    final char c = text.charAt(text.length() - 1);
                    if (space == c)
                    {
                        text.delete(text.length() - 1, text.length());
                    }
                }
                // Insert char where needed.
                if (text.length() > 0 && (text.length() % 5) == 0)
                {
                    char c = text.charAt(text.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(text.toString(), String.valueOf(space)).length <= 3)
                    {
                        text.insert(text.length() - 1, String.valueOf(space));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                String str = txtPhoneNumber.getText().toString();
                TextLength = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
        });
    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        validator = new Validation(Home.this);

        if (!validator.IsPhoneNumber(txtPhoneNumber, true))
        {
            ret = false;
        }

        if (!OperatorSelected)
        {
            Toast.makeText(Home.this, getResources().getString(R.string.validation_required_operator), Toast.LENGTH_LONG).show();
            ret = false;
        }

        if (!ret)
        {
            EnableTopupButton(true);
        }

        if (AmountTopup == 0)
        {
            Toast.makeText(Home.this, getString(R.string.spinner_amount_validation), Toast.LENGTH_LONG).show();
            ret = false;
        }

        return ret;
    }

    /*private void CreateFullScreenDialog(String pTitle, String pMsgLine1, String pMsgLine2, String pMsgLine3, String pButton, String pAction, boolean pError, boolean pFromTopup)
    {
        Bundle bundle = new Bundle();
        String Title = pTitle;
        String Line1 = pMsgLine1;
        String Line2 = pMsgLine2;
        String Line3 = pMsgLine3;
        String Button = pButton;
        String Action = pAction;
        boolean Error = pError;
        boolean FromTopup = pFromTopup;


        bundle.putString("Title", Title);
        bundle.putString("Line1", Line1);
        bundle.putString("Line2", Line2);
        bundle.putString("Line3", Line3);
        bundle.putString("Button", Button);
        bundle.putString("Action", Action);
        bundle.putBoolean("Error", Error);
        bundle.putBoolean("FromTopup", FromTopup);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentCustomDialog CustomDialog = new FragmentCustomDialog();
        CustomDialog.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(android.R.id.content, CustomDialog, "FragmentCustomDialog").addToBackStack("tag").commit();

        txtPhoneNumber.setText("");

    }*/

    public void ExecuteShowcase()
    {
        final Target tgBalance = new ViewTarget(findViewById(R.id.rectangle));
        //final Target tgOperators = new ViewTarget(findViewById(R.id.gvOperadores));
        final Target tgPhone = new ViewTarget(findViewById(R.id.etPhoneNumber));
        final Target tgAmount = new ViewTarget(findViewById(R.id.spMontoRecarga));
        final Target tgButton = new ViewTarget(findViewById(R.id.btnEnvar));


        showcaseView = new ShowcaseView.Builder(Home.this).setTarget(tgBalance).setContentTitle(getString(R.string.sv_title_1)).setContentText(getString(R.string.sv_content_1)).setStyle(R.style.CustomShowcaseTheme2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (ShowCaseCounter)
                {
                    case 0:
                        Handler handler = new Handler();
                        showcaseView.hideButton();
                        showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(0).findViewById(R.id.ivOperador)), true);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(1).findViewById(R.id.ivOperador)), true);

                            }
                        }, 1000);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(2).findViewById(R.id.ivOperador)), true);

                            }
                        }, 2000);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(3).findViewById(R.id.ivOperador)), true);

                            }
                        }, 3000);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.showButton();

                            }
                        }, 3500);

                        showcaseView.setContentTitle(getString(R.string.sv_title_2));
                        showcaseView.setContentText(getString(R.string.sv_content_2));
                        showcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);


                        break;
                    case 1:
                        showcaseView.setShowcase(tgPhone, true);
                        showcaseView.setContentTitle(getString(R.string.sv_title_3));
                        showcaseView.setContentText(getString(R.string.sv_content_3));
                        showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                        break;
                    case 2:
                        showcaseView.setShowcase(tgAmount, true);
                        showcaseView.setContentTitle(getString(R.string.sv_title_4));
                        showcaseView.setContentText(getString(R.string.sv_content_4));
                        break;
                    case 3:
                        showcaseView.setShowcase(tgButton, true);
                        showcaseView.setContentTitle(getString(R.string.sv_title_5));
                        showcaseView.setContentText(getString(R.string.sv_content_5));
                        showcaseView.setButtonText(getString(R.string.sv_close_boton));
                        break;
                    case 4:
                        showcaseView.hide();
                }
                ShowCaseCounter++;
            }
        }).build();


    }

    public void EnableTopupButton(boolean pEnabled)
    {
        btnTopup.setClickable(pEnabled);
        btnTopup.setEnabled(pEnabled);
    }

    public void ShowErrorDialog()
    {
        String Titulo = "ALGO HA SALIDO MAL...";
        String Linea1 = getString(R.string.something_went_wrong_try_again);
        String Button = "OK";
        ProgressDialog.dismiss();
        txtPhoneNumber.setText("");
        getOperators();

        IsExecuting = false;
        EnableTopupButton(true);
    }

    public String RetrieveUserPin()
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

    public void SetHistoryOnFragment()
    {
        FragmentHistorialVentas fragmentHistorial = new FragmentHistorialVentas();
        android.support.v4.app.FragmentTransaction fragmentTransactionAApp = getSupportFragmentManager().beginTransaction();
        rlMainHomeContent.setVisibility(View.GONE);
        toolbar.setTitle("");
        fragmentTransactionAApp.replace(R.id.frame, fragmentHistorial);
        fragmentTransactionAApp.commit();
    }

    public void ResetControls()
    {
        txtPhoneNumber.setText("");
        OperatorSelected = false;

        //Remueve el operador seleccionado
        for (int i = 0; i < GridViewOperators.getAdapter().getCount(); i++)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                GridViewOperators.getChildAt(i)
                        .setBackground(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
            }
            else
            {
                GridViewOperators.getChildAt(i)
                        .setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
            }
        }

        //Resetea los montos
        selectedOperatorAmounts.clear();
        for (int i = 0; i < 2; i++)
        {
            selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        }
        SpinnerAmount.setSelection(AmountAdapter.getCount());
    }

    /*private void getDeviceSuperInfo() {
        Log.i(TAG, "getDeviceSuperInfo");

        try {

            String s = "Debug-infos:";
            s += "\n OS Version: "      + System.getProperty("os.version")      + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += "\n OS API Level: "    + android.os.Build.VERSION.SDK_INT;
            s += "\n Device: "          + android.os.Build.DEVICE;
            s += "\n Model (and Product): " + android.os.Build.MODEL            + " ("+ android.os.Build.PRODUCT + ")";

            s += "\n -----------------------"              ;
            s += "\n Model (just model): " + android.os.Build.MODEL;
            s += "\n BOARD: "              + android.os.Build.BOARD;
            s += "\n FINGERPRINT: "              + Build.FINGERPRINT;
            s += "\n HARDWAER: "              + Build.HARDWARE;
            s += "\n ID: "              + Build.ID;

            s += "\n -----------------------"              ;

            s += "\n RELEASE: "         + android.os.Build.VERSION.RELEASE;
            s += "\n BRAND: "           + android.os.Build.BRAND;
            s += "\n DISPLAY: "         + android.os.Build.DISPLAY;
            s += "\n CPU_ABI: "         + android.os.Build.CPU_ABI;
            s += "\n CPU_ABI2: "        + android.os.Build.CPU_ABI2;
            s += "\n UNKNOWN: "         + android.os.Build.UNKNOWN;
            s += "\n HARDWARE: "        + android.os.Build.HARDWARE;
            s += "\n Build ID: "        + android.os.Build.ID;
            s += "\n MANUFACTURER: "    + android.os.Build.MANUFACTURER;
            s += "\n SERIAL: "          + android.os.Build.SERIAL;
            s += "\n USER: "            + android.os.Build.USER;
            s += "\n HOST: "            + android.os.Build.HOST;


            Log.i(TAG + " | Device Info > ", s);

        } catch (Exception e) {
            Log.e(TAG, "Error getting Device INFO");
        }

    }//end getDeviceSuperInfo*/

    /*
    * ****************************************************************************
    *       PUSH NOTIFICATIONS LOGIC
    * ****************************************************************************
    */

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean CheckGooglePlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                //apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "Dispositivo si tiene soporte para Google Play Services.");
            }
            else
            {
                Log.i(TAG, "Este dispositivo no tiene soporte para Google Play Services.");
                Toast.makeText(this, getString(R.string.google_play_not_supported), Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public void BeginDeviceRegistration()
    {
        new AsyncTask<Object, Object, Object>()
        {
            @Override
            protected Object doInBackground(Object... params)
            {
                try
                {
                    String regid = gcm.register(NotificationSettings.SenderId);
                    registerClient.RegisterDevice(regid, new HashSet<String>());
                }
                catch (Exception e)
                {
                    /*runOnUiThread(new Runnable() {
                        public void run() {
                            // runs on UI thread
                            Toast.makeText(Home.this, "MainActivity - Failed to register", Toast.LENGTH_LONG).show();
                        }
                    });*/
                    Log.i("NotifHub:", "Falló registro de dispositivo");

                    return e;
                }
                return null;
            }
            protected void onPostExecute(Object result)
            {

            }
        }.execute(null, null, null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }


}


