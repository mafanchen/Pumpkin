package com.video.test;

import java.io.File;

/**
 * @author Enoch Created on 2018/6/25.
 */
public class AppConstant {


    public static final String SP_RECOMMEND_ID_KEY = "RECOMMEND_ID";

    /**
     * 请求码
     */
    public final static int INTENT_CODE_IMAGE_GALLERY = 1001;

    /**
     * WeChat Key
     */
    public static final String WECHAT_APP_ID = "wxaad0b70dd4766575";


    public static final int NG_LAST_REQUEST_URL = 4;


    /**
     * 老库用 8590  新的用 8591
     */
    public static final String BASE_PLAY_URL = "http://localhost:8591/playdata/";
    public static final String BASE_OLD_PLAY_URL = "http://localhost:8590/playdata/";
    public static final String CACHE_DIRECTORY = "/ffhd/FFMedia/";
    public static final String CACHE_FILE_PREFIX = "file:" + File.separator;


    /**
     * Android Device
     */
    public static final String ANDROID_OS = "Android_OS";
    public static final int ANDROID_TYPE = 1;
    public static final String ANDROID_BUG_ID = "9774d56d682e549c";


    /**
     * 网络请求相关
     */
    public static final int DEFAULT_TIMEOUT = 10;
    public static final int REQUEST_SUCCESS = 200;
    public static final int REQUEST_FAILED = 300;
    public static final int REQUEST_INVALID_TOKEN = 301;
    public static final int REQUEST_USED_PHONE = 400;
    public static final int REQUEST_LOGIN_MAXTIME = 3;
    public static final String LOGIN_TOKEN_INVALID = "登陆信息已失效，请您重新登陆";
    public static final String NETWORK_ERROR = "网络异常，请您重试";
    public static final String LOGIN_INFO_INVALID = "登陆信息异常,请您退出登陆后再次尝试";
    public static final String REQUEST_INFO_OK = "ok";
    public static final String REQUEST_INFO_START = "start";
    public static final String REQUEST_INFO_STOP = "stop";
    public static final String REQUEST_INFO_DELETE = "delete";
    public static final String FFHD_HEAD = "ffhd://";
    public static final String HTTP_HEAD = "http://";
    public static final String HTTPS_HEAD = "https://";


    /**
     * 用户中心
     */
    public static final String USER_VIP = "2";
    public static final String USER_NORMAL = "1";
    public static final String USER_VIP_EXPIRE = "3";
    public static final String USER_VIP_LASTDAY = "4";


    public static final int LAUNCH_NOT_FIRST = 0;
    public static final int LAUNCH_FIRST = 1;
    public static final int USER_COLLECTED = 1;
    public static final int USER_UNCOLLECT = 0;
    public static final long TRIAL_TIME = 900000L;
    public static final long REMIND_SPACE_SIZE = 1048576L;

    public static final int MOBILE_NETWORK_CAN_USE = 2;
    public static final int MOBILE_NETWORK_CAN_NOT_USE = 1;
    public static final int WIFI_NETWORK_CAN_USE = 3;
    public static final int SWAP_NUMBER = 6;

    public static final int WX_SCENE_SESSION = 0;
    public static final int WX_SCENE_TIMELINE = 1;
    public static final int WX_SCENE_FAVORITE = 2;
    public static final int TENCENT_QQ = 3;

    public static final int SOFT_INPUT_INVISIBLE = 0;

    public static final String BANNER_TYPE_VIDEO = "1";
    public static final String BANNER_TYPE_ROUTER = "2";
    public static final String BANNER_TYPE_WEBURL = "3";
    public static final String BANNER_TYPE_AD = "13";
    public static final String BANNER_TYPE_TOPIC = "21";
    public static final String TYPE_CLOSE_NOTICE = "97";

    public static final int LIST_POSITION_ZERO = 0;
    public static final int LIST_POSITION_ONE = 1;
    public static final int LIST_POSITION_TWO = 2;


    /*permission*/
    public static final int PERSSION_READ_PHONE_STATE = 10001;
    public static final int PERSSION_READ_EXTERNAL_STORAGE = 10002;
    public static final int PERSSION_WRITE_EXTERNAL_STORAGE = 10003;
    public static final int PERSSION_CAMERA = 10004;
    public static final int PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE = 10005;

    /*userCenter*/
    public static final String USER_INFO = "userInfo";
    public static final String USER_TOKEN = "userToken";
    public static final String USER_TOKEN_ID = "userTokenId";
    public static final String USER_TOKEN_LEVEL = "userLevel";
    public static final String USER_IS_LOGIN = "userIsLogin";
    public static final String LAUNCH_COUNT = "launchCount";
    public static final String USER_SHARE_URL = "userS hareUrl";
    public static final String SERVER_VERSION_CODE = "serverCode";


    /*VideoType*/
    public static final int VIDEO_TYPE_MOVIE = 3;
    public static final int VIDEO_TYPE_TELEPLAY = 4;
    public static final int VIDEO_TYPE_CARTOON = 5;
    public static final int VIDEO_TYPE_VARIETY = 6;


    /* M3U8 TASK STATUS */

    //默认状态
    public static final int M3U8_TASK_DEFAULT = 0;
    //下载排队
    public static final int M3U8_TASK_PENDING = -1;
    //下载准备中
    public static final int M3U8_TASK_PREPARE = 1;
    //下载中
    public static final int M3U8_TASK_DOWNLOADING = 2;
    //下载完成
    public static final int M3U8_TASK_SUCCESS = 3;
    //下载出错
    public static final int M3U8_TASK_ERROR = 4;
    //下载暂停
    public static final int M3U8_TASK_PAUSE = 5;
    //空间不足
    public static final int M3U8_TASK_ENOSPC = 6;

    public static final String M3U8_DEL_TASK_SUCCESS = "1";

    public static final String M3U8_DEL_TASK_ERROR = "4";

    public static final String M3U8_DEL_TASK_START = "2";

    public static final String M3U8_DEL_TASK_FAIL = "3";

    /**
     * LeCast ResultCode
     */

    public static final int LE_CAST_EMPTY_URL = 1;
    public static final int LE_CAST_GET_VIDEO_INFO = 2;
    public static final int LE_CAST_REJECT_CONNECT = 4;
    public static final int LE_CAST_GET_EDITTEXT_URL = 5;
    public static final int LE_CAST_MIRROR_ERROR = 10;
    public static final int LE_CAST_MIRROR_SUCCESS = 11;
    public static final int LE_CAST_MIRROR_DISCONNECT = 12;
    public static final int LE_CAST_CAN_NOT_FIND_DEVICE = 34;
    public static final int LE_CAST_CONNECTED_DEVICE = 35;

    /**
     * 首页视频分页列表页面的pid
     */
    //热门
    public static final int VIDEO_LIST_PID_HOT = 1;
    //电影
    public static final int VIDEO_LIST_PID_MOVIE = 3;
    //电视剧
    public static final int VIDEO_LIST_PID_TELEPLAY = 4;
    //动漫
    public static final int VIDEO_LIST_PID_CARTOON = 5;
    //综艺
    public static final int VIDEO_LIST_PID_VARIETYSHOW = 6;

    /**
     * 上报的广告类型
     */
    /**
     * 启动
     */
    public static final int AD_TYPE_SPLASH = 1;
    /**
     * 轮播
     */
    public static final int AD_TYPE_BANNER = 2;
    /**
     * 热门
     */
    public static final int AD_TYPE_RECOMMEND = 3;
    /**
     * 电影
     */
    public static final int AD_TYPE_MOVIE = 4;
    /**
     * 电视
     */
    public static final int AD_TYPE_TELEPLAY = 5;
    /**
     * 综艺
     */
    public static final int AD_TYPE_VARIETY = 6;
    /**
     * 动漫
     */
    public static final int AD_TYPE_CARTOON = 7;
    /**
     * 播放页
     */
    public static final int AD_TYPE_PLAYER = 8;
    /**
     * 片头
     */
    public static final int AD_TYPE_VIDEO_HEAD = 9;
    /**
     * 暂停
     */
    public static final int AD_TYPE_VIDEO_PAUSE = 10;
    /**
     * 小卡片
     */
    public static final int AD_TYPE_VIDEO_CARD = 11;


    /**
     * 当 应用切换到后台 到 再次切换到前台时，允许跳转到广告界面 的计时时间
     */
    public static final int TIME_PLAY_AD_WHEN_BACKGROUND = 5 * 60 * 1000;


    /**
     *  设置中的 储存变量
     */

    public static final String SWITCH_MOBILE_PLAY ="SettingSwitchMobilePlay";
    public static final String SWITCH_MOBILE_DOWN ="SettingSwitchMobileDown";
    public static final String SWITCH_HOMEPAGE_HISTORY ="SettingSwitchHomeHistory";
    public static final String SWITCH_PUSH_NOTICE ="SettingSwitchPushNotice";
    public static final String SWITCH_AUTO_PLAY ="SettingSwitchAutoPlay";

    /**
     * 快进 快退
     */

    public static final int VIDEO_FORWARD = 1;
    public static final int VIDEO_BACK = 2;
    public static final int PHONE_TYPE_ANDROID = 1;
    public static final int PHONT_TYPE_IOS = 2;


}
