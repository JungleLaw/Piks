package com.law.think.frame.db.helper;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.law.think.frame.db.DataSupport;
import com.law.think.frame.db.annotation.Column;
import com.law.think.frame.db.annotation.Table;

import android.os.Build;
import android.text.TextUtils;

public class SQLHelper {
	public static final String NULL = " NULL";
	public static final String NOT_NULL = " NOT NULL";
	public static final String UNIQUE = " UNIQUE";

	//////////////////////////////////////////////////////////////////////////////////////
	// ENUMERATIONS
	//////////////////////////////////////////////////////////////////////////////////////

	public enum SQLiteType {
		INTEGER, REAL, TEXT, BLOB
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC CONSTANTS
	//////////////////////////////////////////////////////////////////////////////////////

	public static final boolean FOREIGN_KEYS_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;

	//////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE CONTSANTS
	//////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("serial")
	public static final HashMap<Class<?>, SQLiteType> TYPE_MAP = new HashMap<Class<?>, SQLiteType>() {
		{
			put(byte.class, SQLiteType.INTEGER);
			put(short.class, SQLiteType.INTEGER);
			put(int.class, SQLiteType.INTEGER);
			put(long.class, SQLiteType.INTEGER);
			put(float.class, SQLiteType.REAL);
			put(double.class, SQLiteType.REAL);
			put(boolean.class, SQLiteType.INTEGER);
			put(char.class, SQLiteType.TEXT);
			put(byte[].class, SQLiteType.BLOB);
			put(Byte.class, SQLiteType.INTEGER);
			put(Short.class, SQLiteType.INTEGER);
			put(Integer.class, SQLiteType.INTEGER);
			put(Long.class, SQLiteType.INTEGER);
			put(Float.class, SQLiteType.REAL);
			put(Double.class, SQLiteType.REAL);
			put(Boolean.class, SQLiteType.INTEGER);
			put(Character.class, SQLiteType.TEXT);
			put(String.class, SQLiteType.TEXT);
			put(Byte[].class, SQLiteType.BLOB);
		}
	};

	private SQLHelper() {
	}

	public static String getTableName(Class<? extends DataSupport> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			Table annotation = clazz.getAnnotation(Table.class);
			if (!TextUtils.isEmpty(annotation.name())) {
				return annotation.name();
			}
		}
		return clazz.getSimpleName();
	}

	public static String getColumnName(Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			Column annotation = field.getAnnotation(Column.class);
			if (!TextUtils.isEmpty(annotation.name()))
				return annotation.name();
			else
				return field.getName();
		}
		return null;
	}

	public static String getColumnType(Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			StringBuilder sbColumnType = new StringBuilder();
			Class<?> type = field.getType();
			if (TYPE_MAP.containsKey(type)) {
				sbColumnType.append(TYPE_MAP.get(type).toString());
			}
			Column annotation = field.getAnnotation(Column.class);
			if (annotation.notNull())
				sbColumnType.append(" ").append(NOT_NULL);
			else
				sbColumnType.append(" ").append(NULL);
			if (annotation.unique())
				sbColumnType.append(" ").append(UNIQUE);
			return sbColumnType.toString();
		}
		return null;
	}

}
