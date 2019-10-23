package com.mobilesutra.SMS_Lohit.config;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Ganesh Borse on 04-04-2017.
 */
public class BroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MyApp.log("In BroadcastReceiver");
        MyApp.log("Intent Action->"+intent.getAction().toString());
        if(intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION") || intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE"))
        {
            ComponentName comp = new ComponentName(context.getPackageName(),School_Service.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
        else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            // Now to check if we're actually connected
            if(cm!=null)
            {
                if(cm!=null)
                {
                    if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                        MyApp.log("BroadcastReceiver", "Connection ON");
                        Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                        intent1.putExtra("Flag", "Connection");
                        intent1.putExtra("Status", "ON");
                        context.startService(intent1);
                    }
                    else
                    {
                        MyApp.log("BroadcastReceiver", "Connection OFF");
                        Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                        intent1.putExtra("Flag", "UpdateMaster");
                        intent1.putExtra("Status", "OFF");
                        context.startService(intent1);
                    }
                }
            }
        }

    }
}
