package com.law.think.frame.inject.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.AdapterView;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = AdapterView.OnItemSelectedListener.class, listenerSetter = "setOnItemSelectedListener", methodName = "onNothingSelected")
public @interface OnNothingSelected {
	int[] value();

	int[] parentId() default 0;
}
