package com.law.think.frame.inject.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.RadioGroup;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = RadioGroup.OnCheckedChangeListener.class, listenerSetter = "setOnCheckedChangeListener", methodName = "onCheckedChanged")
public @interface OnRadioGroupCheckedChange {
	int[] value();

	int[] parentId() default 0;
}
