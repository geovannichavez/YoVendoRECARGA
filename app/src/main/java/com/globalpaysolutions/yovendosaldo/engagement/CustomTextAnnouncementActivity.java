package com.globalpaysolutions.yovendosaldo.engagement;

import com.microsoft.azure.engagement.reach.activity.EngagementTextAnnouncementActivity;

/**
 * Created by Josu� Ch�vez on 09/08/2016.
 */
public class CustomTextAnnouncementActivity extends EngagementTextAnnouncementActivity
{
    @Override
    protected String getLayoutName()
    {
        return "custom_engagement_text_announcement";
    }
}
