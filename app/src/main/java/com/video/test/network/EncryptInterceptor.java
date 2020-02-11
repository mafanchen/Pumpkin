package com.video.test.network;

import android.util.Log;

import com.google.gson.Gson;
import com.video.test.ApiUrl;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.TestApp;
import com.video.test.sp.SpUtils;
import com.video.test.utils.AESUtils;
import com.video.test.utils.EncryptUtils;
import com.video.test.utils.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * okHttp 加密拦截
 *
 * @author : AhhhhDong
 * @date : 2019/4/25 11:20
 */
public class EncryptInterceptor implements Interceptor {

    private static final String TAG = "EncryptInterceptor";

    /**
     * 不进行加密的接口
     */
    private static String[] EXCLUDE_API_LIST = {
            //上传接口
            ApiUrl.UPLOAD,
            //更新接口
            ApiUrl.UPDATE
    };

    private Gson gson = new Gson();

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_PATCH = "PATCH";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        //判断是否加密
        if (checkRequestIsExclude(request)) {
            Log.d(TAG, "无需加密");
            return chain.proceed(addPackageAppId(request));
        }
        //获取参数进行加密
        Map<String, String> params = parseParams(request);
        String encryptParams = null;
        if (params != null) {
            encryptParams = encryptParams(params);
        }
        if (method.equals(METHOD_GET)) {
            // TODO: 2019/4/25 暂无get
            return chain.proceed(request);
        } else {
            return chain.proceed(addPackageAppId(buildPostRequest(request, encryptParams)));
        }
    }

    private Request buildPostRequest(Request request, String encryptParams) {
        String token = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String tokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        String versionCode = BuildConfig.VERSION_NAME.replace(".", "");
        String phoneType = "1";
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("token", token);
        Log.d(TAG, "添加TOKEN:" + token);
        builder.add("token_id", tokenId);
        Log.d(TAG, "添加TOKEN_ID:" + tokenId);
        builder.add("phone_type", phoneType);
        Log.d(TAG, "添加PHONE_TYPE:" + phoneType);
        // TODO: 2019/6/13 低版本号会导致接口异常，现在先写死1.2.3
        if (BuildConfig.APP_ID != 1) {
            builder.add("versions_code", "123");
            Log.d(TAG, "添加VERSION_CODE:123");
        } else {
            builder.add("versions_code", versionCode);
            Log.d(TAG, "添加VERSION_CODE:" + versionCode);
        }
        if (encryptParams != null) {
            builder.add("request_key", encryptParams);
        }
        return request
                .newBuilder()
                .method(request.method(), builder.build())
                .build();
    }

    /**
     * 对原有参数进行加密
     * POST 请求
     */
    private String encryptParams(@NotNull Map<String, String> params) {
        return encryptParams(gson.toJson(params));
    }

    /**
     * 对原有参数进行加密
     */
    private String encryptParams(@NotNull String params) {
        LogUtils.d(TAG, "开始加密:" + params);
        String aesKey = EncryptUtils.keyFromJNI();
        String aesVi = EncryptUtils.viFromJNI();
        String encrypt = AESUtils.encrypt(params, aesKey, aesVi);
        LogUtils.d(TAG, "加密完成: " + encrypt);
        return encrypt;
    }

    /**
     * 解析请求参数
     */
    @Nullable
    private Map<String, String> parseParams(Request request) {
        //GET POST DELETE PUT PATCH
        String method = request.method();
        Map<String, String> params = null;
        if (METHOD_GET.equals(method)) {
            params = doGet(request);
        } else if (METHOD_POST.equals(method) || METHOD_PUT.equals(method) || METHOD_DELETE.equals(method) || METHOD_PATCH.equals(method)) {
            RequestBody body = request.body();
            if (body instanceof FormBody) {
                params = doForm(request);
            }
        }
        return params;
    }

    /**
     * 获取get方式的请求参数
     */
    private Map<String, String> doGet(Request request) {
        Map<String, String> params = null;
        HttpUrl url = request.url();
        Set<String> strings = url.queryParameterNames();
        if (strings != null) {
            Iterator<String> iterator = strings.iterator();
            params = new HashMap<>();
            int i = 0;
            while (iterator.hasNext()) {
                String name = iterator.next();
                String value = url.queryParameterValue(i);
                params.put(name, value);
                i++;
            }
        }
        return params;
    }

    /**
     * 获取表单的请求参数
     */
    private Map<String, String> doForm(Request request) {
        Map<String, String> params = null;
        FormBody body = null;
        try {
            body = (FormBody) request.body();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (body != null) {
            int size = body.size();
            if (size > 0) {
                params = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    params.put(body.name(i), body.value(i));
                }
            }
        }
        return params;
    }

    /**
     * 判断请求是否不加密
     *
     * @param request 请求
     * @return 是否加密
     */
    private boolean checkRequestIsExclude(Request request) {
        for (String url : EXCLUDE_API_LIST) {
            if (request.url().toString().contains(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加分包的独有的app_id
     *
     * @param request 原Request
     * @return
     */
    private Request addPackageAppId(Request request) {
        RequestBody body;
        RequestBody originBody = request.body();
        if (originBody instanceof MultipartBody) {
            List<MultipartBody.Part> parts = new ArrayList<>(((MultipartBody) originBody).parts());
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (MultipartBody.Part part : parts) {
                builder.addPart(part);
            }
            builder.addFormDataPart("app_id", String.valueOf(BuildConfig.APP_ID));
            Log.d(TAG, "添加APP_ID:" + BuildConfig.APP_ID);
            if (!BuildConfig.STORE_CHANNEL.isEmpty()) {
                //如果是上线版本  app_versions=101
                builder.addFormDataPart("app_versions", BuildConfig.STORE_CHANNEL);
            }
            // 新字段  跟广告有关的接口 都需要通过上传 ad_version (实际应该为adChannel)  判断是否展示广告。
            builder.addFormDataPart("ad_version", String.valueOf(BuildConfig.AD_CHANNEL));
            body = builder.build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            if (originBody instanceof FormBody) {
                for (int i = 0; i < ((FormBody) originBody).size(); i++) {
                    builder.add(((FormBody) originBody).name(i), ((FormBody) originBody).value(i));
                }
            }
            builder.add("app_id", String.valueOf(BuildConfig.APP_ID));
            Log.d(TAG, "添加APP_ID:" + BuildConfig.APP_ID);
            if (!BuildConfig.STORE_CHANNEL.isEmpty()) {
                //如果是上线版本  app_versions=101
                builder.add("app_versions", BuildConfig.STORE_CHANNEL);
            }
            // 新字段  跟广告有关的接口 都需要通过上传 ad_version(实际应该为adChannel)  判断是否展示广告。
            builder.add("ad_version", String.valueOf(BuildConfig.AD_CHANNEL));
            body = builder.build();
        }
        return request.newBuilder().method(request.method(), body).build();
    }
}
