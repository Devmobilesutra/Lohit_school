package com.mobilesutra.SMS_Lohit.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        MyApp.log("MyReceiver", "In on receive");
        Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
        intent1.putExtra("Flag", "SMS_data");
        context.startService(intent1);
    }
}
