package com.law.think.frame.inject.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.preference.Preference;

/**
 * Author: wyouflf Date: 13-8-16 Time: 下午2:37
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = Preference.OnPreferenceClickListener.class, listenerSetter = "setOnPreferenceClickListener", methodName = "onPreferenceClick")
public @interface OnPreferenceClick {
	String[] value();
}
