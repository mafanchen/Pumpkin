package com.video.test.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import com.video.test.TestApp;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Enoch on 2017/3/10.
 */

public class SDCardUtils {

    /*仅能使用静态方法*/
    private SDCardUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    /**
     * 获取SD卡的状态
     */
    public static String getSDCardState() {
        return Environment.getExternalStorageState();
    }

    /**
     * SD卡是否可用
     *
     * @return 只有当SD卡已经安装并且准备好了才返回true
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(getSDCardState());
    }

    /**
     * 获取sd卡根路径
     *
     * @return StringPath
     */
    public static String getSDRootPath() {
        String sdDir = null;
        //判断sd卡是否存在
        boolean sdCardExist = isSDCardAvailable();
        if (sdCardExist) {
            //获取根目录
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdDir;
    }


    /**
     * Return whether sdcard is enabled.
     *
     * @return true : enabled<br>false : disabled
     */
    public static boolean isSDCardEnable() {
        return !getSDCardPaths().isEmpty();
    }

    /**
     * Return the paths of sdcard.
     *
     * @param removable True to return the paths of removable sdcard, false otherwise.
     * @return the paths of sdcard
     */
    public static List<String> getSDCardPaths(final boolean removable) {
        List<String> paths = new ArrayList<>();
        StorageManager sm =
                (StorageManager) TestApp.getContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(sm);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean res = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable == res) {
                    paths.add(path);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * Return the paths of sdcard.
     *
     * @return the paths of sdcard
     */
    public static List<String> getSDCardPaths() {
        StorageManager storageManager = (StorageManager) TestApp.getContext()
                .getSystemService(Context.STORAGE_SERVICE);
        List<String> paths = new ArrayList<>();
        try {
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object invoke = getVolumePathsMethod.invoke(storageManager);
            paths = Arrays.asList((String[]) invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }


    /**
     * 获取SD卡空闲的空间大小
     *
     * @param cachePath 缓存的目录
     * @return 可用空间大小
     */
    public static long getFreeSpaceBytes(String cachePath) {
        long freeSpaceBytes;
        final StatFs statFs = new StatFs(cachePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            freeSpaceBytes = statFs.getAvailableBytes();
        } else {
            //noinspection deprecation
            freeSpaceBytes = statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }
        return freeSpaceBytes;
    }

    /**
     * 获取SD卡总的空间大小
     *
     * @param cachePath 文件的路径
     * @return 总的空间大小
     */
    public static long getTotalSpaceBytes(String cachePath) {
        long totalSpaceBytes;
        final StatFs statFs = new StatFs(cachePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalSpaceBytes = statFs.getTotalBytes();
        } else {
            //noinspection deprecation
            totalSpaceBytes = statFs.getBlockCount() * (long) statFs.getBlockSize();
        }
        return totalSpaceBytes;
    }

    /*格式化获取的Bytes值.转换成GB/MB等*/
    public static String getSizeString(double nBytes) {
        double fBytes = nBytes * 1.0;
        if (fBytes / 1024 / 1024 / 1024 > 1) {
            double f = fBytes / 1024 / 1024 / 1024;
            return String.format(Locale.US, "%.1fGB", f);
        } else if (fBytes / 1024 / 1024 > 1) {
            double f = fBytes / 1024 / 1024;
            return String.format(Locale.US, "%.1fMB", f);
        } else if (fBytes / 1024 > 1) {
            double f = fBytes / 1024;
            return String.format(Locale.US, "%.1fKB", f);
        } else {
            return String.format(Locale.US, "%.1fBytes", nBytes);
        }
    }


}
