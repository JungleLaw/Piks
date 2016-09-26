package com.law.think.frame.db;

import com.law.think.frame.db.query.ThinkQuery;
import com.law.think.frame.utils.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ThinkDb {
	private static DbConfiguration mDbConfiguration = null;
	private static ThinkDb mThinkDb = null;
	private static ThinkDbEngine mThinkDbEngine = null;

	private ThinkDb() {
		Logger.i(mDbConfiguration);
		mThinkDbEngine = ThinkDbEngine.getIntance(mDbConfiguration);
	}

	public static void initialize(Context context) {
		ThinkContext.init(context);
		if (mDbConfiguration == null)
			mDbConfiguration = new DbConfiguration.Builder(context).build();
		Logger.i(mDbConfiguration);
		mThinkDb = new ThinkDb();
	}

	public static void initialize(Context context, DbConfiguration configuration) {
		mDbConfiguration = configuration;
		initialize(context);
	}

	public static void terminate() {
		if (null == mThinkDb) {
			return;
		}

		mThinkDb.doTerminate();
		ThinkContext.terminate();
	}

	private void doTerminate() {
		if (null != mThinkDbEngine) {
			mThinkDbEngine.close();
		}
	}

	public static DbConfiguration getDbConfiguration() {
		return mDbConfiguration;
	}

	public static ThinkDbEngine getThinkDbEngine() {
		return mThinkDbEngine;
	}

	protected static SQLiteDatabase getReadableDatabase() {
		return getThinkDbEngine().getReadableDatabase();
	}

	protected static SQLiteDatabase getWriteableDatabase() {
		return getThinkDbEngine().getWritableDatabase();
	}

	public static ThinkQuery<?> where(Class<? extends DataSupport> clazz) {
		return ThinkQuery.newThinkQuery(clazz);
	}
}
