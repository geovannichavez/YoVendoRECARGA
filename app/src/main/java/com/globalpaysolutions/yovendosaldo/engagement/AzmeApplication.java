package com.globalpaysolutions.yovendosaldo.engagement;

import android.content.Intent;

import com.microsoft.azure.engagement.EngagementAgent;
import com.microsoft.azure.engagement.EngagementApplication;
import com.microsoft.azure.engagement.EngagementConfiguration;
import com.microsoft.azure.engagement.reach.EngagementReachAgent;

/**
 * Created by Josué Chávez on 08/08/2016.
 */
public final class AzmeApplication extends EngagementApplication
{

    @Override
    public void onApplicationProcessCreate()
    {
        final EngagementConfiguration engagementConfiguration = new EngagementConfiguration();
        engagementConfiguration.setConnectionString("Endpoint=CEOAnalyticsYVS.device.mobileengagement.windows.net;SdkKey=705ef5ca3c645a0af96997df0becbffe;AppId=cua000312");
        EngagementAgent.getInstance(this).init(engagementConfiguration);

        final EngagementReachAgent reachAgent = EngagementReachAgent.getInstance(this);
        reachAgent.registerNotifier(new AzmeNotifier(this), Intent.CATEGORY_DEFAULT);
    }
}
