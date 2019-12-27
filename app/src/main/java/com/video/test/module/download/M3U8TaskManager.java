package com.video.test.module.download;

/**
 * @author Enoch Created on 2019/1/25.
 */
public final class M3U8TaskManager {
    private static volatile M3U8TaskManager mInstance;

    private static M3U8TaskManager getInstance() {
        if (null == mInstance) {
            synchronized (M3U8TaskManager.class) {
                if (null == mInstance) {
                    mInstance = new M3U8TaskManager();
                }
            }
        }
        return mInstance;
    }

    public static void startNewTask() {

    }

    public static void stopTask() {

    }

    public static void deleteTask() {

    }

}
