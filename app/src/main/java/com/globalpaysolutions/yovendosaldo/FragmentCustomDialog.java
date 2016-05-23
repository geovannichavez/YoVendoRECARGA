package com.globalpaysolutions.yovendosaldo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.customs.SessionManager;

public class FragmentCustomDialog extends DialogFragment
{
    TextView tvTitle;
    TextView tvLine1;
    TextView tvLine2;
    TextView tvLine3;
    ImageView ivResult;
    SessionManager sessionManager;

    public static String Action;

    public FragmentCustomDialog()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_custom_dialog, container, false);

        sessionManager = new SessionManager(getActivity());

        tvTitle = (TextView) view.findViewById(R.id.tvDialogTitle);
        tvLine1 = (TextView) view.findViewById(R.id.tvFirstLine);
        tvLine2 = (TextView) view.findViewById(R.id.tvSecondLine);
        tvLine3 = (TextView) view.findViewById(R.id.tvThirdLine);
        ivResult = (ImageView) view.findViewById(R.id.ivResult);

        String Title = this.getArguments().getString("Title");
        String Line1 = this.getArguments().getString("Line1");
        String Line2 = this.getArguments().getString("Line2");
        String Line3 = this.getArguments().getString("Line3");
        String Button = this.getArguments().getString("Button");
        Action = this.getArguments().getString("Action");
        boolean Error = this.getArguments().getBoolean("Error");
        boolean FromTopup = this.getArguments().getBoolean("FromTopup");

        tvTitle.setText(Title);
        tvLine1.setText(Line1);
        tvLine2.setText(Line2);
        tvLine3.setText(Line3);

        if(Error)
        {
            if(FromTopup)
            {
                ivResult.setImageResource(R.drawable.ic_phone_error);
            }
            else
            {
                ivResult.setImageResource(R.drawable.ic_error_blanco);
            }

        }
        else
        {
            if(FromTopup)
            {
                ivResult.setImageResource(R.drawable.ic_phone_success);
            }
            else
            {
                ivResult.setImageResource(R.drawable.ic_check_blanco);
            }
        }

        Button dismiss = (Button) view.findViewById(R.id.btnDismiss);
        dismiss.setText(Button);
        dismiss.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                switch (Action)
                {
                    case "NEWACTION":
                        DismissFragment();
                        break;
                    case "LOGOUT":
                        sessionManager.LogoutUser();
                        break;
                    case "NAVIGATEHOME":
                        Intent i = new Intent(getActivity(), Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(i);
                        (getActivity()).finish();
                    default:
                        DismissFragment();
                        break;
                }

            }
        });

        return view;
    }

    public void DismissFragment()
    {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(FragmentCustomDialog.this);
        trans.commit();
        manager.popBackStack();
    }

}
