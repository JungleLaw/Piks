package com.law.think.frame.inject.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.ExpandableListView;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = ExpandableListView.OnGroupExpandListener.class, listenerSetter = "setOnGroupExpandListener", methodName = "onGroupExpand")
public @interface OnGroupExpand {
	int[] value();

	int[] parentId() default 0;
}
