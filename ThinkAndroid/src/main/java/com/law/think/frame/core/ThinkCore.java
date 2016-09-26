package com.law.think.frame.core;

import java.lang.reflect.Field;

import com.law.think.frame.db.ThinkDb;
import com.law.think.frame.prefs.AnyPref;
import com.law.think.frame.reflect.KernelObject;
import com.law.think.frame.reflect.KernelReflect;
import com.law.think.frame.utils.Logger;

import android.app.Application;
import android.content.Context;

public class ThinkCore {
	private Application mApplication;

	private static ThinkCore mCore;

	public static ThinkCore getInstance() {
		if (mCore == null) {
			synchronized (ThinkCore.class) {
				if (mCore == null) {
					mCore = new ThinkCore();
				}
			}
		}
		return mCore;
	}

	/** aop切入点 **/
	private InstrumentationIoc instrumentationIoc;

	public void initCore(Application application) {
		this.mApplication = application;

		long time = System.currentTimeMillis();
		// --------------------------------------------------------------------------------------------------
		// 整个框架的核心
		// 反射获取mMainThread
		// getBaseContext()返回的是ContextImpl对象 ContextImpl中包含ActivityThread
		// mMainThread这个对象
		Context object = application.getBaseContext();
		Object mainThread = KernelObject.declaredGet(object, "mMainThread");
		// 反射获取mInstrumentation的对象
		Field instrumentationField = KernelReflect.declaredField(mainThread.getClass(), "mInstrumentation");
		instrumentationIoc = new InstrumentationIoc();
		// 自定义一个Instrumentation的子类 并把所有的值给copy进去
		// --------------------------------------------------------------------------------------------------
		// 是否打开兼容模式
		// KernelObject.copy(KernelReflect.get(mainThread,
		// instrumentationField), instrumentationIoc);
		// 再把替换过的Instrumentation重新放进去
		KernelReflect.set(mainThread, instrumentationField, instrumentationIoc);
		// --------------------------------------------------------------------------------------------------
		Logger.d("appliaction 加载时间为:" + (System.currentTimeMillis() - time));
		// initDatabase(application);
		initPref(application);
	}

	public Application getApplication() {
		if (mApplication == null) {
			throw new IllegalStateException("U should init ThinkCore firstly.");
		}
		return mApplication;
	}

	private void initDatabase(Application appContext) {
		ThinkDb.initialize(appContext);
	}

	private void initPref(Application appContext) {
		AnyPref.init(appContext);
	}
}
