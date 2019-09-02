package com.timestamp.calculator;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectFormActivity extends Activity {

	protected CheckBoxListLayoutAdapter adapter;
	protected ArrayList<String> list;
	protected Context context;
	protected ListView listView;
	protected TextView nDTextView;
	protected SharedPreferences sharedPref;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.formats);
		context = getApplicationContext();
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		listView = (ListView) findViewById(R.id.listView);
		list = UtilityClass.setArrayList(context);

		adapter = new CheckBoxListLayoutAdapter();
		adapter.insertItems(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.st_dateArray))));
		adapter.insertItems(list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String result = adapter.getItemString(position);
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				sharedPref.edit().putString(UtilityClass.k_ShowNotifiUserText, result).commit();
				finish();
			}

		});

		nDTextView = (TextView) findViewById(R.id.nDTextView);

		if (listView.getCount() == 0)
			nDTextView.setVisibility(View.VISIBLE);

	}

	@Override
	public void onBackPressed() {
		String str = "yyyy-MM-dd";
		sharedPref.edit().putString(UtilityClass.k_ShowNotifiUserText, str).commit();
		Toast toast = Toast.makeText(this,
				R.string.strNoSelectedForced, Toast.LENGTH_SHORT);
		toast.show();
		super.onBackPressed();
	}
	
}
