package com.law.think.frame.confs;

import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.imageloader.display.Display;
import com.law.think.frame.utils.Logger;

import android.app.Application;

public final class AppConf {
	public static boolean isDebug() {
		return Config.isDebug;
	}

	public static Application app() {
		if (Config.app == null) {
			throw new RuntimeException("please invoke x.Ext.init(app) on Application#onCreate()");
		}
		return Config.app;
	}

	public static class Config {
		private static Application app;
		private static boolean isDebug = false;

		public static void conf(Application appContext) {
			// TODO Auto-generated method stub
			if (Config.app != null) {
				Config.app = appContext;
			}
		}

		public static void setDebug(boolean isDebug) {
			Config.isDebug = isDebug;
			Logger.init(isDebug);
		}

		public static void initImageLoader(Application appContext, Display displayImpl) {
			ImageLoader.init(appContext, displayImpl);
		}

	}

}
