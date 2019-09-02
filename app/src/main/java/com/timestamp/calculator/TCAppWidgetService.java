package com.timestamp.calculator;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class TCAppWidgetService extends Service {

	SharedPreferences sharedPref;
	Notification nt;
	TimeUpdater h;

	private BroadcastReceiver disabledReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent intent) {
			unregisterDisabledReceiver();
			stopSelf();
		}
		
	};

	private BroadcastReceiver disabledReceiver_hex = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctxt, Intent intent) {
			unregisterDisabledReceiver();
			stopSelf();
		}

	};

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // FIXME Notification must be added before using a Service
			nt = new Notification.Builder(this).setContentTitle(this.getString(R.string.app_name)).setSmallIcon(R.drawable.ic_launcher).setOngoing(true).setChannelId("com.timestamp.calculator").build();
			startForeground(1, nt);
		} else nt = new Notification(R.drawable.ic_launcher, getString(R.string.app_name), System.currentTimeMillis());
		h = new TimeUpdater(this, nt);


		if (h.hasMessages(0))
			h.removeMessages(0);
		h.sendEmptyMessage(0);
		registerDisabledReceiver();

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		h.unregisterDisabledReceiver();
		h.disappearNotifi();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void registerDisabledReceiver()
	{
		IntentFilter filter = new IntentFilter(getString(R.string.strWidgetSignalDisabled));
		IntentFilter filter_hex = new IntentFilter(getString(R.string.strHexWidgetSignalDisabled));
		registerReceiver(disabledReceiver, filter);
		registerReceiver(disabledReceiver_hex, filter_hex);
	}

	void unregisterDisabledReceiver()
	{  
		unregisterReceiver(disabledReceiver);
		unregisterReceiver(disabledReceiver_hex);
	}

}
