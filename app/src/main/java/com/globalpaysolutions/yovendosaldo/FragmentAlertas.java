package com.globalpaysolutions.yovendosaldo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.adapters.NotificationsAdapter;
import com.globalpaysolutions.yovendosaldo.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class FragmentAlertas extends Fragment
{

    NotificationsAdapter NotifAdapter;
    ListView NotifListView;

    public FragmentAlertas()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_alertas, container, false);

        NotifListView = (ListView) view.findViewById(R.id.lvNotificaciones);
        NotifAdapter = new NotificationsAdapter(getActivity(), R.layout.custom_notification_listview_item);

        NotifListView.setAdapter(NotifAdapter);

        FillNotif();

        return view;
    }

    public void FillNotif()
    {
        List<Notification> notifs = new ArrayList<Notification>();

        Notification notif1 = new Notification();
        notif1.setTitle("Tu cuenta está activada");
        notif1.setContent("Ya puedes comenzar a vender saldo desde la App de YVS.com donde tu Master YVS te realizará recargas a tu cuenta para que comiences a vender desde donde quiera que te encuentres.");
        notifs.add(notif1);

        Notification notif2 = new Notification();
        notif2.setTitle("Quíntuple Saldo TIGO");
        notif2.setContent("Ahora todos tus clientes que recarguen a traves de YVS.com recibirán quíntuple saldo desde recargas desde $5.00 en adelante. \r\n \nDuración de saldo: 2 días.");
        notifs.add(notif2);

        for (Notification item : notifs)
        {
            NotifAdapter.add(item);
        }

    }

}
