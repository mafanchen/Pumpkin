package com.video.test.network;

import android.util.Log;

import com.video.test.BuildConfig;
import com.video.test.utils.LogUtils;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 重试的拦截器
 *
 * @author : AhhhhDong
 * @date : 2019/5/15 15:09
 */
public class RetryInterceptor implements Interceptor {

    private static final String TAG = "RetryInterceptor";

    String[] hostList = BuildConfig.NG_BASE_URLS;

    @Override
    public Response intercept(Chain chain) throws IOException {
        return retry(chain, chain.request());
    }

    private Response retry(Chain chain, Request oriRequest) throws IOException {
        HttpUrl originalHttpUrl = oriRequest.url();
        HttpUrl newHttpUrl;
        Response response = null;
        try {
            response = chain.proceed(oriRequest);
            LogUtils.d(TAG, "Status : " + response.isSuccessful());
        } catch (IOException e) {
            LogUtils.d(TAG, "error : " + e.getMessage());
        }
        if (response != null && response.isSuccessful()) {
            Log.d(TAG, "response is success");
            return response;
        } else {
            String oldHost = originalHttpUrl.host();
            Log.d(TAG, "retry,old host is " + oldHost);
            Log.d(TAG, "retry,hostList " + Arrays.toString(hostList));
            int oldIndex = getHostIndex(originalHttpUrl.host());
            Log.d(TAG, "retry,oldIndex " + oldIndex);
            int newIndex = oldIndex + 1;
            if (newIndex >= hostList.length) {
                newIndex = 0;
                //保存选中的host地址，方便后续接口直接从此接口开始调用
                RetrofitHelper.sCurrentHostIndex = newIndex;
                //已经重试了所有的域名
                return chain.proceed(oriRequest);
            } else {
                //保存选中的host地址，方便后续接口直接从此接口开始调用
                RetrofitHelper.sCurrentHostIndex = newIndex;
                String newHost = hostList[newIndex];
                Log.d(TAG, "retry,new host is " + newHost);
                newHttpUrl = originalHttpUrl.newBuilder()
//                            .scheme(originalHttpUrl.scheme())
                        //目前只有第一个域名使用了https，当第一个域名连接失败时，尝试连接的后续域名都应该是http
//                        .scheme(newIndex == 0 ? "https" : "http")
                        //改为根据服务器地址判断网络协议
                        .scheme(getProtocol(newHost))
                        .host(getHost(newHost))
                        .build();
                Request request = oriRequest.newBuilder().url(newHttpUrl).build();
                if (response != null) {
                    response.close();
                }
                return retry(chain, request);
            }
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
