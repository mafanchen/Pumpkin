package com.video.test.network;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.video.test.TestApp;
import com.video.test.utils.ToastUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 拦截代理
 *
 * @author : AhhhhDong
 * @date : 2019/5/14 19:20
 */
public class ProxyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (isWifiProxy(TestApp.getContext())) {
            new Handler(Looper.getMainLooper()).post(() -> ToastUtils.showToast(TestApp.getContext(), "请您关闭代理软件"));
            return null;
        } else {
            return chain.proceed(chain.request());
        }
    }

    /*
     * 判断设备 是否使用代理上网
     * */
    private boolean isWifiProxy(Context context) {

        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

        String proxyAddress;

        int proxyPort;

        if (IS_ICS_OR_LATER) {

            proxyAddress = System.getProperty("http.proxyHost");

            String portStr = System.getProperty("http.proxyPort");

            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));

        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

}
