package com.timestamp.calculator;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class UtilityClass
{
 
	public static final String[] COLNAME = {"_id", "UF"};  
	public static final String k_useHexaDecimal = "useHexaDecimal"; 
	public static final String k_NotuseAMPM = "NotuseAMPM"; 
	public static final String k_autoCalc = "autoCalc"; 
	public static final String k_ifSaves = "ifSaves"; 
	public static final String k_isGMTinConvert = "isGMTinConvert"; 
	public static final String k_isGMTinConvert2 = "isGMTinConvert2"; 
	public static final String k_ifIMEShows = "ifIMEShows"; 
	public static final String k_ifShowTZ = "ifShowTZ"; 
	public static final String k_dbIsNotSet = "dbIsNotSet"; 
	public static final String k_IsFirstRun = "IsFirstRun"; 
	public static final String k_ShowToolTip = "ShowToolTip";  // FIXME Deprecated
	public static final String k_VibMotorUse = "VibMotorUse";
	
	public static final String k_skBarValue = "skBarValue"; 
	public static final String k_savedBgColor = "savedBgColor"; 
	public static final String k_savedfontColor = "savedfontColor";
	public static final String k_fontSize = "fontSize";
	public static final String k_notifontSize = "notifontSize";

	public static final String k_ShowNotifi = "ShowNotifi";
	public static final String k_ShowNotifiHex = "ShowNotifiHex";
	public static final String k_ShowNotifiUser = "ShowNotifiUser";
    public static final String k_ShowNotifiUserText = "ShowNotifiUserText";
	public static final String k_n_skBarValue = "n_skBarValue";
	public static final String k_n_savedBgColor = "n_savedBgColor";
	public static final String k_n_savedfontColor = "n_savedfontColor";

	public static final String hexaFilter = "^[a-fA-F0-9]*$";
	public static final String normalFilter = "^[0-9]*$";
	//public static final String formatFilter = "^[HmsyMda:-]*$"; 
	
	public static final String MA_TimeStamp = "MainActivity_TimeStamp"; 
	public static final String MA_TimeZone = "MainActivity_TimeZone"; 
	public static final String MA_canCopy = "MainActivity_canCopy"; 
	public static final String CA_FromText = "ConvertingActivity_FromText"; 
	public static final String CA_DestText = "ConvertingActivity_DestText"; 
	public static final String CA_canCopy = "ConvertingActivity_canCopy";
	
	/**
	 * When textbox is selected, you'll be likely to move cursor
	 * the last character of textbox, rather than first character of the textbox.
	 * This function is very small, which will move cursor.
	 *
	 * @param et : EditText that you want to move cursor.
	 */

	protected static void setSelectionforLastChar(EditText et) {
		
		int textLength = et.getText().length();
		et.setSelection(textLength, textLength);
		
	}

	protected static void postKeyboard(final Context context, final EditText et) {

		new Handler().postDelayed(new Runnable() {

			@TargetApi(Build.VERSION_CODES.CUPCAKE)
			public void run() {

				InputMethodManager imm = (InputMethodManager) 
						context.getSystemService(Context.INPUT_METHOD_SERVICE);
				// pend event to show implicitly
				imm.showSoftInput(et,
						InputMethodManager.SHOW_IMPLICIT);

			}

		}, 150);
		
	}

	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	protected static void disappearKeyboard(final Context context, final EditText et, int keyCode) {
		
		InputMethodManager imm = (InputMethodManager) 
				context.getSystemService(Context.INPUT_METHOD_SERVICE);
		
    	if(KeyEvent.KEYCODE_MENU == keyCode && imm.isActive())
			// this code will run when soft keyboard is shown
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    	
	}

	protected static InputFilter[] setInputFilter(final String pattern) {
				
		// Make InputFilter 
	    InputFilter filter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start,
					int end, Spanned dest, int dstart, int dend) {
			
				final Pattern ps = Pattern.compile(pattern);
					
	            if (!ps.matcher(source).matches())
	                return "";  
	            
	            return null;
				
			}
	    };

	    return new InputFilter[] {filter};
		
	}

	protected static ArrayList<String> setArrayList(Context context) {
		
		SQLiteDatabase db = new SQLiteHelper(context).getWritableDatabase();
	    String sql = "SELECT COUNT(*) FROM " + SQLiteHelper.DBNAME; 
	    SQLiteStatement statement = db.compileStatement(sql);
	    long count = statement.simpleQueryForLong() - 1;
	    statement.close();

		Cursor c = db.query(SQLiteHelper.DBNAME, COLNAME, null, null, null, null, null);
		c.moveToFirst();
		String dbTrimming = ""; 
		String[] db_array;
	    
		while (!c.isAfterLast())
		{
			if (c.getPosition() < count)
				dbTrimming += (c.getString(1) + ';');
			else {
				dbTrimming += (c.getString(1));
				break;
			}
			
			c.moveToNext();
		}
		
		db_array = dbTrimming.split(";", c.getPosition() + 1); 
		
		ArrayList<String> array = new ArrayList <String>();
		
		// If DB Data is null, must not added.
		if (!dbTrimming.equals("")) 
			array.addAll(Arrays.asList(db_array));
			
		c.close();
		db.close();
		
		return array;
		
	}

	protected static void AddtoAdapter(CheckBoxListLayoutAdapter adapter, String name, int number) {
		
		if (adapter != null) {
			adapter.insert(name, number);			  
			adapter.notifyDataSetChanged();
		}
		
	}

	protected static void DeletefromAdapter(CheckBoxListLayoutAdapter adapter, String name) {
		
		if (adapter != null) {
			adapter.remove(name);
			adapter.notifyDataSetChanged();
		}
		
	}

	protected static void AddtoDatabase(Context context, String txt) {
	
		ContentValues cv = new ContentValues();
		cv.put(COLNAME[1], txt);
		SQLiteDatabase db = new SQLiteHelper(context).getWritableDatabase();
		db.insert(SQLiteHelper.DBNAME, COLNAME[1], cv);
		db.close();
		
	}

	protected static void DeletefromDatabase(Context context, String name) {

		SQLiteDatabase db = new SQLiteHelper(context).getWritableDatabase();
		db.delete(SQLiteHelper.DBNAME, " UF='" + name + "'", null);  
		db.close();
		
	}

	protected static void ClearDatabase(Context context, CheckBoxListLayoutAdapter adapter) {

		adapter.clear();
		adapter.notifyDataSetChanged();
		
		SQLiteDatabase db = new SQLiteHelper(context).getWritableDatabase();
		db.delete(SQLiteHelper.DBNAME, null, null);
		db.close();
		
	}



	protected static void UpdateDatabase(Context context, String oldname, String newname) {
		SQLiteDatabase db = new SQLiteHelper(context).getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLNAME[1], newname);

		db.update(SQLiteHelper.DBNAME, cv, "UF=?", new String[]{oldname});  
		db.close();
	}

	protected static void modifyDatabase(Context context, CheckBoxListLayoutAdapter adapter, String newname, int number) {

		String oldname = adapter.getItemString(number);
		UpdateDatabase(context, oldname, newname);
		DeletefromAdapter(adapter, oldname);
		AddtoAdapter(adapter, newname, number);
		
	}
	
	protected static ProgressDialog showLoadingDialog(Context ctx, boolean cancelable) {
		
		ProgressDialog dialog = new ProgressDialog(ctx);
		dialog.setMessage(ctx.getString(R.string.strPleaseWait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		dialog.show();
		
		return dialog;
		
	}

	protected static void ComposeDialog(Builder dg) {
		
		AlertDialog adg = dg.create();
		adg.getWindow().getAttributes().windowAnimations = R.style.Animations_UserWindowsDialogs;
		adg.show();

	}

	public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static int makeColor(SharedPreferences sp, String v, String col) {
		int bgOpacity_N = sp.getInt(v, 1);
		int savedBgColorNumber_N = sp.getInt(col, 0xFF000000);
		int bgRed_N = Color.red(savedBgColorNumber_N);
		int bgBlue_N = Color.blue(savedBgColorNumber_N);
		int bgGreen_N = Color.green(savedBgColorNumber_N);
		savedBgColorNumber_N = Color.argb(bgOpacity_N, bgRed_N, bgGreen_N, bgBlue_N);
		return savedBgColorNumber_N;
	}

}
