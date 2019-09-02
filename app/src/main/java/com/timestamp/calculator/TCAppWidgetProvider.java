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
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.preference.PreferenceManager;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class TCAppWidgetProvider extends AppWidgetProvider {
	
	public static final String WIDGETVAL = "isWidgetAlive";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	}

	@Override
	public void onEnabled(Context context) {  
		
		PackageManager pm = context.getPackageManager();
		
		pm.setComponentEnabledSetting(           
				new ComponentName(context.getPackageName(), ".TCAppWidgetProvider"),
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

		if (action != null && (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) || action.equals(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE))) {
			val = val + 1;
			e.putInt(TCAppWidgetProvider.WIDGETVAL, val);
			if (val >= 1 && !UtilityClass.isMyServiceRunning(context, TCAppWidgetService.class)) {
				Intent mainIntent = new Intent(context, TCAppWidgetService.class);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					context.startForegroundService(mainIntent);
				} else context.startService(mainIntent);
				//if(val == 1) { Intent itt = new Intent(context, SettingsActivity.class); context.startActivity(itt); }
			}
		} else if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED)) {
			val = val - 1;
			//Log.e("TC", String.valueOf(val));
			e.putInt(TCAppWidgetProvider.WIDGETVAL, val);
			if (val <= 0) {
				String str = context.getString(R.string.strWidgetSignalDisabled);
				Intent disabledIntent = new Intent(str);
				context.sendBroadcast(disabledIntent);
			}
		}

		e.commit();

	}

}
