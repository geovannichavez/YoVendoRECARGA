package com.globalpaysolutions.yovendosaldo.engagement;

import com.microsoft.azure.engagement.reach.activity.EngagementTextAnnouncementActivity;

/**
 * Created by Josué Chávez on 09/08/2016.
 */
public class CustomTextAnnouncementActivity extends EngagementTextAnnouncementActivity
{
    @Override
    protected String getLayoutName()
    {
        //Es posible añadir lógica aqui??
        return "custom_engagement_text_announcement";
    }
}
