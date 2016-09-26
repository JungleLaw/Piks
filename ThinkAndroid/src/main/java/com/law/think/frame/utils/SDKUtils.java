package com.law.think.frame.utils;

import android.os.Build;

public class SDKUtils {
	public static boolean hasJellyBeanMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean hasJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean hasKitKatWatch() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;
	}

	public static boolean hasLollopop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	public static boolean hasLollopopMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
	}

	public static boolean hasM() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	public static boolean hasN() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
	}
}
