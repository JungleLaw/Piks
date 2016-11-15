package com.law.piks.app;

import android.os.Environment;

import com.law.think.frame.utils.AppUtils;

import java.io.File;

/**
 * Created by Law on 2016/9/21.
 */

public final class Constants {
    public final static class SharedPrefrenced {
        public static final String VERSION_CODE = "versioncode";

        public static final String HAS_NEW_VERSION = "hasNewVersion";
    }

    public final static class FIR {
        public static final String FIR_TOKEN = "b4b783c8e3f834735a83e74d3d5a6c6b";
    }

    public final static class SHARE {
        public static final String FIR_TOKEN = "b4b783c8e3f834735a83e74d3d5a6c6b";

        public final static class WX {
            public static final String APP_ID = "wx128f53045f6fb603";
        }
    }

    public final static class DOWNLOAD {
        public final static String DOWNLOAD_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppInfo(PiksApp.getInstance()).getPackageName() + File.separator;
        public final static String DOWNLOAD_FILE_NAME_PREFIX = AppUtils.getAppInfo(PiksApp.getInstance()).getName() + "_";
        public final static String DOWNLOAD_FILE_NAME_STUFIX = ".apk";
        public final static String DOWNLOAD_FILE_NAME_TEMP_STUFIX = ".temp";
    }
}
