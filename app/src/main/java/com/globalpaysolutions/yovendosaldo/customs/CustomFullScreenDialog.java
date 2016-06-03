package com.globalpaysolutions.yovendosaldo.customs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.globalpaysolutions.yovendosaldo.FragmentCustomDialog;

/**
 * Created by Josué Chávez on 06/05/2016.
 */
public class CustomFullScreenDialog
{
    Context _context;
    FragmentActivity _fragmentManager;

    public CustomFullScreenDialog(Context context, FragmentActivity  fragmentManager)
    {
        _context = context;
        _fragmentManager = fragmentManager;
    }

    /**
     * Setea el DialogFragment que se mostrará al momento de un Diálogo.
     * @param pTitle El título que se mostrará en el dialogo.
     * @param pMsgLine1 Línea 1 del mensaje.
     * @param pMsgLine2 Línea 2 del mensaje.
     * @param pMsgLine3 Línea 3 del mensaje.
     * @param pButton Texto que llevará el boton
     * @param pAction La acción que se realizará al precionar el botón.
     * @param pError (Boolean) <strong>True</strong> si es error y <strong>False</strong> si no.
     * @param pFromTopup (Boolean) <strong>True</strong> si viene de Topup y <strong>False</strong> si no.
     */
    public void CreateFullScreenDialog(String pTitle, String pMsgLine1, String pMsgLine2, String pMsgLine3, String pButton, String pAction, boolean pError, boolean pFromTopup)
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

        FragmentManager fragmentManager = _fragmentManager.getSupportFragmentManager();
        FragmentCustomDialog CustomDialog = new FragmentCustomDialog();
        CustomDialog.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(android.R.id.content, CustomDialog, "FragmentCustomDialog").addToBackStack("tag").commit();


    }
}
