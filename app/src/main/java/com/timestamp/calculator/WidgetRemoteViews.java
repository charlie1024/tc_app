package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;

/**
 * Created by JCH on 2017-09-03.
 * FIXME Workaround for slow UI issue #1
 */

public class WidgetRemoteViews extends RemoteViews {

    private AppWidgetManager awm;
    private ComponentName cn;
    private PendingIntent piCpy;
    private SharedPreferences sharedPref;
    private int wc;

    private String pkgn;

    public WidgetRemoteViews(String packageName, int layoutId) {
        super(packageName, layoutId);
        pkgn = packageName;
    }

    public WidgetRemoteViews getComponentName(ComponentName cnv) {
        cn = cnv;
        return this;
    }

    public WidgetRemoteViews setSharedPref(SharedPreferences sp) {
        sharedPref = sp;
        return this;
    }

    public WidgetRemoteViews getIntent(PendingIntent pii) {
        piCpy = pii;
        return this;
    }

    public WidgetRemoteViews getAWM(AppWidgetManager awmv) {
        awm = awmv;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void update() {
        RemoteViews rv = new RemoteViews(pkgn, R.layout.widget_layout);
        int fontSize = sharedPref.getInt(UtilityClass.k_fontSize, R.dimen.widget_size);
        if(wc != awm.getAppWidgetIds(cn).length) {
            rv.setOnClickPendingIntent(R.id.widget_textView, null);
            rv.setOnClickPendingIntent(R.id.widget_textView, piCpy);
            wc = awm.getAppWidgetIds(cn).length;
            awm.updateAppWidget(cn, this);
        }

        rv.setTextViewText(R.id.widget_textView, String.valueOf(System.currentTimeMillis() / 1000));
        rv.setFloat(R.id.widget_textView, "setTextSize", fontSize);
        rv.setInt(R.id.layout, "setBackgroundColor",
                UtilityClass.makeColor(sharedPref, UtilityClass.k_skBarValue, UtilityClass.k_savedBgColor));
        int savedfontColorNumber = sharedPref.getInt(UtilityClass.k_savedfontColor, 0xFFFFFFFF);
        rv.setTextColor(R.id.widget_textView, savedfontColorNumber);

        rv.setOnClickPendingIntent(R.id.widget_textView, null);
        rv.setOnClickPendingIntent(R.id.widget_textView, piCpy);

        awm.updateAppWidget(cn, rv);
    }

}
