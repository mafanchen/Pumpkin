package com.video.test.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.video.test.utils.DownloadUtil;

/**
 * 监听广告中下载完成后的广播
 */
public class DownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "DownloadReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.d(TAG, String.format("onReceive: %tF", downloadId));
            DownloadUtil.openDownloadFileByDownloadId(context, downloadId);
        }
    }
}
