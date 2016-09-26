package com.law.think.frame.db;

import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.SDKUtils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class ThinkDbEngine extends SQLiteOpenHelper {
	private int openedConnections = 0;
	private static ThinkDbEngine mInstance;
	private SchemaGenerator mSchemaGenerator;

	private ThinkDbEngine(DbConfiguration configuration) {
		super(configuration.getContext(), configuration.getDatabaseName(), null, configuration.getDatabaseVersion());
		mSchemaGenerator = SchemaGenerator.getInstance(configuration.getDataSupportClasses());
	}

	public static ThinkDbEngine getIntance(DbConfiguration configuration) {
		if (null == mInstance) {
			synchronized (ThinkDbEngine.class) {
				if (null == mInstance) {
					mInstance = new ThinkDbEngine(configuration);
				}
			}
		}
		return mInstance;
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Logger.i("onOpen");
		executePragmas(db);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		Logger.i("onCreate");
		executePragmas(database);
		mSchemaGenerator.createDatabase(database);
	}

	@Override
	public void onConfigure(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		executePragmas(database);
		final DbConfiguration mConfiguration = ThinkDb.getDbConfiguration();
		if (null != mConfiguration) {
			database.setMaximumSize(mConfiguration.getCacheSize());
		}
		super.onConfigure(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Logger.i("onUpgrade");
		executePragmas(database);
		mSchemaGenerator.doUpgrade(database, oldVersion, newVersion);
	}

	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		openedConnections--;
		if (openedConnections == 0) {
			Logger.i("close");
			super.close();
		}
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		// TODO Auto-generated method stub
		Logger.i("getWritableDatabase");
		openedConnections++;
		return super.getWritableDatabase();
	}

	@Override
	public SQLiteDatabase getReadableDatabase() {
		// TODO Auto-generated method stub
		Logger.i("getReadableDatabase");
		openedConnections++;
		return super.getReadableDatabase();
	}

	private void executePragmas(SQLiteDatabase db) {
		if (SDKUtils.hasJellyBeanMR1()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
			Logger.i("Foreign Keys supported. Enabling foreign key features.");
		}
	}
}
