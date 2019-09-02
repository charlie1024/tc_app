package com.timestamp.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Spinner;

public class SmartSpinner extends Spinner {

	SharedPreferences sharedPref;
	
	public SmartSpinner(Context context) {
		
		super(context);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

	}
	
	public SmartSpinner(Context context, AttributeSet set) {
		
		super(context, set);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

	}
	
	public void evokeSelection() {

		int selectedNumber = sharedPref.getInt("selectedNumber", 0);
		
		if (selectedNumber >= getCount()) selectedNumber = 0;
		if (selectedNumber != 0 && getCount() >= selectedNumber)
			setSelection(selectedNumber);
		
	}
	
}
