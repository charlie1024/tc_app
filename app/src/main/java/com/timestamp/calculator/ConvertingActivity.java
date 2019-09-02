package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/*
 * FIXME this ClipboardManager is deprecated; deprecated means that's too old to use properly
 * thus Eclipse recommends to use new    class(android.content.ClipboardManager)
 * but I targeted to old APIs as well, therefore deprecated methods are used.
 */
	
@SuppressWarnings("deprecation")
public class ConvertingActivity extends Activity {
	
	private CheckBox isGMTCkbx, usehD;
	protected SmartSpinner sp;
	
	protected boolean canCopy;
	protected Button btConvert;
	protected EditText FromText;
	protected TextView DestText;

	protected SharedPreferences sharedPref;

	  @Override
	    public void onCreate(final Bundle savedInstanceState) {
		  
	        // initialize view and settings
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.convert);
	     	
	        btConvert = (Button) findViewById(R.id.Convert);
			OnClickListener ocl_btSetTS_d = new OnClickListener() {

				public void onClick(View arg0) {
					setDatePickDialog();
				}

			};
			
			OnClickListener ocl_btSetTS_t = new OnClickListener() {

				public void onClick(View arg0) {
					setTimePickDialog();
				}

			};
			
			OnClickListener ocl_btConvert = new OnClickListener() {

				public void onClick(View arg0) {
					calcDatefromText(false);
				}
				
			};		
			
			OnLongClickListener olcl = new OnLongClickListener() {
				public boolean onLongClick(View arg0) {
					if (canCopy) {
						ClipboardManager cM = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						cM.setText(DestText.getText());
					}
					Toast.makeText(ConvertingActivity.this,
								canCopy ? R.string.copied : R.string.convertWarn, Toast.LENGTH_SHORT).show();
					return true;
				}	        	
			};
			
			OnLongClickListener olclTextBox = new OnLongClickListener() {
				
				public boolean onLongClick(View arg0) {
					EditText e = (EditText) arg0;
					
					if (e.length() != 0) {
						e.selectAll();
						ClipboardManager cM =
							(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						cM.setText(e.getText());
					}
					Toast.makeText(ConvertingActivity.this,
							e.length() != 0 ? R.string.copied : R.string.inputWarn, Toast.LENGTH_SHORT).show();
					return true;
					
				}
				
			};
			
			TextWatcher tw = new TextWatcher() {

				public void afterTextChanged(Editable arg0) {

					if (arg0.length() <= 0)
				        btConvert.setEnabled(false);
					else
						btConvert.setEnabled(true);

					/* FIXME Confirmation needed for crash or ANR */ 
					/*int k = sharedPref.getBoolean
							(UtilityClass.k_useHexaDecimal, false) ? 11 : 12;*/
					if (arg0.length() > 10)
						arg0.delete(10, arg0.length());
	
				}

				public void beforeTextChanged(CharSequence arg0, int arg1,
											  int arg2, int arg3) {
					// Not used

				}

				public void onTextChanged(CharSequence arg0 , int arg1,
										  int arg2, int arg3) {
					
					calcDatefromText(true);
					
				}

			};
			
	        FromText = (EditText) findViewById(R.id.inputtxt);
	        DestText = (TextView) findViewById(R.id.result);
	        isGMTCkbx = (CheckBox) findViewById(R.id.useGMT);
	        usehD = (CheckBox) findViewById(R.id.useHexaVal);
			sp = (SmartSpinner) findViewById(R.id.spinner);
	        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	        canCopy = false;
			btConvert.setEnabled(false);
	        btConvert.setOnClickListener(ocl_btConvert);
	        findViewById(R.id.setTS_d).setOnClickListener(ocl_btSetTS_d);	
	        findViewById(R.id.setTS_t).setOnClickListener(ocl_btSetTS_t);
	        DestText.setOnLongClickListener(olcl);
	        FromText.addTextChangedListener(tw);
			
			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
					{
						Editor e = sharedPref.edit();
						e.putInt("selectedNumber",  
								sp.getSelectedItemPosition());
						e.commit();
						calcDatefromText(true);
					}

					public void onNothingSelected(AdapterView<?> p1)
					{
						// Not used
					}
				
			});
			
	        FromText.setOnLongClickListener(olclTextBox);
			
			// Get preferences and initialize
			verifyhDCk();
	        verifyIMECk();
			setisGMT();
			setSpinners();
			
			/* FIXME To prevent bug, OnCheckedChangeListener must be here. */
			OnCheckedChangeListener occl = new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					Editor e = sharedPref.edit();
					boolean ifSaves = sharedPref.getBoolean
							(UtilityClass.k_ifSaves, false); 
					
					if (arg1) {
						if (ifSaves)	
							e.putBoolean(UtilityClass.k_isGMTinConvert2, true); 
					} else {
						if (ifSaves) 
							e.putBoolean(UtilityClass.k_isGMTinConvert2, false); 
					}
					
					// finally commit data
			        e.commit();
			        
					calcDatefromText(true);
				}

			};
			
			OnCheckedChangeListener occl_h = new OnCheckedChangeListener() {

				@TargetApi(Build.VERSION_CODES.CUPCAKE)
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					
					// Make InputFilter
					InputFilter[] filter;
					Editor e = sharedPref.edit();
                    String savedTs = FromText.getText().toString();

					if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false)) {
						e.putBoolean(UtilityClass.k_useHexaDecimal, false);
                        FromText.setInputType(InputType.TYPE_CLASS_PHONE);
						filter = UtilityClass.setInputFilter(UtilityClass.normalFilter);
						savedTs = String.valueOf(Long.parseLong(savedTs.length() > 0 ? savedTs : "0", 16));
					} else {
						e.putBoolean(UtilityClass.k_useHexaDecimal, true);
                        FromText.setInputType(InputType.TYPE_CLASS_TEXT);
						filter = UtilityClass.setInputFilter(UtilityClass.hexaFilter);
						savedTs = Long.toHexString(Long.valueOf(savedTs.length() > 0 ? savedTs : "0")).toUpperCase();
					}

                    FromText.setFilters(filter);
                    FromText.setText(savedTs);
							
					// finally commit data
			        e.commit();
			        
				}

			};
			
		    isGMTCkbx.setOnCheckedChangeListener(occl);
		    usehD.setOnCheckedChangeListener(occl_h);

		    if (savedInstanceState != null) {
		    	
				FromText.setText(savedInstanceState.getString(UtilityClass.CA_FromText));
				DestText.setText(savedInstanceState.getString(UtilityClass.CA_DestText));
				canCopy = savedInstanceState.getBoolean(UtilityClass.CA_canCopy);

		    }

	  }
		
		@Override
		public void onResume() {
			super.onResume();
			Button btc = btConvert;

			if (btc != null) {
				
				if (sharedPref.getBoolean(UtilityClass.k_autoCalc, false))
					btc.setVisibility(View.GONE);
				else btc.setVisibility(View.VISIBLE);
				
			}

			setSpinners();
			setisGMT();
			verifyIMECk();
			
		    sp.evokeSelection();
		}

		/* make options menu, API >= 10(Gingerbread) : normal menu,
		 *  API >= 11(Honeycomb) : action bar appeared.(non-Javadoc)
		 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
		 */
		   
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.convert, menu);
	        return true;
	    }
	    
		// onKeyDown() : If menu key pressed, soft keyboard will disappeared.
		
	    @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			
	    	UtilityClass.disappearKeyboard(this, FromText, keyCode);
	    	return super.onKeyDown(keyCode, event);
			
	    }
	    
		/* commit to finish activity when back item selected
	     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	     */
		 
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {	
	    	
			Intent intent;
			
			switch (item.getItemId()) {
				case R.id.menu_back:
					finish();
					break;
					
				case R.id.menu_settings_cvt:
					// make difference against MainActivity's
					intent = new Intent(this, SettingsActivity.class);
					startActivity(intent);
					break;
					
				case R.id.menu_getnow:
					long nowTS = (System.currentTimeMillis()) / 1000;
					String TimestampS;
					
					if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false))
						TimestampS = Long.toString(nowTS, 16).toUpperCase(Locale.US);
					else
						TimestampS = Long.toString(nowTS);
					
					FromText.setText(TimestampS);
					break;	
					
				case R.id.menu_mngFormat:
					intent = new Intent(this, FormatSettingActivity.class);
					startActivity(intent);
					break;
					
				case R.id.menu_pasteFromClipboard:
					ClipboardManager cM =
						(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					
					if (cM != null)
						FromText.setText(cM.getText());
					break;
					
				default:
				    break;
			}
			
			return super.onOptionsItemSelected(item);
			
		}
	    
		@Override
		public void onSaveInstanceState(Bundle outState) {
			
			super.onSaveInstanceState(outState);

			outState.putString(UtilityClass.CA_FromText, FromText.getText().toString());
			outState.putString(UtilityClass.CA_DestText, DestText.getText().toString());
			outState.putBoolean(UtilityClass.CA_canCopy, canCopy);

		}
		
		// setisGMT() : Initialize regards whether Save checkbox was checked
		private void setisGMT() {

			//initialize settings saved
			if (sharedPref.getBoolean(UtilityClass.k_isGMTinConvert2, false)
					&& sharedPref.getBoolean(UtilityClass.k_ifSaves, false)) 
				isGMTCkbx.setChecked(true);
				
		}
		
		// verifyIMECk() : This works with confirming IME window shows or not shows.
		protected void verifyIMECk() {

			if (sharedPref.getBoolean(UtilityClass.k_ifIMEShows, true))
				FromText.requestFocus();
				UtilityClass.postKeyboard(this, FromText);	
			
		}
		
		// verifyhDCk() : Verify whether Hexadecimal used.
		@TargetApi(Build.VERSION_CODES.CUPCAKE)
		private void verifyhDCk() {
			boolean usehD_b = sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false);

			// Make InputFilter
			InputFilter[] filter;
			
			if (usehD_b) {
				filter = UtilityClass.setInputFilter(UtilityClass.hexaFilter);
				FromText.setInputType(InputType.TYPE_CLASS_TEXT);
			} else {
				filter = UtilityClass.setInputFilter(UtilityClass.normalFilter);
				FromText.setInputType(InputType.TYPE_CLASS_PHONE);
			}

			FromText.setFilters(filter);
			usehD.setChecked(usehD_b);
		}
		
		// setSpinners() : Initialize Spinners
		private void setSpinners() {
			
			ArrayList<String> array = new ArrayList<String> ();
			array.addAll(Arrays.asList(getResources().getStringArray(R.array.st_dateArray)));
			array.addAll(UtilityClass.setArrayList(this));
			// adapter : ArrayAdapter that connects string array and Spinner
			ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
					android.R.layout.simple_spinner_item, array);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapter.setNotifyOnChange(true);
			sp.setAdapter(adapter);
			
		}
		
		// setDatePickDialog() : Shows DatePicker Dialog.
		protected void setDatePickDialog() {
			DatePickerDialog dtDialog;
			String l_cd_bf_s = FromText.getText().toString();
			final Calendar CD = Calendar.getInstance();
			long l_cd;
			
			OnDateSetListener odsl = new OnDateSetListener() {

				public void onDateSet(DatePicker arg0, int arg1, int arg2,
									  int arg3) {
					CD.set(Calendar.YEAR, arg1);
					CD.set(Calendar.MONTH, arg2);
					CD.set(Calendar.DAY_OF_MONTH, arg3);
					long returnval = CD.getTimeInMillis();
					String returnval_S;

					if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false))
						returnval_S = Long.toString(returnval / 1000, 16);
					else
						returnval_S = Long.toString(returnval / 1000);
					
					FromText.setText(returnval_S.toUpperCase(Locale.US));
					verifyIMECk();
				}

			};
			
			if (l_cd_bf_s.equals("")) 
				
				l_cd = 0;
			
			else {

				Long l_cd_bf;
				
				if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false))
					l_cd_bf = Long.valueOf(l_cd_bf_s, 16);
				else
					l_cd_bf = Long.valueOf(l_cd_bf_s);
				
				l_cd = l_cd_bf * 1000;
				
			}
			
			CD.setLenient(true);
			CD.setTimeInMillis(l_cd);
			
			dtDialog = new DatePickerDialog(this, odsl, 
					CD.get(Calendar.YEAR), CD.get(Calendar.MONTH), CD.get(Calendar.DAY_OF_MONTH));
			dtDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_UserWindowsDialogs;
			dtDialog.show();
		}
		
		// setTimePickDialog() : Shows TimePicker Dialog.
		protected void setTimePickDialog() {
			TimePickerDialog tDialog;
			// AMPMNeeded : Preference checker(if use AMPM or 24h time system).
			boolean AMPMnotNeeded = sharedPref.getBoolean(UtilityClass.k_NotuseAMPM, false);
			String l_cd_bf_s = FromText.getText().toString();
			final Calendar CD = Calendar.getInstance();	
			long l_cd;
			
			OnTimeSetListener otsl = new OnTimeSetListener() {

				public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
					CD.set(Calendar.HOUR_OF_DAY, arg1);
					CD.set(Calendar.MINUTE, arg2);

					long returnval = CD.getTimeInMillis();
					String returnval_S;

					if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false))
						returnval_S = Long.toString(returnval / 1000, 16);
					else
						returnval_S = Long.toString(returnval / 1000);

					FromText.setText(returnval_S.toUpperCase(Locale.US));
					verifyIMECk();
				}

			};
			
			if (l_cd_bf_s.equals("")) 
				
				l_cd = 0;
			
			else {

				Long l_cd_bf;
				
				if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false))
					l_cd_bf = Long.valueOf(l_cd_bf_s, 16);
				else
					l_cd_bf = Long.valueOf(l_cd_bf_s);
				
				l_cd = l_cd_bf * 1000;
			
			}
			
			CD.setLenient(true);
			CD.setTimeInMillis(l_cd);
			
			tDialog = new TimePickerDialog(this, otsl,
					CD.get(Calendar.HOUR_OF_DAY), CD.get(Calendar.MINUTE), AMPMnotNeeded);
			tDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_UserWindowsDialogs;
			tDialog.show();
		}
	  
		public void calcDatefromText(boolean autoCalcable) {

			if (!sharedPref.getBoolean(UtilityClass.k_autoCalc, false)
					&& autoCalcable) return;

			boolean ifShowTZ =
					sharedPref.getBoolean(UtilityClass.k_ifShowTZ, false);
			Format fm;
			String stR = null, fmstr;
			fmstr = sp.getItemAtPosition
					(sp.getSelectedItemPosition()).toString();
			long l;
			try {

				if (sharedPref.getBoolean(UtilityClass.k_useHexaDecimal, false))
					l = Long.parseLong(
							FromText.getText().toString().toUpperCase(Locale.US), 16) * 1000;
				else
					l = Long.parseLong(
								 FromText.getText().toString()) * 1000;
				
			} catch (NumberFormatException e) {
				l = 0;
			}
			
			if (isGMTCkbx.isChecked()) {

				l -= TimeZone.getDefault().getRawOffset();
				fm = new SimpleDateFormat(fmstr, Locale.US);
				stR = fm.format(l);
				if (ifShowTZ)
					stR += " GMT"; 
				
			} else {

				try {
					
					if (ifShowTZ)
						fm = new SimpleDateFormat((fmstr + " z"), Locale.getDefault()); 
					else
						fm = new SimpleDateFormat(fmstr, Locale.getDefault());
					
					stR = fm.format(l);
					
				} catch (IllegalArgumentException e) {
					
					Toast toast = Toast.makeText(ConvertingActivity.this, 
							R.string.CannotConvert, Toast.LENGTH_SHORT);

					toast.show();
					
				}
				
			}

			if (stR != null)
				DestText.setText(stR);
			canCopy = true;
		  
	  }

}
