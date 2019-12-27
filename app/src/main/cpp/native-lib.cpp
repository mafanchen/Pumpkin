#include <jni.h>
#include <string>
#include "MD5.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_video_test_utils_EncryptUtils_md5FromJNI(JNIEnv *env, jobject,
                                                  jstring timeStamp, jstring ip) {
    const char *originIp;
    const char *originTime;
    //将 jstring 转化为 char*类型
    const char *salt = "QY2019@nangua!";

    originTime = env->GetStringUTFChars(timeStamp, JNI_FALSE);
    originIp = env->GetStringUTFChars(ip, JNI_FALSE);

    char keyStr[50];
    strcpy(keyStr, salt);
    strcat(keyStr, originTime);

    MD5 md5 = MD5(keyStr);
    std::string md5Result = md5.hexdigest();
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());

}

extern "C"
JNIEXPORT jstring JNICALL
Java_jaygoo_library_m3u8downloader_utils_NativeMD5Utils_md5FromJNI(JNIEnv *env, jobject,
                                                                   jstring timeStamp, jstring ip) {
    const char *originIp;
    const char *originTime;

    //将 jstring 转化为 char*类型
    const char *salt = "QY2019@nangua!";

    originTime = env->GetStringUTFChars(timeStamp, JNI_FALSE);
    originIp = env->GetStringUTFChars(ip, JNI_FALSE);

    char keyStr[50];
    strcpy(keyStr, salt);
    strcat(keyStr, originTime);

    MD5 md5 = MD5(keyStr);
    std::string md5Result = md5.hexdigest();
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_video_test_utils_EncryptUtils_keyFromJNI(JNIEnv *env, jobject) {
    const char *key = "5qDVqrIFASQ7NZSc";
    return env->NewStringUTF(key);

}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_video_test_utils_EncryptUtils_viFromJNI(JNIEnv *env, jobject) {
    const char *vi = "6666184153728923";
    return env->NewStringUTF(vi);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_video_test_utils_EncryptUtils_getSHA1FromJNI(JNIEnv *env, jobject) {
    const char *sha1 = "a22c004211ff741f4581be7eb2241b000b91a593";
    return env->NewStringUTF(sha1);
}







