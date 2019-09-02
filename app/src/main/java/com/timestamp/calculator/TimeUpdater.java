package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;

public class TimeUpdater extends Handler {

    private AppWidgetManager awm;
    private ComponentName cn;
    private ComponentName cn_hex;
    private int wc, wc_hex;

    private Context context;
    private SharedPreferences sharedPref;

    private ClipboardManager cM;
    private Vibrator vibe;
    private IntentFilter iF;
    private IntentFilter iF_hex;
    private Intent copy;
    private Intent copy_hex;
    private PendingIntent piCpy;
    private PendingIntent piCpy_hex;

    private NotificationManager nm;
    private Notification nt;
    private NotiRemoteViews nrv;
    private IntentFilter iF_N;
    private Intent copy_N;
    private PendingIntent piCpy_N;

    private WidgetRemoteViews rView;
    private HexWidgetRemoteViews rView_hex;
    private ArrayList<String> itemArray;

    static final String NOTIFIVAL = "NOTIFIVAL";

    BroadcastReceiver brCpy = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tStr = String.valueOf(System.currentTimeMillis() / 1000);

            cM.setText(tStr);
            Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
            if (sharedPref.getBoolean(UtilityClass.k_VibMotorUse, false))
                vibe.vibrate(50);
        }
    };

    BroadcastReceiver brCpy_hex = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tStr = Long.toHexString(System.currentTimeMillis() / 1000)
                    .toUpperCase(Locale.US);

            cM.setText(tStr);
            Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
            if (sharedPref.getBoolean(UtilityClass.k_VibMotorUse, false))
                vibe.vibrate(50);
        }
    };

    BroadcastReceiver brCpy_N = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tStr = nrv.getTimeString();

            cM.setText(tStr);
            Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
            if (sharedPref.getBoolean(UtilityClass.k_VibMotorUse, false))
                vibe.vibrate(50);
        }
    };

    void unregisterDisabledReceiver()
    {
        context.unregisterReceiver(brCpy);
        context.unregisterReceiver(brCpy_hex);
        context.unregisterReceiver(brCpy_N);
    }

    void disappearNotifi() {
        if(!(nm == null)) nm.cancelAll();
    }

    // SuppressWarning Public Class Usage
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public TimeUpdater(Context ctxt, Notification noti) {
        context = ctxt;
        awm = AppWidgetManager.getInstance(context);
        cn = new ComponentName(context, TCAppWidgetProvider.class);
        cn_hex = new ComponentName(context, TCAppHexWidgetProvider.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        cM = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // FIXME workaround 20170302 widget count bug -> unnecessary load
        SharedPreferences.Editor e = sharedPref.edit();
        wc = awm.getAppWidgetIds(cn).length;
        wc_hex = awm.getAppWidgetIds(cn_hex).length;
        int widgetCount = wc + wc_hex + sharedPref.getInt(TimeUpdater.NOTIFIVAL, 0);
        e.putInt(TCAppWidgetProvider.WIDGETVAL, widgetCount);
        e.commit();

        iF = new IntentFilter("com.timestamp.calculator.COPY_DECI");
        context.registerReceiver(brCpy, iF);
        copy = new Intent("com.timestamp.calculator.COPY_DECI");
        piCpy = PendingIntent.getBroadcast(context, 0, copy, PendingIntent.FLAG_CANCEL_CURRENT);
        iF_hex = new IntentFilter("com.timestamp.calculator.COPY_HEX");
        context.registerReceiver(brCpy_hex, iF_hex);
        copy_hex = new Intent("com.timestamp.calculator.COPY_HEX");
        piCpy_hex = PendingIntent.getBroadcast(context, 0, copy_hex, PendingIntent.FLAG_CANCEL_CURRENT);

        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // FIXME Oreo / P Bug
            if (nm.getNotificationChannel("com.timestamp.calculator") != null)
                nm.deleteNotificationChannel("com.timestamp.calculator");
            NotificationChannel nch = new NotificationChannel("com.timestamp.calculator", "NOTIFCATION", NotificationManager.IMPORTANCE_LOW);
            nch.enableVibration(true);
            nch.setVibrationPattern(new long[]{ 0 });
            nch.setShowBadge(false);
            nm.createNotificationChannel(nch);
        }
        nt = noti;
        nrv = new NotiRemoteViews(context.getPackageName(), R.layout.notifi_layout);
        iF_N = new IntentFilter("com.timestamp.calculator.COPY_DECI_NOTI");
        context.registerReceiver(brCpy_N, iF_N);
        copy_N = new Intent("com.timestamp.calculator.COPY_DECI_NOTI");
        piCpy_N = PendingIntent.getBroadcast(context, 0, copy_N, PendingIntent.FLAG_UPDATE_CURRENT);
        nrv.setOnClickPendingIntent(R.id.notifi_textView, piCpy_N);

        rView = new WidgetRemoteViews(context.getPackageName(), R.layout.widget_layout);
        rView.getAWM(awm).getComponentName(cn).getIntent(piCpy).setSharedPref(sharedPref);

        rView_hex = new HexWidgetRemoteViews(context.getPackageName(), R.layout.widget_layout_hex);
        rView_hex.getAWM(awm).getComponentName(cn_hex).getIntent(piCpy_hex).getSharedPref(sharedPref);
        itemArray = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.st_dateArray)));
        itemArray.addAll(UtilityClass.setArrayList(context));
        nrv.setPreference(sharedPref).getIntent(piCpy_N);
        nrv.setItemArray(itemArray);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public void handleMessage(Message msg)
    {
        long t = System.currentTimeMillis(), offset;
        if ((sharedPref.getInt(NOTIFIVAL, 0) == 0)) {
            nm.cancelAll();
        } else {
            nt.contentView = nrv.update();
            nm.notify(1, nt);
            if (awm.getAppWidgetIds(cn).length + awm.getAppWidgetIds(cn_hex).length <= 0) {
                offset = System.currentTimeMillis() - t;
                sendEmptyMessageDelayed(0, 1000 - offset);
                return;
            }
        }

        if (sharedPref.getInt(TCAppWidgetProvider.WIDGETVAL, 0) == 0) {
            removeMessages(0);
            return;
        }

        rView.update();
        rView_hex.update();

        offset = System.currentTimeMillis() - t;
        sendEmptyMessageDelayed(0, 1000 - offset);
    }
}
