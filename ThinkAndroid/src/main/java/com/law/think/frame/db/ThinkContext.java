package com.law.think.frame.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

class ThinkContext {
	private static Context mContext;

	private ThinkContext() {
	}

	public static void init(Context ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx shouldn't be null!");
		}
		mContext = ctx;
	}

	public static void terminate() {
		mContext = null;
	}

	public static Context getContext() {
		return mContext;
	}

	public static AssetManager getAssets() {
		return getContext().getAssets();
	}

	public static PackageManager getPackageManager() {
		return getContext().getPackageManager();
	}

	public static String getPackageName() {
		return getContext().getPackageName();
	}

	public static SharedPreferences getSharedPreferences(String name, int mode) {
		return getContext().getSharedPreferences(name, mode);
	}
}
