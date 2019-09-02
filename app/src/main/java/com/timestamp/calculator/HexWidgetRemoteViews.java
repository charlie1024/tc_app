package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;

import java.util.Locale;

/**
 * Created by JCH on 2017-09-03.
 * FIXME Workaround for slow UI issue #1
 */

public class HexWidgetRemoteViews extends RemoteViews {

    private AppWidgetManager awm;
    private ComponentName cn_hex;
    private PendingIntent piCpy_hex;
    private SharedPreferences sharedPref;
    private int wc_hex;

    private String pkgn;

    public HexWidgetRemoteViews(String packageName, int layoutId) {
        super(packageName, layoutId);
        pkgn = packageName;
    }

    public HexWidgetRemoteViews getComponentName(ComponentName cnv) {
        cn_hex = cnv;
        return this;
    }

    public HexWidgetRemoteViews getSharedPref(SharedPreferences sp) {
        sharedPref = sp;
        return this;
    }

    public HexWidgetRemoteViews getIntent(PendingIntent pii) {
        piCpy_hex = pii;
        return this;
    }

    public HexWidgetRemoteViews getAWM(AppWidgetManager awmv) {
        awm = awmv;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void update() {
        RemoteViews rv = new RemoteViews(pkgn, R.layout.widget_layout_hex);
        int fontSize = sharedPref.getInt(UtilityClass.k_fontSize, R.dimen.widget_size);
        if(wc_hex != awm.getAppWidgetIds(cn_hex).length) {
            rv.setOnClickPendingIntent(R.id.widget_textView_hex, null);
            rv.setOnClickPendingIntent(R.id.widget_textView_hex, piCpy_hex);
            wc_hex = awm.getAppWidgetIds(cn_hex).length;
            awm.updateAppWidget(cn_hex, this);
        }
        String tStr = Long.toHexString(System.currentTimeMillis() / 1000).toUpperCase(Locale.US);
        rv.setTextViewText(R.id.widget_textView_hex, tStr);
        rv.setFloat(R.id.widget_textView_hex, "setTextSize", fontSize);
        rv.setInt(R.id.layout_hex, "setBackgroundColor",
                UtilityClass.makeColor(sharedPref, UtilityClass.k_skBarValue, UtilityClass.k_savedBgColor));
        int savedfontColorNumber = sharedPref.getInt(UtilityClass.k_savedfontColor, 0xFFFFFFFF);
        rv.setTextColor(R.id.widget_textView_hex, savedfontColorNumber);
        rv.setOnClickPendingIntent(R.id.widget_textView_hex, null);
        rv.setOnClickPendingIntent(R.id.widget_textView_hex, piCpy_hex);
        awm.updateAppWidget(cn_hex, rv);
    }
}
