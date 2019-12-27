package com.video.test.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.video.test.TestApp;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载工具类
 *
 * @author : AhhhhDong
 * @date : 2019/3/29 18:34
 */
public final class DownloadUtil {

    private static final String TAG = "DownloadUtil";

    /**
     * 下载文件（apk）保存的下载任务id，key=下载链接，value=下载id
     */
    private static final Map<String, Long> DOWNLOAD_MAP = new HashMap<>();

    /**
     * 根目录
     *
     * @return 根目录文件对象
     */
    public static File getRootDirFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //如果sd卡存在，或者sd卡不可移除，返回/sdcard/Android/data/<packageName>/files
            return TestApp.getContext().getExternalFilesDir(null);
        } else {
            //否则返回/data/data/<packageName>/files
            return TestApp.getContext().getFilesDir();
        }
    }

    /**
     * 根目录
     *
     * @return 根目录路径
     */
    public static String getRootDirPath() {
        return getRootDirFile().getAbsolutePath();
    }

    /**
     * 下载文件目录
     *
     * @return 下载目录对象
     */
    public static File getDownloadDirFile() {
        File downloadDir = new File(getRootDirFile(), "Download");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        return downloadDir;
    }

    /**
     * 下载文件目录
     *
     * @return 下载目录路径
     */
    public static String getDownloadDirPath() {
        return getDownloadDirFile().getAbsolutePath();
    }

    /**
     * 缓存文件目录
     *
     * @return 缓存目录对象
     */
    public static File getCacheDirFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //如果sd卡存在，或者sd卡不可移除，返回/sdcard/Android/data/<packageName>/cache
            return TestApp.getContext().getExternalCacheDir();
        } else {
            //否则返回/data/data/<packageName>/cache
            return TestApp.getContext().getCacheDir();
        }
    }

    /**
     * 缓存文件目录
     *
     * @return 目录路径
     */
    public static String getCacheDirPath() {
        return getImageDirFile().getAbsolutePath();
    }

    /**
     * 图片文件目录
     *
     * @return 图片目录对象
     */
    public static File getImageDirFile() {
        File imageDir = new File(getRootDirFile(), "Image");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        return imageDir;
    }

    /**
     * 图片文件目录
     *
     * @return 图片目录路径
     */
    public static String getImageDirPath() {
        return getImageDirFile().getAbsolutePath();
    }

    /**
     * 下载文件， 会先判断是否已经下载，如果已经下载则会直接打开已下载的文件。
     *
     * @param context     上下文对象
     * @param downloadUrl 下载url
     */
    public static void startDownloadOrOpenDownloadedFile(Context context, String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
//        //判断任务是否创建
//        Long downlongdingId = DOWNLOAD_MAP.get(downloadUrl);
//        //有这个任务
//        if (downlongdingId != null) {
//            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//            //查询下载任务
//            DownloadManager.Query query = new DownloadManager.Query().setFilterById(downlongdingId);
//            Cursor cursor = manager.query(query);
//            if (cursor != null && cursor.moveToFirst()) {
//                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                //下载成功，判断下载文件是否存在，若存在则打开，若不存在则重新创建下载任务
//                if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                    //查询文件是否存在
//                    String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                    URI fileUri = URI.create(uriString);
//                    File file = new File(fileUri);
//                    if (file.exists()) {
//                        //文件存在，打开文件
//                        Log.d(TAG, "downloaded File is exists");
//                        openDownloadFileByDownloadId(context, downlongdingId);
//                        return;
//                    }
//                }
//                //下载中
//                else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING) {
//                    Log.d(TAG, "download task is running");
//                    new Handler(Looper.getMainLooper()).post(() -> ToastUtils.showToast(context, "正在下载中..."));
//                    return;
//                }
//            }
//
//        }
//        //创建新的下载任务
//        startDownload(context, downloadUrl);

        //上面的方法是根据id来判断，需要专门去保存id，故使用url方式查询是否已经下载完成
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //查询已经完成的任务
        Cursor cursor = manager.query(new DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL));
        //遍历查询结果
        while (cursor != null && cursor.moveToNext()) {
            String taskUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
            //判断是否该url对应的文件已经下载完成
            if (TextUtils.equals(taskUrl, downloadUrl)) {
                //查询文件是否存在
                String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                URI fileUri = URI.create(uriString);
                File file = new File(fileUri);
                if (file.exists()) {
                    //文件存在，打开文件
                    Log.d(TAG, "downloaded File is exists");
                    long downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                    openDownloadFileByDownloadId(context, downloadId);
                    return;
                }
                break;
            }
        }
        //创建新的下载任务
        startDownload(context, downloadUrl);
    }

    /**
     * 开始下载文件
     *
     * @param context     上下文对象
     * @param downloadUrl 下载链接
     */
    public static void startDownload(Context context, String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        Uri downloadUri;
        try {
            downloadUri = Uri.parse(downloadUrl);
            // 指定下载地址
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
            request.allowScanningByMediaScanner();
            // 设置通知的显示类型，下载进行时和完成后显示通知
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 允许在计费流量下下载
            request.setAllowedOverMetered(false);
            // 允许该记录在下载管理界面可见
            request.setVisibleInDownloadsUi(true);
            // 允许下载的网路类型
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            //下载路径
            String tempStr = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
            int indexParams = tempStr.indexOf("?");
            String name = indexParams == -1 ? tempStr : tempStr.substring(0, indexParams);
            String apkEnd = ".apk";
            if (!name.endsWith(apkEnd)) {
                name = name + apkEnd;
            }
            String fileName = name;
            //设置下载路径
            request.setDestinationUri((Uri.fromFile(new File(DownloadUtil.getDownloadDirFile(), fileName))));
            request.setTitle(fileName);
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
            //缓存任务id，用于DownloadReceiver 判断收到的下载完成广播中的下载任务是否是本app创建的
            DOWNLOAD_MAP.put(downloadUrl, downloadId);
            Log.d(TAG, "startDownload, downloadId=" + downloadId + ", fileName=" + fileName);
            new Handler(Looper.getMainLooper()).post(() -> ToastUtils.showToast(context, "开始下载" + fileName));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 根据下载id来打开下载的文件
     *
     * @param context    上下文
     * @param downloadId 下载id
     */
    public static void openDownloadFileByDownloadId(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
        Log.d(TAG, String.format("getMimeTypeForDownloadedFile:{%s}", type));
        if (TextUtils.isEmpty(type)) {
            type = "*/*";
        }
//            DownloadManager.Query query = new DownloadManager.Query();
//            Cursor cursor = downloadManager.query(query.setFilterById(downloadId));
//            if (cursor != null && cursor.moveToFirst()) {
//                String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                path = path.substring(path.indexOf("/storage"));
//                Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
//                handlerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                //必须添加此行代码，否则将出现解析包错误
//                handlerIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                String absolutePath = new File(path).getAbsoluteFile().getAbsolutePath();
////                handlerIntent.setDataAndType(uri, type);
//                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".rxdownload.provider", new File(absolutePath));
//                ;
//                handlerIntent.setDataAndType(uri, "application/vnd.android.package-archive");
//                context.startActivity(handlerIntent);
//
//            }
        Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
        if (uri != null) {
            Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
            //必须添加此行代码，否则将出现解析包错误
            handlerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            handlerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            handlerIntent.setDataAndType(uri, type);
//                handlerIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(handlerIntent);
        }
    }

}
