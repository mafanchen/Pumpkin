package jaygoo.library.m3u8downloader;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import jaygoo.library.m3u8downloader.utils.SpUtils;

/**
 * ================================================
 *
 * @Author JayGoo
 * 版    本：
 * 创建日期：2017/11/24
 * 描    述: M3U8Downloader 配置类
 * ================================================
 */
public class M3U8DownloaderConfig {

    private static final String TAG = "M3U8DownloaderConfig";
    private static final String TAG_SAVE_DIR = "TAG_SAVE_DIR_M3U8";
    private static final String TAG_THREAD_COUNT = "TAG_THREAD_COUNT_M3U8";
    private static final String TAG_CONN_TIMEOUT = "TAG_CONN_TIMEOUT_M3U8";
    private static final String TAG_READ_TIMEOUT = "TAG_READ_TIMEOUT_M3U8";
    private static final String TAG_DEBUG = "TAG_DEBUG_M3U8";
    private static Context mContext;

    public static M3U8DownloaderConfig build(Context context) {
        mContext = context;
        return new M3U8DownloaderConfig();
    }

    public static String getSaveDir() {
        return SpUtils.getString(mContext, TAG_SAVE_DIR, Environment.getExternalStorageDirectory().getPath() + "/video/cache/");
//        return Environment.getExternalStorageDirectory().getPath() + CACHE_DIRECTORY;
    }

    public M3U8DownloaderConfig setSaveDir(String saveDir) {
        SpUtils.putString(mContext, TAG_SAVE_DIR, saveDir);
        return this;
    }

    public static int getThreadCount() {
        return SpUtils.getInt(mContext, TAG_THREAD_COUNT, 3);
//        return 3;
    }

    public M3U8DownloaderConfig setThreadCount(int threadCount) {
        if (threadCount > 5) {
            threadCount = 5;
        }
        if (threadCount <= 0) {
            threadCount = 1;
        }
        SpUtils.putInt(mContext, TAG_THREAD_COUNT, threadCount);
        return this;
    }

    public static int getConnTimeout() {
        return SpUtils.getInt(mContext, TAG_CONN_TIMEOUT, 10 * 1000);
//        return 10 * 1000;
    }

    public M3U8DownloaderConfig setConnTimeout(int connTimeout) {
        SpUtils.putInt(mContext, TAG_CONN_TIMEOUT, connTimeout);
        return this;
    }

    public static int getReadTimeout() {
        return SpUtils.getInt(mContext, TAG_READ_TIMEOUT, 30 * 60 * 1000);
//        return 30 * 60 * 1000;
    }

    public M3U8DownloaderConfig setReadTimeout(int readTimeout) {
        SpUtils.putInt(mContext, TAG_READ_TIMEOUT, readTimeout);
        return this;
    }

    public static boolean isDebugMode() {
        return BuildConfig.DEBUG;
    }
}
