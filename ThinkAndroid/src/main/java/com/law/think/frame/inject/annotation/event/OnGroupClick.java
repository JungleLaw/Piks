
package com.law.think.frame.inject.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.ExpandableListView;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = ExpandableListView.OnGroupClickListener.class, listenerSetter = "setOnGroupClickListener", methodName = "onGroupClick")
public @interface OnGroupClick {
	int[] value();

	int[] parentId() default 0;
}
