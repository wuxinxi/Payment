package com.szxb.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBContext extends SQLiteOpenHelper {

	public DBContext(Context context) {
		super(context, "szxb_record.db", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table record(id integer primary key autoincrement,outtradeno varchar(80),transactionid varchar(80),timeend varchar(30),total_fee varchar(6))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

}
