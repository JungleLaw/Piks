package com.law.think.frame.db;

import android.database.sqlite.SQLiteDatabase;

public abstract class DataSupport {
	public static final String TAG = DataSupport.class.getSimpleName();

	private Long id = null;

	public static SQLiteDatabase getDatabase() {
		return ThinkDb.getThinkDbEngine().getWritableDatabase();
	}

}
