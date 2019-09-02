package com.timestamp.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class FormatSettingActivity extends Activity {

	protected CheckBoxListLayoutAdapter adapter;
	protected ArrayList<String> list;
	protected Context context;
	protected ListView listView;
	protected TextView nDTextView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formats);
		context = getApplicationContext();
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				makeMenuDialog(adapter.getItemString(arg2), arg2);
				return false;
			}
			
		});
		list = UtilityClass.setArrayList(context);

		adapter = new CheckBoxListLayoutAdapter();
		adapter.insertItems(list);
		listView.setAdapter(adapter);

		nDTextView = (TextView) findViewById(R.id.nDTextView);

		if (listView.getCount() == 0)
			nDTextView.setVisibility(View.VISIBLE);
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.formats, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
			case R.id.menu_additem:
				makeETDialog();
				break;
				
			case R.id.menu_deleteAll:
			case R.id.menu_deleteChecked:
				if (listView.getCount() != 0)
					if (item.getItemId() == R.id.menu_deleteAll)
						confirmDeleteAll(); else confirmDeleteChecked();
				else {
					Builder dg = new AlertDialog.Builder(this);
					
					dg.setTitle(item.getItemId() == R.id.menu_deleteAll ? R.string.menu_deleteAll : R.string.menu_deleteChecked)
					.setMessage(R.string.AlreadyClear)
					.setPositiveButton(android.R.string.ok, null);
					
					UtilityClass.ComposeDialog(dg);
				}

				break;
				
			case R.id.menu_back_format:
				finish();
				break;
				
			default:
				break;
				
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	
	// modifyETDialog() : Make custom dialog, pay attention to layout use.
	protected void modifyETDialog(final int n) {
		
		LayoutInflater LI = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View VIEW = LI.inflate(R.layout.input_itv, (ViewGroup) findViewById(R.id.lyout_dg));
		
		// explore EditText after dialog shown
		final EditText et = (EditText) VIEW.findViewById(R.id.inputtext);
		et.setText(adapter.getItemString(n));
		et.setSelection(et.getText().length());

		et.requestFocus();
		UtilityClass.postKeyboard(context, et);	
		Builder dg = new AlertDialog.Builder(this);

		dg.setTitle(R.string.menu_modifyitem)
		.setView(VIEW)
		.setCancelable(true)
		.setPositiveButton(android.R.string.ok, 
			new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					String txt = et.getText().toString().trim();
					
					if (!txt.equals("")) 
					{	
						for (int i = 0; i < adapter.getCount(); i++)
						{
							if (txt.equals(adapter.getItemString(n))) {
								Toast.makeText(context,
										R.string.duplicated, Toast.LENGTH_SHORT).show();
								return;
							}
						}
						
						UtilityClass.modifyDatabase(getApplicationContext(), adapter,
								txt, n);
						listView.setItemChecked(n, false);
					}
					
				} 

			})
		.setNegativeButton(android.R.string.cancel, null);
		UtilityClass.ComposeDialog(dg);
	}
	
	protected void makeMenuDialog(final String selectedStr, final int n) {
		
		Builder dg = new AlertDialog.Builder(this);
				
		dg.setTitle(R.string.mngFormat)
		.setItems(R.array.format_menuArray,
		new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which)
				{
				
					case 0:
						modifyETDialog(n);
						break;
						
					case 1:
						deleteOneItem(selectedStr, n);
						break;
						
					default:
						break;
						
				}
			}

		})
		.setNegativeButton(android.R.string.cancel, null);
		UtilityClass.ComposeDialog(dg);
		
	}
	
	protected void deleteOneItem(final String selectedStr, final int n) {
		
		Builder dg = new AlertDialog.Builder(this);
		
		dg.setTitle(R.string.menu_deleteThis)
		.setMessage(R.string.confirmDeleteThisString)
		.setPositiveButton(android.R.string.yes,
		new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				UtilityClass.DeletefromDatabase(getApplicationContext(), selectedStr);
				UtilityClass.DeletefromAdapter(adapter, selectedStr);
				if (listView.getCount() == 0)
					nDTextView.setVisibility(View.VISIBLE);
				listView.setItemChecked(n, false);
			}
			
		})
		.setNegativeButton(android.R.string.no, null);
		UtilityClass.ComposeDialog(dg);
	
	}

	protected void confirmDeleteChecked() {
		Builder dg = new AlertDialog.Builder(this);
		final SparseBooleanArray items = listView.getCheckedItemPositions();
		int count=0;
		for (int k=0; k<listView.getAdapter().getCount(); k++)
			if(items.get(k)) count++;
		if(count<=0) {
			dg.setTitle(R.string.NoItemSelectedCaption)
					.setMessage(R.string.NoItemSelected)
					.setPositiveButton(android.R.string.ok, null);
			UtilityClass.ComposeDialog(dg);
			return;
		}

		dg.setTitle(R.string.menu_deleteChecked)
				.setMessage(R.string.confirmDeleteThisString)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								int k;
								String itemstr;
								Log.e("TC", String.valueOf(items.size()));
								for (k=0; k<listView.getAdapter().getCount(); k++) {
									if (items.get(k)) {
										itemstr = ((CheckBoxListLayoutAdapter) listView.getAdapter()).getItemString(items.keyAt(k));
										Log.e("TC", String.valueOf(itemstr));
										UtilityClass.DeletefromDatabase(getApplicationContext(), itemstr);
										listView.setItemChecked(k, false);
									}
								}

								/* FIXME 20170301 workaround for issue
								   I tried to remove data and adapter dynamically,
								   but that failed.
								   So,
								     1. Delete dynamically from SQLite Database
								     2. Get New Context and ArrayList from "new" Database
								     3. Clear adapter and insert items.
								 */
								context = getApplicationContext();
								list = UtilityClass.setArrayList(context);
								adapter.clear();
								adapter.insertItems(list);
								adapter.notifyDataSetChanged();

								if (listView.getCount() == 0)
									nDTextView.setVisibility(View.VISIBLE);
							}

						})
				.setNegativeButton(android.R.string.no, null);
		UtilityClass.ComposeDialog(dg);
	}
	
	protected void confirmDeleteAll() {
		
		Builder dg = new AlertDialog.Builder(this);
		
		dg.setTitle(R.string.menu_deleteAll)
		.setMessage(R.string.confirmDeleteString)
		.setPositiveButton(android.R.string.yes,
		new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				for (String str : list)
					adapter.remove(str);
				UtilityClass.ClearDatabase(context,
						adapter);
				nDTextView.setVisibility(View.VISIBLE);
			}

		})
		.setNegativeButton(android.R.string.no, null);
		UtilityClass.ComposeDialog(dg);
		
	}
	
	// makeETDialog() : Make custom dialog, pay attention to layout use.
	private void makeETDialog() {

		// FIXME 20170301 workaround for probable cast issue
		if (listView.getCount() > 32767) {
			Toast.makeText(FormatSettingActivity.this, 
					R.string.strTooManyFormats, Toast.LENGTH_SHORT).show();
			return;
		}
			
		LayoutInflater LI = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View VIEW = LI.inflate(R.layout.input_itv, (ViewGroup) findViewById(R.id.lyout_dg));
		
		// explore EditText after dialog shown
		final EditText et = (EditText) VIEW.findViewById(R.id.inputtext);
		et.requestFocus();
		UtilityClass.postKeyboard(context, et);
		Builder dg = new AlertDialog.Builder(this);

		dg.setTitle(R.string.menu_additem)
		.setView(VIEW)
		.setCancelable(true)
		.setPositiveButton(android.R.string.ok, 
			new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					
					ListView l = listView;
					String txt = et.getText().toString().trim();
					int k = l.getCount();
					
					for (int i = 0; i < k; i++)
					{
						if (l.getCount() != 0) {
							if (txt.equals(adapter.getItemString(i))) {
								Toast.makeText(FormatSettingActivity.this, 
										R.string.duplicated, Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}

					if (!txt.equals("")) { 
						UtilityClass.AddtoDatabase(getApplicationContext(), txt);
						UtilityClass.AddtoAdapter(adapter, txt, l.getCount());
					}
					
					nDTextView.setVisibility(View.GONE);
					
				} 

			})
		.setNegativeButton(android.R.string.cancel, null);
		UtilityClass.ComposeDialog(dg);
		
	}
	
}
