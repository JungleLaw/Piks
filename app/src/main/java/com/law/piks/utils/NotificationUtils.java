package com.law.piks.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.law.piks.R;
import com.law.piks.app.PiksApp;
import com.law.piks.others.AboutMeActivity;

/**
 * Created by Law on 2016/10/19.
 */

public class NotificationUtils {
    public static NotificationUtils mInstance;
    public static Context mContext;
    public static NotificationManager mManager;

    public NotificationUtils(Context context) {
        this.mContext = context;
    }

    private static void init() {
        if (mInstance == null)
            mInstance = new NotificationUtils(PiksApp.getInstance().getApplicationContext());
        if (mManager == null)
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void notify(Context context) {
        init();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, AboutMeActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        // 需要注意build()是在API
        // level16及之后增加的，API11可以使用getNotificatin()来替代
        Notification notify = new Notification.Builder(context).setSmallIcon(R.drawable.ic_piks).setTicker("TickerText:" + "您有新短消息，请注意查收！").setContentTitle("Notification Title").setContentText("This is the notification message").setContentIntent(pendingIntent).setNumber(1).build();
        // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        // 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
        mManager.notify(1, notify);
    }
}
