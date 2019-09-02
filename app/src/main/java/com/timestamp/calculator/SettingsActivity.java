package com.timestamp.calculator;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

public class SettingsActivity extends PreferenceActivity {

	protected int savedOpacityValue;
	protected int savedSizeValue;
	protected SharedPreferences sharedPref;

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        /*
		 * Initialize PreferenceScreen.
		 * These must be attentioned because these methods are
		 * deprecated, you must confirm 
		 * Also deprecated methods will affect app compatibility,
		 * If you want change these, I recommend you change 
		 * But regarding this, this app was constructed for API 5~,
		 * You must mark about 
		 */

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		final Preference ShowNotifiHex = (CheckBoxPreference) findPreference(UtilityClass.k_ShowNotifiHex);
		final Preference ShowNotifiUser = (CheckBoxPreference) findPreference(UtilityClass.k_ShowNotifiUser);

		ShowNotifiHex.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Editor e = sharedPref.edit();
						if (sharedPref.getBoolean(UtilityClass.k_ShowNotifiHex, false)) {
							e.putBoolean(UtilityClass.k_ShowNotifiUser, false);
							e.putString(UtilityClass.k_ShowNotifiUserText, "yyyy-MM-dd");
							e.commit();
							((CheckBoxPreference) ShowNotifiUser).setChecked(false);
						}
						return false;
					}
				});

		ShowNotifiUser.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Editor e = sharedPref.edit();
				Intent intent;
				if (sharedPref.getBoolean(UtilityClass.k_ShowNotifiUser, false)) {
					e.putBoolean(UtilityClass.k_ShowNotifiHex, false);
					e.commit();
					((CheckBoxPreference) ShowNotifiHex).setChecked(false);
					intent = new Intent(getApplicationContext(), SelectFormActivity.class);
					startActivity(intent);
				}
				return false;
			}
		});

		findPreference(UtilityClass.k_ShowNotifi).
				setOnPreferenceClickListener(new OnPreferenceClickListener() {

					 public boolean onPreferenceClick(Preference arg0) {
						 Editor e = sharedPref.edit();
						 int val = sharedPref.getInt(TCAppWidgetProvider.WIDGETVAL, 0);
						 if (!sharedPref.getBoolean(UtilityClass.k_ShowNotifi, false)) {
							 val = val - 1;
							 e.putInt(TCAppWidgetProvider.WIDGETVAL, val);
							 e.putInt(TimeUpdater.NOTIFIVAL, 0);
							 e.commit();
							 if (val <= 0) {
								 String str = SettingsActivity.this.getString(R.string.strWidgetSignalDisabled);
								 Intent disabledIntent = new Intent(str);
								 SettingsActivity.this.sendBroadcast(disabledIntent);
							 }
						 } else {
							 val = val + 1;
							 e.putInt(TCAppWidgetProvider.WIDGETVAL, val);
							 e.putInt(TimeUpdater.NOTIFIVAL, 1);
							 e.commit();
							 if (val >= 1 && !UtilityClass.isMyServiceRunning(SettingsActivity.this, TCAppWidgetService.class)) {
								 Intent mainIntent = new Intent(SettingsActivity.this, TCAppWidgetService.class);
								 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
									 startForegroundService(mainIntent);
								 } else startService(mainIntent);
							 }
						 }
						 return false;
					 }
				 }
		);

		findPreference(UtilityClass.k_skBarValue).
		setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {

				LayoutInflater inf = (LayoutInflater)
						getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inf.inflate(R.layout.seekbar_pref,
						(ViewGroup) findViewById(R.id.skbar_dg));
				final SeekBar skb = (SeekBar) layout.findViewById(R.id.skBar);
				final EditText skbET = (EditText) layout.findViewById(R.id.skBarValueTV);
				UtilityClass.postKeyboard(getApplicationContext(), skbET);
				UtilityClass.setSelectionforLastChar(skbET);

				TextWatcher tw = new TextWatcher() {

					public void afterTextChanged(Editable ed) {
						String str = skbET.getText().toString();
						int changedIntValue;

						try {
							changedIntValue = Integer.valueOf(str);
						} catch (NumberFormatException e) {
							changedIntValue = 1;
						}

						if (changedIntValue > 255) {
							changedIntValue = 255;
							skbET.setText(String.valueOf(changedIntValue));
						} else if (changedIntValue < 1) {
							changedIntValue = 1;
							skbET.setText(String.valueOf(changedIntValue));
						}

						UtilityClass.setSelectionforLastChar(skbET);
						skb.setProgress(changedIntValue);
					}

					public void beforeTextChanged(CharSequence c, int arg1,
							int arg2, int arg3) {
						// Not used
					}

					public void onTextChanged(CharSequence c, int arg1,
							int arg2, int arg3) {
						// Not used
					}

				};

				int homeVal = sharedPref.getInt(UtilityClass.k_skBarValue, 1);
				skb.setProgress(homeVal);
				skbET.setText(String.valueOf(homeVal));

				skbET.addTextChangedListener(tw);

				skb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar skbar, int skbarV,
							boolean bool) {
						savedOpacityValue = skbarV;
						String text = String.valueOf(skbarV);
						skbET.setText(text);
						UtilityClass.setSelectionforLastChar(skbET);
					}

					public void onStartTrackingTouch(SeekBar skbar) {
						// Not used.
					}

					public void onStopTrackingTouch(SeekBar skbar) {
						// Not used.
					}

				});

				Builder dg = new AlertDialog.Builder(SettingsActivity.this);
				dg.setTitle(R.string.skBarPrefTitle)
				.setView(layout)
				.setCancelable(true)
				.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg1, int arg2) {
							// Widget flash issue...
							if (savedOpacityValue < 1)
								savedOpacityValue = 1;

							Editor e = sharedPref.edit();
							e.putInt(UtilityClass.k_skBarValue,
									savedOpacityValue);
							e.commit();
						}
					})
				.setNegativeButton(android.R.string.cancel, null);

				UtilityClass.ComposeDialog(dg);

				return false;
			}

		});

		findPreference(UtilityClass.k_fontSize).
				setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference arg0) {

						LayoutInflater inf = (LayoutInflater)
								getSystemService(LAYOUT_INFLATER_SERVICE);
						View layout = inf.inflate(R.layout.seekbar_pref,
								(ViewGroup) findViewById(R.id.skbar_dg));
						final SeekBar skb = (SeekBar) layout.findViewById(R.id.skBar);
						final EditText skbET = (EditText) layout.findViewById(R.id.skBarValueTV);
						final TextView tvExample = (TextView) layout.findViewById(R.id.tvExample);
						UtilityClass.postKeyboard(getApplicationContext(), skbET);
						UtilityClass.setSelectionforLastChar(skbET);
						tvExample.setVisibility(View.VISIBLE);
						skb.setMax(96);

						TextWatcher tw = new TextWatcher() {

							public void afterTextChanged(Editable ed) {
								String str = skbET.getText().toString();
								int changedIntValue;

								try {
									changedIntValue = Integer.valueOf(str);
								} catch (NumberFormatException e) {
									changedIntValue = 0;
								}

								if (changedIntValue > 96) {
									changedIntValue = 96;
									skbET.setText(String.valueOf(changedIntValue));
								} else if (changedIntValue < 0) {
									changedIntValue = 0;
									skbET.setText(String.valueOf(changedIntValue));
								}

								UtilityClass.setSelectionforLastChar(skbET);
								skb.setProgress(changedIntValue);
								tvExample.setTextSize(changedIntValue);
							}

							public void beforeTextChanged(CharSequence c, int arg1,
														  int arg2, int arg3) {
								// Not used
							}

							public void onTextChanged(CharSequence c, int arg1,
													  int arg2, int arg3) {
								// Not used
							}

						};

						int homeVal = sharedPref.getInt(UtilityClass.k_fontSize, (int) (getResources().getDimension(R.dimen.widget_size) / getResources().getDisplayMetrics().density));
						skb.setProgress(homeVal);
						tvExample.setTextSize(homeVal);
						skbET.setText(String.valueOf(homeVal));
						skbET.addTextChangedListener(tw);

						skb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

							public void onProgressChanged(SeekBar skbar, int skbarV,
														  boolean bool) {
								savedSizeValue = skbarV;
								String text = String.valueOf(skbarV);
								skbET.setText(text);
								UtilityClass.setSelectionforLastChar(skbET);
							}

							public void onStartTrackingTouch(SeekBar skbar) {
								// Not used.
							}

							public void onStopTrackingTouch(SeekBar skbar) {
								// Not used.
							}

						});

						Builder dg = new AlertDialog.Builder(SettingsActivity.this);
						dg.setTitle(R.string.fontSize)
								.setView(layout)
								.setCancelable(true)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {

											public void onClick(DialogInterface arg1, int arg2) {
												Editor e = sharedPref.edit();
												e.putInt(UtilityClass.k_fontSize,
														savedSizeValue);
												e.commit();
											}
										})
								.setNegativeButton(android.R.string.cancel, null);

						UtilityClass.ComposeDialog(dg);

						return false;
					}

				});

		findPreference(UtilityClass.k_notifontSize).
				setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference arg0) {

						LayoutInflater inf = (LayoutInflater)
								getSystemService(LAYOUT_INFLATER_SERVICE);
						View layout = inf.inflate(R.layout.seekbar_pref,
								(ViewGroup) findViewById(R.id.skbar_dg));
						final SeekBar skb = (SeekBar) layout.findViewById(R.id.skBar);
						final EditText skbET = (EditText) layout.findViewById(R.id.skBarValueTV);
						final TextView tvExample = (TextView) layout.findViewById(R.id.tvExample);
						UtilityClass.postKeyboard(getApplicationContext(), skbET);
						UtilityClass.setSelectionforLastChar(skbET);
						tvExample.setVisibility(View.VISIBLE);
						skb.setMax(64);

						TextWatcher tw = new TextWatcher() {

							public void afterTextChanged(Editable ed) {
								String str = skbET.getText().toString();
								int changedIntValue;

								try {
									changedIntValue = Integer.valueOf(str);
								} catch (NumberFormatException e) {
									changedIntValue = 0;
								}

								if (changedIntValue > 64) {
									changedIntValue = 64;
									skbET.setText(String.valueOf(changedIntValue));
								} else if (changedIntValue < 0) {
									changedIntValue = 0;
									skbET.setText(String.valueOf(changedIntValue));
								}

								UtilityClass.setSelectionforLastChar(skbET);
								skb.setProgress(changedIntValue);
								tvExample.setTextSize(changedIntValue);
							}

							public void beforeTextChanged(CharSequence c, int arg1,
														  int arg2, int arg3) {
								// Not used
							}

							public void onTextChanged(CharSequence c, int arg1,
													  int arg2, int arg3) {
								// Not used
							}

						};

						int homeVal = sharedPref.getInt(UtilityClass.k_notifontSize, (int) (getResources().getDimension(R.dimen.widget_size) / getResources().getDisplayMetrics().density));
						skb.setProgress(homeVal);
						tvExample.setTextSize(homeVal);
						skbET.setText(String.valueOf(homeVal));
						skbET.addTextChangedListener(tw);

						skb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

							public void onProgressChanged(SeekBar skbar, int skbarV,
														  boolean bool) {
								savedSizeValue = skbarV;
								String text = String.valueOf(skbarV);
								skbET.setText(text);
								UtilityClass.setSelectionforLastChar(skbET);
							}

							public void onStartTrackingTouch(SeekBar skbar) {
								// Not used.
							}

							public void onStopTrackingTouch(SeekBar skbar) {
								// Not used.
							}

						});

						Builder dg = new AlertDialog.Builder(SettingsActivity.this);
						dg.setTitle(R.string.notifontSize)
								.setView(layout)
								.setCancelable(true)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {

											public void onClick(DialogInterface arg1, int arg2) {
												Editor e = sharedPref.edit();
												e.putInt(UtilityClass.k_notifontSize,
														savedSizeValue);
												e.commit();
											}
										})
								.setNegativeButton(android.R.string.cancel, null);

						UtilityClass.ComposeDialog(dg);

						return false;
					}

				});

		final String savedbgColorStr = UtilityClass.k_savedBgColor;

		OnPreferenceClickListener opcl = new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {
				AmbilWarnaDialog awd = new AmbilWarnaDialog(SettingsActivity.this,
						sharedPref.getInt(savedbgColorStr, 0xFF000000),
						new OnAmbilWarnaListener() {

							public void onCancel(AmbilWarnaDialog dialog) {
								// Not used
							}

							public void onOk(AmbilWarnaDialog dialog, int color) {
								Editor e = sharedPref.edit();
								e.putInt(savedbgColorStr, color);
								e.commit();
							}

				});

				awd.getDialog().getWindow().getAttributes().windowAnimations
				= R.style.Animations_UserWindowsDialogs;
				awd.show();

				return false;
			}};

			findPreference("bgcolorSettings").setOnPreferenceClickListener(opcl); 

		final String savedFontColorStr = UtilityClass.k_savedfontColor;

		OnPreferenceClickListener opcl_f = new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {
				AmbilWarnaDialog awd = new AmbilWarnaDialog(SettingsActivity.this,
						sharedPref.getInt(savedFontColorStr, 0xFFFFFFFF),
						new OnAmbilWarnaListener() {

					public void onCancel(AmbilWarnaDialog dialog) {
						// Not used
					}

					public void onOk(AmbilWarnaDialog dialog, int color) {
						Editor e = sharedPref.edit();
						e.putInt(savedFontColorStr, color);
						e.commit();
					}

				});
				awd.getDialog().getWindow().getAttributes().windowAnimations
				= R.style.Animations_UserWindowsDialogs;
				awd.show();

				return false;
			}};

		findPreference("fontcolorSettings").setOnPreferenceClickListener(opcl_f);

		final String n_savedbgColorStr = UtilityClass.k_n_savedBgColor;

		OnPreferenceClickListener n_opcl = new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {
				AmbilWarnaDialog awd = new AmbilWarnaDialog(SettingsActivity.this,
						sharedPref.getInt(n_savedbgColorStr, 0xFF000000),
						new OnAmbilWarnaListener() {

							public void onCancel(AmbilWarnaDialog dialog) {
								// Not used
							}

							public void onOk(AmbilWarnaDialog dialog, int color) {
								Editor e = sharedPref.edit();
								e.putInt(n_savedbgColorStr, color);
								e.commit();
							}

						});

				awd.getDialog().getWindow().getAttributes().windowAnimations
						= R.style.Animations_UserWindowsDialogs;
				awd.show();

				return false;
			}};

		findPreference("n_bgcolorSettings").setOnPreferenceClickListener(n_opcl);

		final String n_savedFontColorStr = UtilityClass.k_n_savedfontColor;

		OnPreferenceClickListener n_opcl_f = new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {
				AmbilWarnaDialog awd = new AmbilWarnaDialog(SettingsActivity.this,
						sharedPref.getInt(n_savedFontColorStr, 0xFFFFFFFF),
						new OnAmbilWarnaListener() {

							public void onCancel(AmbilWarnaDialog dialog) {
								// Not used
							}

							public void onOk(AmbilWarnaDialog dialog, int color) {
								Editor e = sharedPref.edit();
								e.putInt(n_savedFontColorStr, color);
								e.commit();
							}

						});
				awd.getDialog().getWindow().getAttributes().windowAnimations
						= R.style.Animations_UserWindowsDialogs;
				awd.show();

				return false;
			}};

		findPreference("n_fontcolorSettings").setOnPreferenceClickListener(n_opcl_f);

		findPreference(UtilityClass.k_n_skBarValue).
				setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference arg0) {

						LayoutInflater inf = (LayoutInflater)
								getSystemService(LAYOUT_INFLATER_SERVICE);
						View layout = inf.inflate(R.layout.seekbar_pref,
								(ViewGroup) findViewById(R.id.skbar_dg));
						final SeekBar skb = (SeekBar) layout.findViewById(R.id.skBar);
						final EditText skbET = (EditText) layout.findViewById(R.id.skBarValueTV);
						UtilityClass.postKeyboard(getApplicationContext(), skbET);
						UtilityClass.setSelectionforLastChar(skbET);

						TextWatcher tw = new TextWatcher() {

							public void afterTextChanged(Editable ed) {
								String str = skbET.getText().toString();
								int changedIntValue;

								try {
									changedIntValue = Integer.valueOf(str);
								} catch (NumberFormatException e) {
									changedIntValue = 1;
								}

								if (changedIntValue > 255) {
									changedIntValue = 255;
									skbET.setText(String.valueOf(changedIntValue));
								} else if (changedIntValue < 1) {
									changedIntValue = 1;
									skbET.setText(String.valueOf(changedIntValue));
								}

								UtilityClass.setSelectionforLastChar(skbET);
								skb.setProgress(changedIntValue);
							}

							public void beforeTextChanged(CharSequence c, int arg1,
														  int arg2, int arg3) {
								// Not used
							}

							public void onTextChanged(CharSequence c, int arg1,
													  int arg2, int arg3) {
								// Not used
							}

						};

						int homeVal = sharedPref.getInt(UtilityClass.k_n_skBarValue, 255);
						skb.setProgress(homeVal);
						skbET.setText(String.valueOf(homeVal));

						skbET.addTextChangedListener(tw);

						skb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

							public void onProgressChanged(SeekBar skbar, int skbarV,
														  boolean bool) {
								savedOpacityValue = skbarV;
								String text = String.valueOf(skbarV);
								skbET.setText(text);
								UtilityClass.setSelectionforLastChar(skbET);
							}

							public void onStartTrackingTouch(SeekBar skbar) {
								// Not used.
							}

							public void onStopTrackingTouch(SeekBar skbar) {
								// Not used.
							}

						});

						Builder dg = new AlertDialog.Builder(SettingsActivity.this);
						dg.setTitle(R.string.skBarPrefTitle)
								.setView(layout)
								.setCancelable(true)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {

											public void onClick(DialogInterface arg1, int arg2) {
												// Widget flash issue...
												if (savedOpacityValue < 1)
													savedOpacityValue = 1;

												Editor e = sharedPref.edit();
												e.putInt(UtilityClass.k_n_skBarValue,
														savedOpacityValue);
												e.commit();
											}
										})
								.setNegativeButton(android.R.string.cancel, null);

						UtilityClass.ComposeDialog(dg);

						return false;
					}

				});

		OnPreferenceClickListener opcl_v = new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {
                Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (sharedPref.getBoolean(UtilityClass.k_VibMotorUse, false))
                    vibe.vibrate(50);
				return false;
			}};

		findPreference("VibMotorUse").setOnPreferenceClickListener(opcl_v);

	}

	// make options menu (same as ConvertingActivity)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    // commit to finish activity when back item selected
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_back_setting:
				finish();
				break;

			default:
			    break;
		}
		return super.onOptionsItemSelected(item);
	}

}
