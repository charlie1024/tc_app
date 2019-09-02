package com.timestamp.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Created by JCH on 2017-09-02.
 * FIXME BUG
 * Multiple Service Running will result in collision with App Widget Service
 * This BUG was discovered when I tried to add 'Hex Timestamp Widget'.
 * The workaround for this issue is integrating the ability to single service.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int val = sharedPref.getInt(TCAppWidgetProvider.WIDGETVAL, 0);
        if (val >= 1 && !UtilityClass.isMyServiceRunning(context, TCAppWidgetService.class)) {
            Intent mainIntent = new Intent(context, TCAppWidgetService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mainIntent);
            } else context.startService(mainIntent);
        }
    }
}
