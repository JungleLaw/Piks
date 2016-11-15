package com.law.piks.others.update;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.law.piks.R;
import com.law.piks.app.Constants;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.widget.ThinkToast;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

/**
 * Created by Law on 2016/11/4.
 */

public class UpdateService extends Service {
    public static final String DOWNLOAD_BROADCAST = "download_broadcast";

    public static final String DOWNLOAD_NEW_VERSION_FILE = "new_version_file";
    public static final String DOWNLOAD_STATUS = "download_status";
    public static final int DOWNLOAD_GOING = 1;
    public static final int DOWNLOAD_ERROR = 2;
    public static final int DOWNLOAD_COMPELET = 3;

    private static final int NOTIFICATION_TAG = 1024;
    private static final String TAG = UpdateService.class.getSimpleName();

    private static final String DOWNLOAD_URL = "download_url";
    private static final String DOWNLOAD_VERSION_NAME = "download_file_name";
    /**
     * Dir: /Download
     */
    private File mDownloadDir;
    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String mFileName;
    private LocalBroadcastManager mLocalBroadcastManager;

    public static void startDownload(Context context, String downloadUrl, String versionName) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(DOWNLOAD_URL, downloadUrl);
        intent.putExtra(DOWNLOAD_VERSION_NAME, versionName);
        context.startService(intent);
    }

    private void updateNotification() {
        mNotificationManager.notify(NOTIFICATION_TAG, mBuilder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String url = intent.getStringExtra(DOWNLOAD_URL);
            String version = intent.getStringExtra(DOWNLOAD_VERSION_NAME);
            mBuilder.setSmallIcon(R.mipmap.ic_piks).setOngoing(true).setContentTitle("Piks新版本下载").setContentText("Init Download").setProgress(100, 0, true).setTicker("开始下载 Piks " + version);
            mDownloadDir = new File(Constants.DOWNLOAD.DOWNLOAD_FILE_PATH);
            Logger.i(TAG, mDownloadDir.getAbsolutePath());
            if (!mDownloadDir.exists()) {
                mDownloadDir.mkdirs();
            }
            mFileName = Constants.DOWNLOAD.DOWNLOAD_FILE_NAME_PREFIX + version + Constants.DOWNLOAD.DOWNLOAD_FILE_NAME_STUFIX;
            File downloadFile = new File(mDownloadDir.getAbsolutePath(), mFileName);
            Logger.i(TAG, downloadFile.getAbsolutePath());
            if (downloadFile.exists()) {
                downloadFile.delete();
            }
            download(url, downloadFile.getAbsolutePath());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void download(String downloadUrl, final String path) {
        FileDownloader.getImpl().create(downloadUrl).setPath(path).setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Logger.i(TAG, "pending");
                mBuilder.setContentText("等待下载");
                mBuilder.setProgress(100, 0, false);
                updateNotification();
                sendBroadCast(UpdateService.DOWNLOAD_GOING, "");
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                Logger.i(TAG, "connected");
                mBuilder.setContentText("连接成功");
                mBuilder.setProgress(100, 0, false);
                updateNotification();
                sendBroadCast(UpdateService.DOWNLOAD_GOING, "");
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Logger.i(TAG, "progress");
                mBuilder.setContentText("正在下载");
                mBuilder.setOngoing(true);
                mBuilder.setProgress(100, soFarBytes * 100 / totalBytes, false);
                updateNotification();
                sendBroadCast(UpdateService.DOWNLOAD_GOING, "");
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                Logger.i(TAG, "blockComplete");
                mBuilder.setContentText("正在下载");
                mBuilder.setProgress(100, 100, false);
                updateNotification();
                sendBroadCast(UpdateService.DOWNLOAD_GOING, "");
            }

            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                Logger.i(TAG, "retry");
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                Logger.i(TAG, "completed");
                mBuilder.setContentText("下载完成,点击安装");
                mBuilder.setOngoing(false);
                Uri uri = Uri.fromFile(new File(path));
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                PendingIntent updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
                mBuilder.setContentIntent(updatePendingIntent);
                mBuilder.setProgress(100, 100, false);
                updateNotification();
                ThinkToast.showToast(UpdateService.this, "新版本下载完成", ThinkToast.LENGTH_LONG, ThinkToast.INFO);
                sendBroadCast(UpdateService.DOWNLOAD_COMPELET, path);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Logger.i(TAG, "paused");
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Logger.i(TAG, "error" + e.getMessage());
                mBuilder.setContentText("下载出现错误");
                mBuilder.setOngoing(false);
                mBuilder.setProgress(100, 0, false);
                updateNotification();
                sendBroadCast(UpdateService.DOWNLOAD_ERROR, "");
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                Logger.i(TAG, "warn");
            }
        }).start();
    }

    private void sendBroadCast(int status, String filePath) {
        Intent intent = new Intent();
        intent.setAction(UpdateService.DOWNLOAD_BROADCAST);
        intent.putExtra(UpdateService.DOWNLOAD_STATUS, status);
        if (status == DOWNLOAD_COMPELET) {
            intent.putExtra(DOWNLOAD_NEW_VERSION_FILE, filePath);
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }
}