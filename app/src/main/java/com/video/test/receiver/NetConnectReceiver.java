package com.video.test.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.video.test.utils.LogUtils;
import com.video.test.utils.NetworkUtils;

/**
 * Created by Enoch on 2017/6/1.
 */

public class NetConnectReceiver extends BroadcastReceiver {
    private static final String TAG = "NetConnectReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.i(getClass(), "NetConnectReceiver action ==" + action);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            NetworkUtils.checkNetConnectChange();
        } else if ("enableP2PNetwork".equals(action)) {
            NetworkUtils.checkNetConnectChange();
        }
    }
}
