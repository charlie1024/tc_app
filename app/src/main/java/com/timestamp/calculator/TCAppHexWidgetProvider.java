package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class TCAppHexWidgetProvider extends AppWidgetProvider {

    public static final String WIDGETVAL = "isWidgetAlive";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    @Override
    public void onEnabled(Context context) {

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), ".TCAppHexWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String action = intent.getAction();
        Editor e = sharedPref.edit();
        int val = sharedPref.getInt(WIDGETVAL, 0);
        Log.e("TC", String.valueOf(val));
        if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            val = val + 1;
            e.putInt(TCAppHexWidgetProvider.WIDGETVAL, val);
            if (val >= 1 && !UtilityClass.isMyServiceRunning(context, TCAppWidgetService.class)) {
                Intent mainIntent = new Intent(context, TCAppWidgetService.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(mainIntent);
                } else context.startService(mainIntent);
            }

        } else if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED)) {
            val = val - 1;
            e.putInt(TCAppHexWidgetProvider.WIDGETVAL, val);
            if (val <= 0) {
                String str = context.getString(R.string.strHexWidgetSignalDisabled);
                Intent disabledIntent = new Intent(str);
                context.sendBroadcast(disabledIntent);
            }
        }

        e.commit();

    }

}
