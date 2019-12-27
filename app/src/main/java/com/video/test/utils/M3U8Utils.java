package com.video.test.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Enoch Created on 2019/1/22.
 */
public class M3U8Utils {


    private M3U8Utils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }


    /**
     * 将Url转换为M3U8对象
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String parseIndex(String url) throws IOException {
        Log.d("parse M3U8", "Start ");
        String urlParam = addUrlParams(url);
        String basepath = urlParam.substring(0, urlParam.lastIndexOf('/') + 1);

        Log.d("parse M3U8", " basepath : " + basepath + " urlParam : " + urlParam + " Thread :" + Thread.currentThread());
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(urlParam).openStream()));

        Log.d("parse M3U8", " basepath : " + basepath + " urlParam : " + urlParam);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.endsWith("m3u8")) {
                return parseIndex(basepath + line);
            }
        }
        reader.close();
        Log.d("parseIndex M3U8", "urlParam : " + urlParam);
        return urlParam;
    }


    private static String addUrlParams(String url) {
        if (!TextUtils.isEmpty(url)) {
            String timeStamp = String.valueOf(System.currentTimeMillis() + 3600);
            String ipAddress = NetworkUtils.getIPAddress(true);
            String encode = EncryptUtils.md5FromJNI(timeStamp, ipAddress);
            LogUtils.d("parseIndex M3U8", "url = " + url + "?wsSecret=" + encode + "&wsTime=" + timeStamp);
            return url + "?wsSecret=" + encode + "&wsTime=" + timeStamp;
        } else {
            return "";
        }
    }
}
