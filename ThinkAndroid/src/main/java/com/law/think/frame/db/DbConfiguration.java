package com.law.think.frame.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

public class DbConfiguration {
	private Context mContext;
	private Integer mDatabaseVersion;
	private String mDatabaseName;
	private Integer mCacheSize;

	private List<Class<? extends DataSupport>> mClasses;

	private DbConfiguration() {
	}

	public Context getContext() {
		return mContext;
	}

	public Integer getDatabaseVersion() {
		return mDatabaseVersion;
	}

	public String getDatabaseName() {
		return mDatabaseName;
	}

	public Integer getCacheSize() {
		return mCacheSize;
	}

	public List<Class<? extends DataSupport>> getDataSupportClasses() {
		return mClasses;
	}

	public static class Builder {
		// 1M
		private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 10;
		private static final int DEFAULT_DATABASE_VERSION = 1;
		private static final String DEFAULT_DATABASE_NAME = "ThinkAndroid.db";

		private Context mContext;
		private Integer mDatabaseVersion;
		private String mDatabaseName;
		private Integer mCacheSize;

		private List<Class<? extends DataSupport>> mClasses;

		public Builder(Context context) {
			mContext = context.getApplicationContext();
			mCacheSize = DEFAULT_CACHE_SIZE;
		}

		public Builder setCacheSize(int cacheSize) {
			mCacheSize = cacheSize;
			return this;
		}

		public Builder setDatabaseName(String databaseName) {
			mDatabaseName = databaseName;
			return this;
		}

		public Builder setDatabaseVersion(int version) {
			mDatabaseVersion = version;
			return this;
		}

		public Builder setDataSupportClass(Class<? extends DataSupport> dataSupport) {
			mClasses = new ArrayList<>();
			mClasses.add(dataSupport);
			return this;
		}

		public Builder setDataSupportClasses(List<Class<? extends DataSupport>> dataSupports) {
			mClasses = dataSupports;
			return this;
		}

		public Builder setDataSupportClasses(Class<? extends DataSupport>... dataSupports) {
			mClasses = Arrays.asList(dataSupports);
			return this;
		}

		public Builder addDataSupportClass(Class<? extends DataSupport> dataSupport) {
			if (mClasses == null) {
				mClasses = new ArrayList<>();
			}
			mClasses.add(dataSupport);
			return this;
		}

		public Builder addDataSupportClasses(List<Class<? extends DataSupport>> dataSupports) {
			if (mClasses == null) {
				mClasses = new ArrayList<>();
			}
			mClasses.addAll(dataSupports);
			return this;
		}

		public Builder addDataSupportClasses(Class<? extends DataSupport>... dataSupports) {
			if (mClasses == null) {
				mClasses = new ArrayList<>();
			}

			mClasses.addAll(Arrays.asList(dataSupports));
			return this;
		}

		public DbConfiguration build() {
			DbConfiguration mConfiguration = new DbConfiguration();
			mConfiguration.mContext = mContext;
			mConfiguration.mCacheSize = mCacheSize;

			if (TextUtils.isEmpty(mDatabaseName)) {
				mConfiguration.mDatabaseName = DEFAULT_DATABASE_NAME;
			} else {
				mConfiguration.mDatabaseName = mDatabaseName;
			}

			if (null == mDatabaseVersion) {
				mConfiguration.mDatabaseVersion = DEFAULT_DATABASE_VERSION;
			} else {
				mConfiguration.mDatabaseVersion = mDatabaseVersion;
			}
			if (null != mClasses) {
				mConfiguration.mClasses = mClasses;
			}
			return mConfiguration;
		}

	}

}
