package com.globalpaysolutions.yovendorecarga.customs;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

/**
 * Created by Josué Chávez on 12/08/2016.
 */

public class YvsPhoneStateListener extends PhoneStateListener
{
    public static int signalStrengthValue;

    public static  int signalStrengthPercent;
    public static final int UNKNOW_CODE = 99;
    int MAX_SIGNAL_DBM_VALUE = 31;

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {
        super.onSignalStrengthsChanged(signalStrength);

        /*if (signalStrength.isGsm())
        {
            if (signalStrength.getGsmSignalStrength() != 99)
            {
                signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
            }
            else
            {
                signalStrengthValue = signalStrength.getGsmSignalStrength();
            }

        }*/

        if (null != signalStrength && signalStrength.getGsmSignalStrength() != UNKNOW_CODE)
        {
            signalStrengthPercent = calculateSignalStrengthInPercent(signalStrength.getGsmSignalStrength());
        }

    }

    private int calculateSignalStrengthInPercent(int signalStrength)
    {
        return (int) ((float) signalStrength / MAX_SIGNAL_DBM_VALUE * 100);
    }
}
