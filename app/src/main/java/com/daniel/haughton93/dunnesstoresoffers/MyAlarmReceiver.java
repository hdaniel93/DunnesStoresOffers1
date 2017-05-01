package com.daniel.haughton93.dunnesstoresoffers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by danie on 24/12/2016.
 */

public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CheckForNewOffersService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
