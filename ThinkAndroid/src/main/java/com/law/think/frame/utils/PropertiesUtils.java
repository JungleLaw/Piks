package com.law.think.frame.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import com.law.think.frame.core.ThinkCore;

import android.content.Context;

/**
 * 配置文件工具类
 * 
 */
public class PropertiesUtils {

	/**
	 * 根据文件名和文件路径 读取Properties文件
	 * 
	 * @param fileName
	 * @param dirName
	 * @return 设定文件
	 */
	public static Properties loadProperties(String fileName, String dirName) {
		Properties props = new Properties();
		try {
			int id = ThinkCore.getInstance().getApplication().getResources().getIdentifier(fileName, dirName,
					ThinkCore.getInstance().getApplication().getPackageName());
			props.load(ThinkCore.getInstance().getApplication().getResources().openRawResource(id));
		} catch (Exception e) {
			Logger.e(e.toString());
		}
		return props;
	}

	/**
	 * 读取Properties文件(指定目录)
	 * 
	 * @param file
	 * @return 设定文件
	 */
	public static Properties loadConfig(String file) {
		Properties properties = new Properties();
		try {
			FileInputStream s = new FileInputStream(file);
			properties.load(s);
		} catch (Exception e) {
			Logger.e(e.toString());
		}
		return properties;
	}

	/**
	 * 保存Properties(指定目录)
	 * 
	 * @param file
	 * @param properties
	 *            设定文件
	 */
	public static void saveConfig(String file, Properties properties) {
		try {
			FileOutputStream s = new FileOutputStream(file, false);
			properties.store(s, "");
		} catch (Exception e) {
			Logger.e(e.toString());
		}
	}

	/**
	 * 读取文件 文件在/data/data/package_name/files下 无法指定位置
	 * 
	 * @param fileName
	 * @return 设定文件
	 */
	public static Properties loadConfigNoDirs(String fileName) {
		Properties properties = new Properties();
		try {
			FileInputStream s = ThinkCore.getInstance().getApplication().openFileInput(fileName);
			properties.load(s);
		} catch (Exception e) {
			Logger.e(e.toString());
		}
		return properties;
	}

	/**
	 * 保存文件到/data/data/package_name/files下 无法指定位置
	 * 
	 * @param fileName
	 * @param properties
	 *            设定文件
	 */
	public static void saveConfigNoDirs(String fileName, Properties properties) {
		try {
			FileOutputStream s = ThinkCore.getInstance().getApplication().openFileOutput(fileName,
					Context.MODE_PRIVATE);
			properties.store(s, "");
		} catch (Exception e) {
			Logger.e(e.toString());
		}
	}

	public static Properties loadConfigAssets(String fileName) {

		Properties properties = new Properties();
		try {
			InputStream is = ThinkCore.getInstance().getApplication().getAssets().open(fileName);
			properties.load(is);
		} catch (Exception e) {
			Logger.e(e.toString());
		}
		return properties;
	}

	public static Properties loadSrc(String fileName) {
		Properties properties = new Properties();
		try {
			InputStream is = PropertiesUtils.class.getResourceAsStream(fileName);
			properties.load(is);
		} catch (Exception e) {
			Logger.e(e.toString());
		}
		return properties;
	}
}
