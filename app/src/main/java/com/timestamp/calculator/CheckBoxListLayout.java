package com.timestamp.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckBoxListLayout extends RelativeLayout implements Checkable {

    public CheckBoxListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        CheckBox checkable = (CheckBox) findViewById(R.id.ckb);
        return checkable.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox checkable = (CheckBox) findViewById(R.id.ckb);
        if (checkable.isChecked() != checked)
            checkable.setChecked(checked);
    }

    @Override
    public void toggle() {
        CheckBox checkable = (CheckBox) findViewById(R.id.ckb);
        setChecked(!checkable.isChecked());
    }

}