package com.timestamp.calculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper 
{
	
	protected static final String DBNAME = "UserFormats"; 
	
	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// Not used.
	}


	public SQLiteHelper(Context context) {
		super(context, DBNAME + ".db", null, 1); 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DBNAME + 
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, UF TEXT)"); 
	}
}
