package com.video.test.network;

import android.util.Log;

import com.video.test.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于拦截请求的baseUrl，使之动态变化域名
 *
 * @author : AhhhhDong
 * @date : 2019/5/20 10:14
 */
public class BaseUrlInterceptor implements Interceptor {

    private String[] hostList = BuildConfig.NG_BASE_URLS;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        String originHost = originRequest.url().host();
        Log.i("BaseUrlInterceptor", "originHost = " + originHost);
        int index = getHostIndex(originHost);
        if (RetrofitHelper.sCurrentHostIndex != index) {
            originHost = hostList[RetrofitHelper.sCurrentHostIndex];
            Log.i("BaseUrlInterceptor", "has new originHost,originHost = " + originHost);
            HttpUrl url = originRequest.url().newBuilder()
                    .scheme(getProtocol(originHost))
                    .host(getHost(originHost))
                    .build();
            Request request = originRequest.newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        } else {
            return chain.proceed(originRequest);
        }
    }

    private int getHostIndex(String host) {
        for (int i = 0; i < hostList.length; i++) {
            String h = getHost(hostList[i]);
            if (h.equals(getHost(host))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据服务器域名地址获取host
     */
    private String getHost(String url) {
        String host;
        if (url.startsWith("http://")) {
            host = url.substring(7);
        } else if (url.startsWith("https://")) {
            host = url.substring(8);
        } else {
            host = url;
        }
        return host;
    }

    /**
     * 根据服务器域名地址获取协议类型
     */
    private String getProtocol(String url) {
        final String http = "http";
        final String https = "https";
        if (url == null) {
            return http;
        }
        if (url.startsWith(https)) {
            return https;
        } else {
            return http;
        }
    }
}
