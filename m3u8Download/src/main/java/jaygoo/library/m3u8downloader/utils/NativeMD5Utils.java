package jaygoo.library.m3u8downloader.utils;

/**
 * @author enoch, Created on 23/10/2017
 */

public class NativeMD5Utils {

    static {
        System.loadLibrary("native-lib");
    }

    private NativeMD5Utils() {
        throw new UnsupportedOperationException("EncryptUtils can't initialize");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 哈希加密相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Native MD5加密
     */
    public static native String md5FromJNI(String timeStamp, String ip);

}
