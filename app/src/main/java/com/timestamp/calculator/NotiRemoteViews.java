package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.CharArrayReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by JCH on 2017-09-03.
 * FIXME Workaround for slow UI issue #1
 */

public class NotiRemoteViews extends RemoteViews {

    private SharedPreferences sp;
    private PendingIntent piCpy_N;
    private String pkgn;
    private ArrayList<String> array;
    private String timestr;

    public NotiRemoteViews(String packageName, int layoutId) {
        super(packageName, layoutId);
        pkgn = packageName;
    }

    public NotiRemoteViews setPreference(SharedPreferences spv) {
        sp = spv;

        return this;
    }

    public NotiRemoteViews getIntent(PendingIntent pii) {
        piCpy_N = pii;
        return this;
    }

    public NotiRemoteViews setItemArray(ArrayList<String> lst) {
        array = lst;
        return this;
    }

    public String getTimeString() {
        return timestr;
    }

    private String calcDatefromText(long time, String fmt) {
        Format fm = new SimpleDateFormat(fmt, Locale.getDefault());
        return fm.format(time * 1000);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public RemoteViews update() {
        long time = System.currentTimeMillis() / 1000;
        String userItem = sp.getString(UtilityClass.k_ShowNotifiUserText, "yyyy-MM-dd");
        boolean hex = sp.getBoolean(UtilityClass.k_ShowNotifiHex, false);
        boolean user = sp.getBoolean(UtilityClass.k_ShowNotifiUser, false);
        if (hex) timestr = Long.toHexString(time).toUpperCase(Locale.US);
        else if (user) { timestr = calcDatefromText(time, userItem); }
        else timestr = String.valueOf(time);
        RemoteViews rv = new RemoteViews(pkgn, R.layout.notifi_layout);
        rv.setTextViewText(R.id.notifi_textView, timestr);
        rv.setTextColor(R.id.notifi_textView, sp.getInt(UtilityClass.k_n_savedfontColor, 0xFFFFFFFF));
        rv.setInt(R.id.notifi_layout, "setBackgroundColor",
                UtilityClass.makeColor(sp, UtilityClass.k_n_skBarValue, UtilityClass.k_n_savedBgColor));
        rv.setFloat(R.id.notifi_textView, "setTextSize", sp.getInt(UtilityClass.k_notifontSize, 0));
        rv.setOnClickPendingIntent(R.id.notifi_textView, null);
        rv.setOnClickPendingIntent(R.id.notifi_textView, piCpy_N);
        return rv;
    }
}
