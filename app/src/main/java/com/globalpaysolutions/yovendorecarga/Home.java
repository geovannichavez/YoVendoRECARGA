package com.globalpaysolutions.yovendorecarga;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import com.globalpaysolutions.yovendorecarga.adapters.AmountSpinnerAdapter;
import com.globalpaysolutions.yovendorecarga.adapters.OperatorsAdapter;
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.Data;
import com.globalpaysolutions.yovendorecarga.customs.DatabaseHandler;
import com.globalpaysolutions.yovendorecarga.customs.DeviceName;
import com.globalpaysolutions.yovendorecarga.customs.LocationTracker;
import com.globalpaysolutions.yovendorecarga.customs.PinDialogBuilder;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.globalpaysolutions.yovendorecarga.customs.YvsPhoneStateListener;
import com.globalpaysolutions.yovendorecarga.model.Amount;
import com.globalpaysolutions.yovendorecarga.model.LocationData;
import com.globalpaysolutions.yovendorecarga.model.Operator;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//For Push Notifications
import com.globalpaysolutions.yovendorecarga.model.TopupModel;
import com.globalpaysolutions.yovendorecarga.model.TopupResult;
import com.globalpaysolutions.yovendorecarga.rest.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

//Azure Mobile Engagement
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.microsoft.azure.engagement.EngagementAgent;
import com.microsoft.azure.engagement.EngagementAgentUtils;


//Location
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;


public class Home extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener
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
    List<Operator> arrivingOperadores = new ArrayList<>();
    private static final String TAG = Home.class.getSimpleName();
    public static String Token;
    boolean OperatorSelected = false;
    boolean RetrievingAmounts = false;
    String SelectedOperatorName;
    //int AmountTopup;
    double AmountTopup;
    SessionManager sessionManager;
    Validation validator;
    private ShowcaseView showcaseView;
    private int ShowCaseCounter = 0;
    private boolean IsExecuting = false;
    private boolean isFirstTime;
    CustomFullScreenDialog CustomDialogCreator;
    PinDialogBuilder PinDialogBuilder;
    public PinDialogBuilder.CustomOnClickListener ClickListener;
    String mNMO;
    boolean mFineLocationGranted;
    boolean mCoarseLocationGranted;
    DatabaseHandler db;

    //Location
    LocationTracker locationTracker;
    LocationData mLocationData;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    //Signal
    TelephonyManager mTelephonyManager;
    YvsPhoneStateListener psListener;


    //Push Notifications
    public static Home homeActivity;
    public static Boolean isVisible = false;


    //Lock screen
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Engagement
        if (EngagementAgentUtils.isInDedicatedEngagementProcess(this))
        {
            return;
        }

        //Inicialización de Activity
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Creación de objetos globales
        sessionManager = new SessionManager(Home.this);
        CustomDialogCreator = new CustomFullScreenDialog(Home.this, Home.this);
        db = new DatabaseHandler(Home.this);

        //Seteo de vistas y layouts globales
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        tvBalance = (TextView) findViewById(R.id.tvAvailableBalance);
        txtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnTopup = (Button) findViewById(R.id.btnEnvar);
        scrollView = (ScrollView) findViewById(R.id.homeScrollView);
        lnrBalance = (LinearLayout) findViewById(R.id.rectangle);

        isFirstTime = sessionManager.IsFirstTime();
        homeActivity = this;

        InitializeValidation();
        CheckLogin();
        RetrieveSavedToken();


        //Signal
        psListener = new YvsPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);



        /*
        ******************************
        *
        *   Navigation Drawer
        *
        ******************************
        */

        InitializeNavigationDrawer();

        /*
        ******************************
        *
        *   End Navigation Drawer
        *
        ******************************
        */


        /*
        *
        *   OPERADORES Y BAG
        *
        */

        SetBalanceTextView();
        //getOperators();
        retrieveOperators();
        //int countryID = Integer.valueOf(RetrieveCountryID());
        //ListaOperadores = db.getUserOperators(countryID);
        //setOperators();

        //retrieveOperators();

        if (isFirstTime)
        {
            //ExecuteShowcase();
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
                GetUserBag(false);

                //Refrescar montos
                //Data.Amounts.clear();
                RetrieveAmounts();
            }
        });


        if (sessionManager.IsUserLoggedIn())
        {
            if(!isFirstTime)
            {
                SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                SwipeRefresh.setRefreshing(true);
            }
            GetUserBag(false);
            RetrieveAmounts();
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
                NavigateHistoryActivity();
            }
        });






        /*
        *
        *   LOCATION
        *
        *
        */

        if (checkPlayServices())
        {
            buildGoogleApiClient();
            createLocationRequest();
        }
        mLocationData = new LocationData();

        /*if(sessionManager.IsUserLoggedIn())
        {
            RetrieveAmounts();
        }*/


        PrintAZMEDeviceID();
    }



    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   NAVIGATION DRAWER
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void InitializeNavigationDrawer()
    {
        //Navigation Drawer
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationFooter = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        rlMainHomeContent = (RelativeLayout) findViewById(R.id.rlMainHomeContent);

        //Setea el menu dependiendo del tipo de vendedor
        if (isVendorM())
        {
            navigationView.inflateMenu(R.menu.m_vendor_drawer);
        }
        else
        {
            navigationView.inflateMenu(R.menu.drawer);
        }

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
                        Intent deposito = new Intent(getApplication().getApplicationContext(), DepositoBancario.class);
                        startActivity(deposito);
                        return true;

                    case R.id.Historial:
                        Intent history = new Intent(getApplication().getApplicationContext(), HistorialVentas.class);
                        startActivity(history);
                        return true;

                    case R.id.SolicitarSaldo:
                        Intent solicitarSaldo = new Intent(getApplication().getApplicationContext(), SolicitarSaldo.class);
                        startActivity(solicitarSaldo);
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

    }






    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   TOPUP
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void RequestTopUp(View view)
    {
        getLocation();

        EnableTopupButton(false);
        Log.i("Print click", "Para saber cuantas veces se ha hecho click en el botón.");

        final String PhoneNumber = txtPhoneNumber.getText().toString();

        if (CheckValidation())
        {
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
                                if (Data.IntentCounter < 3)
                                {
                                    PinDialogBuilder.GenerateIncorrectPINText();
                                    Data.IntentCounter = Data.IntentCounter + 1;
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
        else
        {
            EnableTopupButton(true);
        }

    }

    public void BeginTopup(String PhoneNumber)
    {
        if (CheckConnection())
        {
            PhoneNumber = PhoneNumber.replace("-", "");

            sendDeviceData(collectDeviceData());


            String Amount = "";

            if ((AmountTopup == Math.floor(AmountTopup)) && !Double.isInfinite(AmountTopup))
            {
                int amountInt = (int) AmountTopup;
                Amount = String.valueOf(amountInt);
            }
            else
            {
                Amount = String.valueOf(AmountTopup);
            }

            ProgressDialog = new ProgressDialog(Home.this);
            ProgressDialog.setMessage("Enviando Recarga...");
            ProgressDialog.show();
            ProgressDialog.setCancelable(false);
            ProgressDialog.setCanceledOnTouchOutside(false);

            //***   ALERTA!! CON ESTE METODO SE ENVIA               ***
            //***   LA RECARGA, TENER CUIDADO CON IMPLEMENTACION    ***
            TopUp(PhoneNumber, Amount);

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

        //Locks the screen while sending request
        activateLockScreenFromSleep(true);

        //Setting up TopupData
        String countryCode = RetrieveCountryPhoneCode();
        String CountryID = RetrieveCountryID();
        String TopupData = countryCode + pPhoneNumber + "/" + pAmount + "/";

        //Making request to server
        TopupModel model = new TopupModel(SelectedOperatorName, CountryID);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TopupResult> call = apiService.getTopupResult(TopupData, Token, model);

        //Handle response
        call.enqueue(new Callback<TopupResult>() {
            @Override
            public void onResponse(Call<TopupResult> call, retrofit2.Response<TopupResult> response)
            {
                if(response.isSuccessful())
                {
                    TopupResult topupResult = response.body();
                    activateLockScreenFromSleep(false);
                    ProgressDialog.dismiss();
                    ProcessTopupResponse(topupResult);
                }
                else
                {
                    int codeResponse = response.code();
                    ProcessRetrofitError(codeResponse);
                }
            }
            @Override
            public void onFailure(Call<TopupResult> call, Throwable t)
            {
                ProgressDialog.dismiss();
                if(t instanceof SocketTimeoutException)
                {
                    //Toast.makeText(Home.this, "Tiempo agotado", Toast.LENGTH_LONG).show();
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
                else
                {
                    String Titulo = "ALGO HA SALIDO MAL...";
                    String Linea1 = getString(R.string.something_went_wrong_try_again);
                    String Button = "OK";
                    ResetControls();

                    IsExecuting = false;
                    EnableTopupButton(true);
                    CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true);
                }

                //Unlocks the screen on response
                activateLockScreenFromSleep(false);
            }

        });


    }

    //Procesa Respuesta de TopUp
    public void ProcessTopupResponse(TopupResult pResponse)
    {
        //Restablece el scroll al tope despues de enviar recarga.
        scrollView.fullScroll(View.FOCUS_UP);

        String PhoneUsed = txtPhoneNumber.getText().toString();
        String Operator = "";
        try
        {
            String Message = pResponse.getMessage();
            String Balance = pResponse.getAvailableAmount();

            sessionManager.SaveAvailableBalance(Balance);
            SetBalanceTextView();
            Log.d("Resultado: ", Message);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        CustomDialogCreator.CreateFullScreenDialog(getString(R.string.dialog_succeed_topoup_title), getString(R.string.dialog_succeed_topup_content), PhoneUsed, Operator, "Enviar Otra Recarga", "NEWACTION", false, true);

        //Resetea todos los controles
        IsExecuting = false;
        EnableTopupButton(true);
        ResetControls();


    }

    public void ProcessRetrofitError(int error)
    {
        scrollView.fullScroll(View.FOCUS_UP);

        String Titulo;
        String Linea1;
        String Linea2;
        String Button;
        ProgressDialog.dismiss();

        switch (error)
        {
            case 502:
                Titulo = getString(R.string.expired_session);
                Linea1 = getString(R.string.dialog_error_topup_content);
                Button = "OK";
                txtPhoneNumber.setText("");
                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "LOGOUT", true, true);
                break;
            case 503:
                Titulo = getString(R.string.insufficent_balance_title);
                Linea1 = getString(R.string.insufficent_balance_ln1);
                Linea2 = getString(R.string.insufficent_balance_ln2);
                Button = "OK";
                ResetControls();
                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, Button, "NAVIGATEHOME", true, true);
                break;
            default:
                Titulo = "ALGO HA SALIDO MAL...";
                Linea1 = getString(R.string.something_went_wrong_try_again);
                Button = "OK";
                ResetControls();
                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true);
                break;
        }
    }






    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   OPERADORES Y MONTOS
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void retrieveOperators()
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
                                HandleOperatorsVolleyError(error);
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
        try
        {
            JSONObject operators = pResponse.getJSONObject("operators");
            JSONArray countryOperators = operators.getJSONArray("countryOperators");

            for (int i = 0; i < countryOperators.length(); i++)
            {
                Operator operator = new Operator();

                JSONObject jsonOperator = countryOperators.getJSONObject(i);
                operator.setID(jsonOperator.has("operatorID") ? jsonOperator.getInt("operatorID") : 0);
                operator.setOperatorName(jsonOperator.has("name") ? jsonOperator.getString("name") : "");
                operator.setLogo(jsonOperator.has("operatorLogo") ? jsonOperator.getString("operatorLogo") : "");
                operator.setLogoURL(jsonOperator.has("logoUrl") ? jsonOperator.getString("logoUrl") : "");
                operator.setLogoVersion(jsonOperator.has("logoVersion") ? jsonOperator.getInt("logoVersion") : 0);
                operator.setBrand(jsonOperator.has("brand") ? jsonOperator.getString("brand") : "");

                ListaOperadores.add(operator);

            }

            setOperators();

            //Setea el showcase view despues que se lleno el adapter
            if (isFirstTime)
            {
                ExecuteShowcase();
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    public void HandleOperatorsVolleyError(VolleyError pError)
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
                //mNMO = SelectedOperatorName;
                getServerAmounts(mNMO);

            }
        });

        for (Operator item : ListaOperadores)
        {
            OpeAdapter.add(item);
        }

    }

    public void setOperators()
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

        GridViewOperators.setNumColumns(ListaOperadores.size());

        OpeAdapter = new OperatorsAdapter(this, R.layout.custom_operator_gridview_item);
        GridViewOperators.setAdapter(OpeAdapter);

        GridViewOperators.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                OperatorSelected = true;
                SelectedOperatorName = ((Operator) parent.getItemAtPosition(position)).getOperatorName();
                mNMO = ((Operator) parent.getItemAtPosition(position)).getBrand();

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

                getServerAmounts(mNMO);

            }
        });


        for (Operator item : ListaOperadores)
        {
            OpeAdapter.add(item);
        }

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

        //Si no hay montos del operador seleccionado, añade el hint 2 veces
        if(selectedOperatorAmounts.size() == 0)
        {
            for (int i = 0; i < 2; i++)
            {
                selectedOperatorAmounts.add(Data.AmountHint(Home.this));
            }
        }
        else
        {
            selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        }

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
        final GridView gridOperators = (GridView) findViewById(R.id.gvOperadores);
        if (Data.Amounts.isEmpty())
        {

            Data.Amounts.clear();
            RetrievingAmounts = true;
            gridOperators.setEnabled(false);
            Log.i("Amounts", "Request para traer montos");

            /*if (!isFirstTime)
            {
                SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                SwipeRefresh.setRefreshing(true);
            }*/


            Data.GetAmounts(Home.this, new Data.VolleyCallback()
            {
                @Override
                public void onResult(boolean result, JSONObject response)
                {
                    if (result)
                    {
                        RetrievingAmounts = false;
                        //SwipeRefresh.setRefreshing(false);
                        HideSwipe();
                        gridOperators.setEnabled(true);

                        getLocation();

                        /* Se obtiene la ubicacion despues de que
                        los montos se hayan obtenido para darle suficiente
                        tiempo a LocationRequest de conectar */
                        sendDeviceData(collectDeviceData());
                    }
                    else
                    {
                        getLocation();
                        RetrievingAmounts = false;
                        //SwipeRefresh.setRefreshing(false);
                        HideSwipe();
                        gridOperators.setEnabled(true);
                    }
                }
            });
        }
    }




    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   BOLZÓN DE USUARIO
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void GetUserBag(boolean isSwipe)
    {
        /*if (isSwipe)
        {
            SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            SwipeRefresh.setRefreshing(true);
        }*/

        YVScomSingleton.getInstance(Home.this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, StringsURL.USERBAG, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
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
        if (SwipeRefresh.isShown() && SwipeRefresh != null && !RetrievingAmounts)
        {
            SwipeRefresh.setRefreshing(false);
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
        formatter.setDecimalSeparatorAlwaysShown(true);
        formatter.setMinimumFractionDigits(2);

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
            private static final char dash = '-';

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
                    if (dash == c)
                    {
                        text.delete(text.length() - 1, text.length());
                    }
                }
                // Insert char where needed.
                if (text.length() > 0 && (text.length() % 5) == 0)
                {
                    char c = text.charAt(text.length() - 1);
                    // Only if its a digit where there should be a dash we insert a dash
                    if (Character.isDigit(c) && TextUtils.split(text.toString(), String.valueOf(dash)).length <= 3)
                    {
                        text.insert(text.length() - 1, String.valueOf(dash));
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

    int showcaseOperatorRuns;
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

                        scrollView.fullScroll(View.FOCUS_DOWN);

                        Handler handler = new Handler();
                        if(ListaOperadores.size() != 0)
                        {
                            showcaseView.hideButton();
                        }
                        //showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(0).findViewById(R.id.ivOperador)), true)

                        int time = 0;
                        showcaseOperatorRuns = GridViewOperators.getChildCount();

                        for(int i = 0; i < GridViewOperators.getChildCount(); i++)
                        {
                            final int position = i;
                            time = time + 1000;
                            handler.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    showcaseOperatorRuns = showcaseOperatorRuns -1;
                                    showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(position).findViewById(R.id.networkViewOperador)), true);

                                    if(showcaseOperatorRuns == 0)
                                    {
                                        showcaseView.showButton();
                                    }
                                }
                            }, time);
                        }

                        /*handler.postDelayed(new Runnable()
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
                        }, 3500);*/

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
                        scrollView.fullScroll(View.FOCUS_UP);
                        showcaseView.hide();
                        askForLocationActivation();
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

    public void NavigateHistoryActivity()
    {
        Intent history = new Intent(getApplication().getApplicationContext(), HistorialVentas.class);
        startActivity(history);
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
                GridViewOperators.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
            }
            else
            {
                GridViewOperators.getChildAt(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
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

    public void activateLockScreenFromSleep(boolean pActivate)
    {
        if(pActivate)
        {
            wakeLock.acquire();
        }
        else
        {
            wakeLock.release();
        }
    }

    public String RetrieveCountryPhoneCode()
    {
        String phoneCode = "";
        HashMap<String, String> countryPhoneCode = sessionManager.GetCountryPhoneCode();
        phoneCode = countryPhoneCode.get(SessionManager.KEY_PHONE_CODE);

        return phoneCode;
    }

    public String RetrieveCountryID()
    {
        String countryID = "0";
        HashMap<String, String> country_ID = sessionManager.GetCountryID();
        countryID = country_ID.get(SessionManager.KEY_COUNTRY_ID);

        if(countryID.equals(""))
        {
            countryID = "0";
        }

        return countryID;
    }

    public boolean isVendorM()
    {
        boolean vendorM;
        HashMap<String, Boolean> MapVendor = sessionManager.GetVendorInfo();
        vendorM = MapVendor.get(SessionManager.KEY_VENDOR_M);

        return vendorM;
    }

    public static boolean containsID(Collection<Operator> pOpe, int pID)
    {
        for(Operator o : pOpe)
        {
            if(o != null && o.getID() == pID)
            {
                return true;
            }
        }
        return false;
    }

    public void PrintAZMEDeviceID()
    {
        EngagementAgent.getInstance(this).getDeviceId(new EngagementAgent.Callback<String>()
        {
            @Override
            public void onResult(String deviceId)
            {
                Log.v("YVR", "AzME device ID:" + deviceId);
            }
        });
    }


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





    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   ACTIVITY'S LIFECYCLE
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    @Override
    protected void onStart()
    {
        super.onStart();
        isVisible = true;
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        EngagementAgent.getInstance(this).endActivity();
        if (ProgressDialog != null && ProgressDialog.isShowing())
        {
            ProgressDialog.dismiss();
        }
        isVisible = false;

        //Screen lock
        //wakeLock.release();

        stopLocationUpdates();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Uses short class name and removes "Activity" at the end.
        String activityNameOnEngagement = EngagementAgentUtils.buildEngagementActivityName(getClass());
        EngagementAgent.getInstance(this).startActivity(this, activityNameOnEngagement, null);
        isVisible = true;

        if (mGoogleApiClient != null)
        {
            // Resuming the periodic location updates
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates)
            {
                startLocationUpdates();
            }
        }

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "YVR Lock");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isVisible = false;

        if (mGoogleApiClient != null)
        {
            if (mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
            }
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




    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   DATOS DEL DISPOSITIVO
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public JSONObject collectDeviceData()
    {
        //Carrier Name
        String carrierName = mTelephonyManager.getNetworkOperatorName();

        //Network Type
        String networkType = getNetworkType();

        //dBm Signal Strength
        //int signalStrength = psListener.signalStrengthValue;
        int signalStrength = YvsPhoneStateListener.signalStrengthPercent;

        //Device manufacturer
        String Manufacturer = Build.MANUFACTURER;
        Manufacturer = Manufacturer.substring(0, 1).toUpperCase() + Manufacturer.substring(1).toLowerCase();

        //Device Model
        String Model = DeviceName.getDeviceName();

        //Location data, duh!!..
        //mLocationData = locationTracker.getLocation();


        JSONObject deviceData = new JSONObject();
        try
        {
            deviceData.put("carrierName", carrierName);
            deviceData.put("networkType", networkType);
            deviceData.put("dBm", signalStrength);
            deviceData.put("phoneModel", Model);
            deviceData.put("phoneManufacturer", Manufacturer);
            deviceData.put("latitude", mLocationData.getLatitude());
            deviceData.put("longitude", mLocationData.getLongitude());

        } catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        return deviceData;

    }

    private String getNetworkType()
    {
        int networkType = mTelephonyManager.getNetworkType();

        switch (networkType)
        {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

    public void sendDeviceData(JSONObject pDeviceData)
    {
        if(isLocationServiceEnabled())
        {
            if(mLocationData.getLatitude() != 0 && mLocationData.getLongitude() != 0)
            {
                YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, StringsURL.CEOA_DEVICE_DATA, pDeviceData, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DeviceData ", response.toString());
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String errorDetails = error.getMessage();
                        Log.i("DeviceData", errorDetails);
                    }
                })
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("apikey", StringsURL.CEO_ANALYTICS_APIKEY);
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1);
            }
        }
    }



    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   GEO-LOCATION
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    /**
     * Method to display the location on UI
     * */
    private void getLocation()
    {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else
        {
            if(mGoogleApiClient != null)
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null)
                {
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    mLocationData.setLongitude(longitude);
                    mLocationData.setLatitude(latitude);
                }
                else
                {
                    Log.i(TAG, "Couldn't get the location. Make sure location is enabled on the device");
                }
            }
        }
    }

    /**
     * Method to toggle periodic location updates
     * */
    private void startPeriodicLocationUpdates()
    {
        if (mRequestingLocationUpdates)
        {
            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                /*Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();*/
                Log.i(TAG, "This device does not support Google Play Services");
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates()
    {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }
        else
        {
            if(mGoogleApiClient != null)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates()
    {
        if(mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0)
    {
        // Once connected with google api, get the location
        //displayLocation();

        if (mRequestingLocationUpdates)
        {
            //startLocationUpdates();
            startPeriodicLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        if(mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        // Assign the new location
        mLastLocation = location;
        Log.i(TAG, "Location DID changed");

    }

    public boolean isLocationServiceEnabled()
    {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;

        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        try
        {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex)
        {
            //do nothing...
        }

        try
        {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex)
        {
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

    public void askForLocationActivation()
    {
        if(!isLocationServiceEnabled())
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle(getString(R.string.title_activate_location));
            alertDialog.setMessage(getString(R.string.content_activate_location));
            alertDialog.setCancelable(false);
            alertDialog.setNeutralButton("ACTIVAR", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    public interface ApiInterface
    {
        @POST
        Call<TopupResult> getTopupResult(@Url String topupUrl, @Header("Token-autorization") String tokenAuthorization, @Body TopupModel topupModel);

    }

}


