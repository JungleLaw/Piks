package com.law.think.frame.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.EventBase;
import com.law.think.frame.reflect.KernelReflect;
import com.law.think.frame.utils.Logger;

import android.app.Activity;
import android.preference.PreferenceActivity;
import android.view.View;

public class ThinkInject {

	private ThinkInject() {
	}

	public static void bind(View view) {
		injectObject(view, new ViewFinder(view));
	}

	public static void bind(Activity activity) {
		injectObject(activity, new ViewFinder(activity));
	}

	public static void bind(PreferenceActivity preferenceActivity) {
		injectObject(preferenceActivity, new ViewFinder(preferenceActivity));
	}

	public static void bind(Object handler, View view) {
		injectObject(handler, new ViewFinder(view));
	}

	public static void bind(Object handler, Activity activity) {
		injectObject(handler, new ViewFinder(activity));
	}

	private static void injectObject(Object target, ViewFinder finder) {
		Class<?> clazz = target.getClass();

		// inject view
		List<Field> fields = KernelReflect.declaredFields(clazz, 1);
		if (fields != null && fields.size() > 0) {
			for (Field field : fields) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				if (viewInject != null) {
					View view = finder.findViewById(viewInject.value(), viewInject.parentId());
					if (view != null) {
						field.setAccessible(true);
						KernelReflect.set(target, field, view);
					}
				}
			}
		}

		// inject event
		List<Method> methods = KernelReflect.declaredMethods(clazz, 1);
		if (methods != null && methods.size() > 0) {
			for (Method method : methods) {
				Annotation[] annotations = method.getAnnotations();
				for (Annotation annotation : annotations) {
					Class<?> annType = annotation.annotationType();
					if (annType.getAnnotation(EventBase.class) != null) {
						method.setAccessible(true);

						try {
							Method valueMethod = annType.getDeclaredMethod("value");
							Method parentIdMethod = null;
							parentIdMethod = annType.getDeclaredMethod("parentId");
							Object values = valueMethod.invoke(annotation);
							Object parentIds = parentIdMethod == null ? null : parentIdMethod.invoke(annotation);
							int parentIdsLen = parentIds == null ? 0 : Array.getLength(parentIds);
							int valuesLen = Array.getLength(values);
							for (int i = 0; i < valuesLen; i++) {
								ViewInjectInfo info = new ViewInjectInfo();
								info.value = Array.get(values, i);
								info.parentId = parentIdsLen > i ? Array.getInt(parentIds, i) : 0;
								EventListenerManager.addEventMethod(finder, info, annotation, target, method);
							}
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							Logger.e(e.getMessage());
						}

					}
				}
			}
		}
	}

}
