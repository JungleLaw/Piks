package com.law.think.frame.inject.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.preference.Preference;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = Preference.OnPreferenceChangeListener.class, listenerSetter = "setOnPreferenceChangeListener", methodName = "onPreferenceChange")
public @interface OnPreferenceChange {
	String[] value();
}
