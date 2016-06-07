package com.globalpaysolutions.yovendosaldo;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.globalpaysolutions.yovendosaldo.adapters.BankSpinnerAdapter;
import com.globalpaysolutions.yovendosaldo.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;
import com.globalpaysolutions.yovendosaldo.customs.StringsURL;
import com.globalpaysolutions.yovendosaldo.customs.Validation;
import com.globalpaysolutions.yovendosaldo.customs.YVScomSingleton;
import com.globalpaysolutions.yovendosaldo.model.Bank;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentDepositoBancario extends Fragment
{
    //Variables globales
    List<Bank> ListaBancos = new ArrayList<>();
    BankSpinnerAdapter BankAdapter;
    int SelectedBank;
    static boolean DateSeted = false;
    Validation validator;
    boolean BankSelected = false;
    CustomFullScreenDialog FullScreenDialog;
    SessionManager sessionManager;
    static String Token;
    int VoucherMinLength;

    //Controles, vistas y layouts
    public static TextView tvFechaDeposito;
    Spinner spBanks;
    EditText edNombreDepositante;
    EditText edMonto;
    EditText edComprobante;
    Button btnEnviar;
    ProgressDialog progressDialog;

    //Variables para la fecha del deposito:
    static Date DepositDate;
    public static int DepositYear = 0;
    public static int DepositMonth = 0;
    public static int DepositDay = 0;

    public FragmentDepositoBancario()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_deposito_bancario, container, false);



        FullScreenDialog = new CustomFullScreenDialog(getContext(), getActivity());
        sessionManager = new SessionManager(getContext());

        spBanks = (Spinner) view.findViewById(R.id.spBanks);
        edNombreDepositante = (EditText) view.findViewById(R.id.edDepositante);
        edMonto = (EditText) view.findViewById(R.id.edMonto);
        edComprobante = (EditText) view.findViewById(R.id.edComprobante);
        btnEnviar = (Button) view.findViewById(R.id.btnEnviarDeposito);
        btnEnviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                VoucherValidation();
            }
        });

        tvFechaDeposito = (TextView) view.findViewById(R.id.tvFechaDeposito);
        tvFechaDeposito.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
            }
        });

        RetrieveSavedToken();
        PopulateBankSpinner();
        InitializeValidation();

        return view;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            DateSeted = true;

            DepositYear = year;
            DepositMonth = month;
            DepositDay = day;

            Calendar C = new GregorianCalendar(year,month,day);
            DepositDate = C.getTime();

            tvFechaDeposito.setText(new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es_ES")).format(DepositDate));

            //System.out.println(new SimpleDateFormat("dd/MMMM/yyyy").format(new Date()));

        }
    }

    public void VoucherValidation()
    {
        if(CheckValidation())
        {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getResources().getString(R.string.deposit_dending_deposit));
            progressDialog.show();
            String NombreDepositante = edNombreDepositante.getText().toString();
            double Monto = Double.parseDouble(edMonto.getText().toString());
            String Comprobante = edComprobante.getText().toString();
            int BancoID = SelectedBank;
            String Fecha = new SimpleDateFormat("dd-MMM-yyyy", new Locale("es_ES")).format(DepositDate);

            JSONObject jVoucher = new JSONObject();

            try
            {
                jVoucher.put("nombre", NombreDepositante);
                jVoucher.put("Banco", BancoID);
                jVoucher.put("monto", Monto);
                jVoucher.put("comprobante", Comprobante);
                jVoucher.put("fecha", Fecha);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            ValidateVoucher(jVoucher);
        }

    }

    public void ValidateVoucher(JSONObject pVoucher)
    {
        if (CheckConnection())
        {
            YVScomSingleton.getInstance(getContext()).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            StringsURL.DEPOSIT,
                            //StringsURL.TEST_TIMEOUT,
                            pVoucher,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    Log.d("Mensaje JSON ", response.toString());
                                    ProcessVoucherResponse(response);
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
                    }, 1); //Parametro, de maximo de re-intentos
        }
    }

    public void ProcessVoucherResponse(JSONObject pResponse)
    {
        final JSONObject VoucherResponse = pResponse;

        try
        {
            boolean Status = VoucherResponse.has("status") ? VoucherResponse.getBoolean("status") : false;

            if(Status)
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(
                        getResources().getString(R.string.deposit_success),
                        getResources().getString(R.string.deposit_success_line1),
                        null, null, "Aceptar", "NAVIGATEHOME", false, false );
            }
            else
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(
                        getResources().getString(R.string.deposit_error),
                        getResources().getString(R.string.deposit_error_line1),
                        null, null, "Aceptar", "NEWACTION", true, false);
                ClearFields();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


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
    ****************************
    OTROS METODOS
    ****************************
    */
    public void PopulateBankSpinner()
    {
        Bank citi = new Bank();
        citi.setID(1);
        citi.setName(getString(R.string.bank_name_citi));
        ListaBancos.add(citi);

        Bank agricola = new Bank();
        agricola.setID(2);
        agricola.setName(getString(R.string.bank_name_agricola));
        ListaBancos.add(agricola);

        Bank bac = new Bank();
        bac.setID(3);
        bac.setName(getString(R.string.bank_name_bac));
        ListaBancos.add(bac);

        Bank hint = new Bank();
        hint.setID(0);
        hint.setName(getString(R.string.spinner_hint));
        ListaBancos.add(hint);

        BankAdapter = new BankSpinnerAdapter(getActivity(), R.layout.custom_bank_spinner_item, R.id.tvBankName, ListaBancos);
        BankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBanks.setAdapter(BankAdapter);
        spBanks.setSelection(BankAdapter.getCount());
        spBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {

                SelectedBank = ((Bank) parentView.getItemAtPosition(position)).getID();
                if(SelectedBank == 0)
                {
                    BankSelected = false;
                }
                else
                {
                    BankSelected = true;
                }

                edComprobante.setText("");
                edComprobante.setError(null);
                switch (SelectedBank)
                {
                    case 1: //Banco Citi
                        ChangeEdittextMaxLength(8, edComprobante);
                        VoucherMinLength = 8;
                        break;
                    case 2: //Banco Agricola
                        ChangeEdittextMaxLength(9, edComprobante);
                        VoucherMinLength = 9;
                        break;
                    case 3: //Banco America Central
                        ChangeEdittextMaxLength(8, edComprobante);
                        VoucherMinLength = 8;
                        break;
                    default:
                        ChangeEdittextMaxLength(9, edComprobante);
                        VoucherMinLength = 9;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });
    }

    public void RetrieveSavedToken()
    {
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);
    }

    public void InitializeValidation()
    {
        edNombreDepositante.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    validator = new Validation(getContext().getApplicationContext());
                    validator.IsValidName(edNombreDepositante, true);
                    validator.HasText(edNombreDepositante);
                }
            }
        });

        edMonto.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    validator = new Validation(getContext().getApplicationContext());
                    validator.IsValidAmount(edMonto, true);
                    validator.HasText(edMonto);
                }
            }
        });
        edComprobante.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    validator = new Validation(getContext().getApplicationContext());
                    validator.IsValidVoucher(edComprobante, true);
                    validator.HasText(edComprobante);
                }
            }
        });

    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        validator = new Validation(getActivity().getApplicationContext());

        if (!validator.IsValidName(edNombreDepositante, true))
        {
            ret = false;
        }

        if(!BankSelected)
        {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.spinner_bank_validation), Toast.LENGTH_LONG).show();
            ret = false;
        }

        if(!DateSeted)
        {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.validation_required_date), Toast.LENGTH_LONG).show();
            ret = false;
        }

        if(!validator.IsValidAmount(edMonto, true))
        {
            ret = false;
        }

        if(!validator.IsValidVoucher(edComprobante, true))
        {
            ret = false;
        }

        if(!validator.IsValidMinLength(edComprobante, VoucherMinLength))
        {
            ret = false;
        }

        return ret;
    }

    public void ClearFields()
    {
        edNombreDepositante.setText("");
        edMonto.setText("");
        edComprobante.setText("");
        spBanks.setSelection(BankAdapter.getCount());
        tvFechaDeposito.setText(getResources().getString(R.string.not_setted));
        BankSelected = false;
        DateSeted = false;
    }

    private boolean CheckConnection()
    {
        boolean connected;

        if(HasNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = "No esta conectado a internet.";
            Toast.makeText(getContext(), connectionMessage, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    private boolean HasNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void ChangeEdittextMaxLength(int pLength, EditText pEditText)
    {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(pLength);
        pEditText.setFilters(FilterArray);
    }

}
