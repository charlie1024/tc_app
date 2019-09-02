package com.timestamp.calculator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.TimeZone;
 
/* 
 * FIXME 20170301
 */

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	
	protected boolean canCopy = false;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (sharedPref != null) {
				
				boolean ifShowTimeZone = sharedPref.getBoolean
						(UtilityClass.k_ifShowTZ, false);
			
				if (ifShowTimeZone) {
					TimezoneNow.setVisibility(View.VISIBLE);
					isGMTCkbx.setVisibility(View.VISIBLE);
				} else {
					TimezoneNow.setVisibility(View.GONE);
					isGMTCkbx.setVisibility(View.GONE);
				}
				
			}
			
		}
		
	};

	protected CheckBox isGMTCkbx;
	protected SharedPreferences sharedPref;
	protected TextView TimestampNow;
	protected TextView TimezoneNow;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        // initialize content and find views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		// Get prefs and initialize root of application    
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// is DB Ready?
		if (sharedPref.getBoolean(UtilityClass.k_dbIsNotSet, true))
			UtilityClass.AddtoDatabase(this, "hh:mm:ss a");
		
		// for Preference Data update
		try {
			sharedPref.getBoolean(UtilityClass.k_isGMTinConvert, false);
			sharedPref.getBoolean(UtilityClass.k_isGMTinConvert2, false);
		} catch (ClassCastException err) {
			Editor e = sharedPref.edit();
			e.remove(UtilityClass.k_isGMTinConvert);
			e.remove(UtilityClass.k_isGMTinConvert2);
			e.putBoolean(UtilityClass.k_isGMTinConvert, false);
			e.putBoolean(UtilityClass.k_isGMTinConvert2, false);
			e.commit();
		}
		
		// deprecated setting remove!
		if (sharedPref.contains(UtilityClass.k_ShowToolTip)) 
			sharedPref.edit().remove(UtilityClass.k_ShowToolTip).commit();

        // new setting added fontsize
        if (!sharedPref.contains(UtilityClass.k_fontSize))
            sharedPref.edit().putInt(UtilityClass.k_fontSize, (int) (getResources().getDimension(R.dimen.widget_size) / getResources().getDisplayMetrics().density)).commit();

		if (!sharedPref.contains(UtilityClass.k_notifontSize))
			sharedPref.edit().putInt(UtilityClass.k_notifontSize, (int) (getResources().getDimension(R.dimen.widget_size) / getResources().getDisplayMetrics().density)).commit();

		// new setting added 2 shownotifi -> all added allin1
		if (!sharedPref.contains(UtilityClass.k_ShowNotifi)) {
			Editor e = sharedPref.edit();
			e.putBoolean(UtilityClass.k_ShowNotifi, false);
			e.putBoolean(UtilityClass.k_ShowNotifiHex, false);
			e.putBoolean(UtilityClass.k_ShowNotifiUser, false);
			e.putString(UtilityClass.k_ShowNotifiUserText, "yyyy-MM-dd");
			e.putInt(UtilityClass.k_n_skBarValue, 255);
			e.putInt(UtilityClass.k_n_savedBgColor, 0xFF000000);
			e.putInt(UtilityClass.k_n_savedfontColor, 0xFFFFFFFF);
			e.commit();
		}

		// First run check.
		if (sharedPref.getInt(UtilityClass.k_IsFirstRun, -2) == -2) {
			Editor e = sharedPref.edit();
			e.clear();
			e.putInt(UtilityClass.k_IsFirstRun, 0); 
			e.putBoolean(UtilityClass.k_isGMTinConvert, false); 
			e.putBoolean(UtilityClass.k_isGMTinConvert2, false);
			e.putInt(UtilityClass.k_skBarValue, 1); 
			e.putInt(UtilityClass.k_savedBgColor, 0xFF000000); 
			e.putInt(UtilityClass.k_savedfontColor, 0xFFFFFFFF);
			e.putBoolean(UtilityClass.k_ifSaves, false); 
			e.putBoolean(UtilityClass.k_ifShowTZ, false);
			e.putBoolean(UtilityClass.k_ifIMEShows, true); 
			e.putBoolean(UtilityClass.k_NotuseAMPM, false); 
			e.putBoolean(UtilityClass.k_useHexaDecimal, false);
			e.putBoolean(UtilityClass.k_dbIsNotSet, false); 
			e.putBoolean(UtilityClass.k_autoCalc, false);
			e.putBoolean(UtilityClass.k_VibMotorUse, false);
			e.commit();

			Intent mStartActivity = new Intent(this, MainActivity.class);
			int mPendingIntentId = 987654;
			PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
			Toast.makeText(this, R.string.strFirstRunAfterSetup, Toast.LENGTH_SHORT).show();
			finish();
		}

		initializeViews();
		setisGMT();
		startFirstRunBoot();
		
		if (savedInstanceState != null) {
			TimestampNow.setText(savedInstanceState.getString(UtilityClass.MA_TimeStamp));
			TimezoneNow.setText(savedInstanceState.getString(UtilityClass.MA_TimeZone));
			canCopy = savedInstanceState.getBoolean(UtilityClass.MA_canCopy);
		}
		
    }
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver();
	}

	// no catch expression because this won't fail anytime.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// declared as a local variable, reduces useless code
		Intent intent = null;
		
		switch (item.getItemId()) {
			case R.id.menu_about:
			    // this is not an independent activity, just a dialog
				intent = new Intent(this, AboutActivity.class);
				break;
				
			case R.id.menu_gotoconvert:
				intent = new Intent(this, ConvertingActivity.class);
				break;
				
			case R.id.menu_settings:
				intent = new Intent(this, SettingsActivity.class);
				break;
				
			case R.id.menu_help:
				intent = new Intent(this, HelpActivity.class);
				break;
				
			case R.id.menu_exit:
				finish();
				break;
				
			default:
				break;
		}
		
		if (intent != null)
			startActivity(intent);

		// must-needed because returning value affects to selected item
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);

		outState.putString(UtilityClass.MA_TimeStamp, TimestampNow.getText().toString());
		outState.putString(UtilityClass.MA_TimeZone, TimezoneNow.getText().toString());
		outState.putBoolean(UtilityClass.MA_canCopy, canCopy);
		
	}

	protected String TzShow() {
		
		String ret;

		if(isGMTCkbx.isChecked()) 
			ret = TimeZone.getTimeZone(Integer.toString(0)).getDisplayName();
		 else
			ret = TimeZone.getDefault().getDisplayName();
		
		return ret;
	}
    
	private void setisGMT() {

		//initialize settings saved
	    if (sharedPref.getBoolean(UtilityClass.k_isGMTinConvert, false) 
	    		&& sharedPref.getBoolean(UtilityClass.k_ifSaves, false))
	    	isGMTCkbx.setChecked(true);
	    
	}
	
	// initializeViews() : initialize Views.
	private void initializeViews() {

        TimestampNow = (TextView) findViewById(R.id.timestamps);
		TimezoneNow = (TextView) findViewById(R.id.timezones);
        isGMTCkbx = (CheckBox) findViewById(R.id.isGMT);

		Intent intent = new Intent(getString(R.string.strPrefChangedSignal));
		sendBroadcast(intent);
        
		// normal timestamp getter
		OnClickListener ocl_getNow = new OnClickListener() {

			public void onClick(View arg0) {
				long t = System.currentTimeMillis();
				if (isGMTCkbx.isChecked()) t = t - TimeZone.getDefault().getOffset(t);

				TimestampNow.setText(String.format(Locale.US, "%d", t / 1000)); // no problem, no character format
				TimezoneNow.setText(TzShow());

				canCopy = true;
			}

		};
		
		findViewById(R.id.getNow).setOnClickListener(ocl_getNow);

        // demical timestamp getter(must be careful that function has difference to normal)
		
		OnClickListener ocl_getNowD = new OnClickListener() {

			public void onClick(View arg0) {
				double t = System.currentTimeMillis();
				if (isGMTCkbx.isChecked()) t = t - TimeZone.getDefault().getOffset((long) t);

				TimestampNow.setText(String.format(Locale.US, "%.3f", t / 1000.)); // no problem, no character format
				TimezoneNow.setText(TzShow());
				canCopy = true;
			}
			
		};
		
		findViewById(R.id.getNowDemical).setOnClickListener(ocl_getNowD);

        // double timestamp getter(must be careful that function has difference to normal)
		
		OnClickListener ocl_getNowDbl = new OnClickListener() {

			public void onClick(View arg0) {
				long t = System.currentTimeMillis();
				if (isGMTCkbx.isChecked()) t = t - TimeZone.getDefault().getOffset(t);

				TimestampNow.setText(
						Long.toHexString(t / 1000).toUpperCase(Locale.US));
				TimezoneNow.setText(TzShow());

				canCopy = true;
			}
			
		};
		
		findViewById(R.id.getNowhD).setOnClickListener(ocl_getNowDbl);

        // textview long-click listener
		
		OnLongClickListener olcl_TimestampNow = new OnLongClickListener() {

			public boolean onLongClick(View arg0) {
				if (canCopy) {
					ClipboardManager cM =
						(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					cM.setText(TimestampNow.getText()); 
					Toast.makeText(MainActivity.this, 
								   R.string.copied, Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(MainActivity.this, 
								   R.string.retryWarn, Toast.LENGTH_SHORT).show();
				return true;
			}
			
		};
		
        TimestampNow.setOnLongClickListener(olcl_TimestampNow);
		
		// checkbox change listener
		
		OnCheckedChangeListener occl = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

				Editor e = sharedPref.edit();
				boolean ifSaves = sharedPref.getBoolean(UtilityClass.k_ifSaves, false); 

				if (arg1) {
						if (ifSaves)
							e.putBoolean(UtilityClass.k_isGMTinConvert, true); 
				} else {
						if (ifSaves)
							e.putBoolean(UtilityClass.k_isGMTinConvert, false); 
				}

				// finally commit data
				e.commit();
			}

		};
		
	    isGMTCkbx.setOnCheckedChangeListener(occl);
	}

	private void registerReceiver()
	{
		IntentFilter filter = new IntentFilter(getString(R.string.strWidgetSignal));
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver()
	{  
		unregisterReceiver(receiver);
	}

	private void startFirstRunBoot() {
		int val = sharedPref.getInt(TCAppWidgetProvider.WIDGETVAL, 0);
		if (val >= 1 && !UtilityClass.isMyServiceRunning(this, TCAppWidgetService.class)) {
			Intent mainIntent = new Intent(this, TCAppWidgetService.class);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(mainIntent);
			} else startService(mainIntent);
		}
	}

}
