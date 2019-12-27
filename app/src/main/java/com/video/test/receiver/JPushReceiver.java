package com.video.test.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.javabean.NotificationBean;
import com.video.test.utils.IntentUtils;
import com.video.test.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * @author Enoch Created on 2019/1/28.
 */
public class JPushReceiver extends JPushMessageReceiver {

    private static final String TAG = "JPushReceiver";

    public JPushReceiver() {
        super();
    }

    /**
     * 点击通知的回调
     */
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        String extras = notificationMessage.notificationExtras;
        if (TextUtils.isEmpty(extras)) {
            return;
        }
        Log.d(TAG, "extra=" + extras);
        Gson gson = new Gson();
        NotificationBean bean = gson.fromJson(extras, NotificationBean.class);
        if (bean.getType() == null) {
            return;
        }
        //判断应用是否存活
        if (isProcessRunning(context, BuildConfig.APPLICATION_ID)) {
            switch (bean.getType()) {
                case AppConstant.BANNER_TYPE_VIDEO:
                    ARouter.getInstance().build("/player/activity").withString("vodId", bean.getVodId()).navigation();
                    break;
                case AppConstant.BANNER_TYPE_ROUTER:
                    String path = bean.getRouter();
                    if (TextUtils.isEmpty(path)) {
                        break;
                    }
                    ARouter.getInstance().build(path).navigation();
                    break;
                case AppConstant.BANNER_TYPE_WEBURL:
                    context.startActivity(IntentUtils.getBrowserIntent(bean.getWebUrl()));
                    break;
                case AppConstant.BANNER_TYPE_TOPIC:
                    LogUtils.d(TAG, "BANNER_TYPE_Topic");
                    if (bean.getZtPid() == null) {
                        return;
                    }
                    int pid = bean.getZtPid();
                    String tag = bean.getZtTag();
                    String type = bean.getZtType();
                    ARouter.getInstance().build("/topicVideoList/activity")
                            .withInt("pid", pid)
                            .withString("tag", tag)
                            .withString("type", type)
                            .navigation();
                    break;
                default:
                    break;
            }
        } else {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                EventBus.getDefault().postSticky(bean);
            }
        }
    }

    /**
     * 判断是进程是否存活
     *
     * @param context
     * @param processName
     * @return
     */
    private static boolean isProcessRunning(Context context, String processName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : runningAppProcesses) {
            if (process.processName.equals(processName)) {
                return true;
            }
        }
        return false;
    }
}
