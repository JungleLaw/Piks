package com.law.think.frame.utils;

import java.util.Locale;

/*
 * Created Date:2015年11月13日
 * Copyright @ 2015 BU
 * Description: 类描述
 *
 * History:
 */
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.law.think.frame.prefs.AnyPref;


/**
 * 设置语言资源(国际化)
 */
public class LanguageSettingUtils {
    public final static String ENGLISH = "en";
    public final static String CHINESE = "zh";
    // 单例模式-
    private static LanguageSettingUtils instance;
    private Context context;
    // 存储当前系统的language设置-
    private String defaultLanguage;
    // 存储当前系统Locale-
    private Locale defaultLocale;
//    // 存储当前系统Locale的SharedPreference
//    private AnyPref anyPref;

    private LanguageSettingUtils() {
        throw new AssertionError("no instance.");
    }

    private LanguageSettingUtils(Context context) {
        // 得到系统语言-
        Locale localLocale = Locale.getDefault();
        this.defaultLocale = localLocale;

        // 保存系统语言到defaultLanguage
        String str = this.defaultLocale.getLanguage();
        this.defaultLanguage = str;

        this.context = context;
    }

    /**
     * 创建并获得LanguageSettingUtils实例
     *
     * @return LanguageSettingUtils实例
     */
    public static LanguageSettingUtils get() {
        if (instance == null)
            throw new IllegalStateException("language setting not initialized yet");
        return instance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        if (instance == null) {
            instance = new LanguageSettingUtils(context);
        }
    }


    /**
     * 设置语言并获得Configuration
     *
     * @param paramString 比如"en","cn"
     * @return
     */
    @SuppressWarnings("deprecation")
    private Configuration getUpdatedLocaleConfig(String paramString) {
        Configuration localConfiguration = context.getResources().getConfiguration();
        Locale localLocale = getLocale(paramString);
        localConfiguration.locale = localLocale;
        return localConfiguration;
    }

    /**
     * @return 语言设置
     */
    public String getLanguage() {
//        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
//        // 如果当前程序没有设置language属性就返回系统语言，如果有，就返回以前的-
//        return localSharedPreferences.getString("language", this.defaultLanguage);
        return AnyPref.getDefault().getString("language", this.defaultLanguage);
    }


    /**
     * @return 配置文件中没有语言设置
     */
    public Locale getLocale() {
        String str = getLanguage();
        return getLocale(str);
    }


    /**
     * 创建新Locale并覆盖原Locale
     *
     * @param paramString 比如"en","cn"
     * @return 返回新Locale
     */
    public Locale getLocale(String paramString) {
        Locale localLocale = new Locale(paramString);
        Locale.setDefault(localLocale);
        return localLocale;
    }


    /**
     * 刷新显示配置
     */
    @SuppressWarnings("deprecation")
    public void refreshLanguage() {
        String str = getLanguage();
        Resources localResources = this.context.getResources();
        if (!localResources.getConfiguration().locale.getLanguage().equals(str)) {
            Configuration localConfiguration = getUpdatedLocaleConfig(str);
            // A structure describing general information about a display, such
            // as its size, density, and font scaling.
            DisplayMetrics localDisplayMetrics = localResources.getDisplayMetrics();
            localResources.updateConfiguration(localConfiguration, localDisplayMetrics);
        }
    }


    /**
     * 设置系统语言
     *
     * @param paramString 语言,比如"en"、"cn"
     */
    public void saveLanguage(String paramString) {
        AnyPref.getDefault().getEditor().putString("language", paramString).commit();
//        PreferenceManager.getDefaultSharedPreferences(this.context).edit().putString("language", paramString).commit();
    }


    /**
     * 保存系统的语言设置到SharedPreferences
     */
    public void saveSystemLanguage() {
//        PreferenceManager.getDefaultSharedPreferences(this.context).edit()
//                .putString("PreSysLanguage", this.defaultLanguage).commit();
        AnyPref.getDefault().getEditor().putString("PreSysLanguage", this.defaultLanguage).commit();
    }

    /**
     * @param cuerSysLanguage 检查系统语言设置变化
     */
    public void checkSysChanged(String cuerSysLanguage) {
        // 如果系统语言设置发生变化-
//        if (!cuerSysLanguage.equals(
//                PreferenceManager.getDefaultSharedPreferences(this.context).getString("PreSysLanguage", "zh"))) {
        if (!cuerSysLanguage.equals(AnyPref.getDefault().getString("PreSysLanguage", "zh"))) {
            // 如果系统保存了this对象，就在这里修改defaultLanguage的值为当前系统语言cuerSysLanguage
            this.defaultLanguage = cuerSysLanguage;
            saveLanguage(cuerSysLanguage);
            saveSystemLanguage();
        }
    }
}
