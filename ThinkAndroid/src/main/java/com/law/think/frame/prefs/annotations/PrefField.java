package com.law.think.frame.prefs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface PrefField {
	String value() default "";

	double numDef() default 0;

	boolean boolDef() default false;

	String[] strDef() default "";
}
