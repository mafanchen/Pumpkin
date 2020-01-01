package com.video.test.network;


import android.support.annotation.RawRes;
import android.text.TextUtils;
import android.util.Log;

import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.IModel;
import com.video.test.javabean.ActivityGiftBean;
import com.video.test.javabean.AdBean;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.BannerAndNoticeListBean;
import com.video.test.javabean.BeanTopicListBean;
import com.video.test.javabean.BindPhoneBean;
import com.video.test.javabean.CollectionBean;
import com.video.test.javabean.CollectionListBean;
import com.video.test.javabean.ExchangeBean;
import com.video.test.javabean.FeedbackBean;
import com.video.test.javabean.FeedbackTypeBean;
import com.video.test.javabean.HistoryListBean;
import com.video.test.javabean.HomeDialogBean;
import com.video.test.javabean.HomePageVideoListBean;
import com.video.test.javabean.HotSearchWordListBean;
import com.video.test.javabean.HottestVideoBean;
import com.video.test.javabean.IndexPidBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.NoticeBean;
import com.video.test.javabean.ProfilePictureBean;
import com.video.test.javabean.ScreenBean;
import com.video.test.javabean.ScreenResultBean;
import com.video.test.javabean.SearchHotWordBean;
import com.video.test.javabean.SearchResultBean;
import com.video.test.javabean.SearchSortTypeBean;
import com.video.test.javabean.ShareExchangeListBean;
import com.video.test.javabean.ShareInfoBean;
import com.video.test.javabean.SplashBean;
import com.video.test.javabean.SwapHistoryBean;
import com.video.test.javabean.UploadAvatarBean;
import com.video.test.javabean.UserCenterAdBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.javabean.VideoListBean;
import com.video.test.javabean.VideoPlayerBean;
import com.video.test.javabean.VideoRecommendBean;
import com.video.test.sp.SpUtils;
import com.video.test.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author enoch Created by Enoch on 2017/5/8.
 */

public class RetrofitHelper implements IModel {

    private static volatile RetrofitHelper sRetrofitHelper;
    private static ApiServer sRetrofitApiServer;
    /**
     * 当前的接口请求host
     *
     * @see BaseUrlInterceptor
     */
    static int sCurrentHostIndex = 0;


    public static RetrofitHelper getInstance() {
        if (sRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (sRetrofitHelper == null) {
                    sRetrofitHelper = new RetrofitHelper();
                }
            }
        }
        return sRetrofitHelper;
    }

    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            //设置缓存的最大生命时间
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            //设置缓存的过期时间
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (!NetworkUtils.isNetWorkAvailable(TestApp.getContext())) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isNetWorkAvailable(TestApp.getContext())) {
                int maxAge = 60;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    private RetrofitHelper() {
        File httpCacheDirectory = new File(TestApp.getContext().getCacheDir(), "response");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(AppConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //代理拦截
//                .addInterceptor(new ProxyInterceptor())
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(new BaseUrlInterceptor())
                .addInterceptor(new RetryInterceptor())
                .addInterceptor(new EncryptInterceptor())
                .cache(cache);
        //HTTPS
        if (!TextUtils.equals(BuildConfig.BUILD_TYPE, "uat")) {
            initSSLSocketFactory(okHttpClient);
        }
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient.build())
                .baseUrl(BuildConfig.NG_BASE_URLS[sCurrentHostIndex])
                .addConverterFactory(JsonConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        sRetrofitApiServer = retrofit.create(ApiServer.class);
    }

    private void initSSLSocketFactory(OkHttpClient.Builder okHttpClient) {
        try {
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
            okHttpClient.sslSocketFactory(sslSocketFactory);
            Log.d("RetrofitHelper", "set sslSocketFactory success");
        } catch (KeyStoreException e) {
            Log.d("RetrofitHelper", "set sslSocketFactory fail " + e.getMessage());
            e.printStackTrace();
        } catch (CertificateException e) {
            Log.d("RetrofitHelper", "set sslSocketFactory fail " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.d("RetrofitHelper", "set sslSocketFactory fail " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("RetrofitHelper", "set sslSocketFactory fail " + e.getMessage());
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.d("RetrofitHelper", "set sslSocketFactory fail " + e.getMessage());
            e.printStackTrace();
        }
    }

    private SSLSocketFactory getSSLSocketFactory() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, KeyManagementException {

        ArrayList<TrustManager> managerList = new ArrayList<>();
        addTrustManager(R.raw.test_nsmxkib_com, managerList);
        addTrustManager(R.raw.api_nsmxkib_com, managerList);
        TrustManager[] managers = new TrustManager[managerList.size()];
        managerList.toArray(managers);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, managers, null);
        return context.getSocketFactory();
    }

    private void addTrustManager(@RawRes int radId, @NotNull ArrayList<TrustManager> managerList) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
//        InputStream inputStream = TestApp.getContext().getResources().openRawResource(
//                BuildConfig.DEBUG ? R.raw.test_nsmxkib_com : R.raw.api_nsmxkib_com);
        InputStream inputStream = TestApp.getContext().getResources().openRawResource(radId);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(inputStream);
        inputStream.close();

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
        factory.init(keyStore);
        TrustManager[] trustManagers = factory.getTrustManagers();
        managerList.addAll(Arrays.asList(trustManagers));
    }


    /*
     ==========================================  Data API  ========================================================
     */


    /**
     * 获取顶栏数据
     */
    public Observable<BaseResult<List<IndexPidBean>>> getIndexPid() {
        return sRetrofitApiServer.getIndexPid();
    }

    /**
     * 获取视频广告
     *
     * @return
     */
    public Observable<BaseResult<VideoAdDataBean>> getVideoAd() {
        return sRetrofitApiServer.getVideoAd();
    }

    /**
     * 获取播放详情页
     */
    public Observable<BaseResult<VideoPlayerBean>> getVideoDetails(Map<String, String> fieldMap) {
        return sRetrofitApiServer.getVideoDetails(fieldMap);
    }

    /**
     * 获取首页的数据
     */
    public Observable<BaseResult<HomePageVideoListBean>> getHomepage(int pid) {
        return sRetrofitApiServer.getHomepage(pid);
    }

    public Observable<BaseResult<List<VideoRecommendBean>>> getRecommendVideo(int parentId) {
        return sRetrofitApiServer.getRecommendVideo(parentId);
    }

    public Observable<BaseResult<HomePageVideoListBean>> getSimpleHomePage() {
        return sRetrofitApiServer.getSimpleHomepage();
    }

    /**
     * 获取首页专题的的数据
     */
    public Observable<BaseResult<BeanTopicListBean>> getHomepageBeanTopic(int pid, int order) {
        return sRetrofitApiServer.getHomepageBeanTopic(pid, order);
    }


    /**
     * 获取Baneer 广告数据
     *
     * @param pid
     */
    public Observable<BaseResult<BannerAndNoticeListBean>> getBannerAndNotice(int pid) {
        return sRetrofitApiServer.getBannerAndNotice(pid, BuildConfig.APP_ID, BuildConfig.AD_VERSION);
    }

    /**
     * 获取专题播放详情页列表
     */

    public Observable<BaseResult<VideoListBean>> getTopicVideoList(int pid, String tag, String type, int page, int videoCount) {
        return sRetrofitApiServer.getTopicVideoList(BuildConfig.APP_ID, pid, tag, type, page, videoCount);
    }


    /**
     * 获取搜索结果
     */
    public Observable<BaseResult<SearchResultBean>> getSearchResult(String keywords, String sort) {
        return sRetrofitApiServer.getSearchResult(keywords, sort);
    }

    /**
     * 获取搜索排序
     */
    public Observable<BaseResult<List<SearchSortTypeBean>>> getSortType() {
        return sRetrofitApiServer.getSortType();
    }

    /**
     * 用户登录
     */
    public Observable<BaseResult<LoginBean>> userLogin(String oldId, String newId, String recommendId) {
        return sRetrofitApiServer.userLogin(oldId, newId, AppConstant.ANDROID_TYPE, recommendId);
    }

    /**
     * 获取会员收藏列表
     */
    public Observable<BaseResult<ListResult<CollectionListBean>>> getCollectionList(String token, String tokenId, int page, int limit) {
        return sRetrofitApiServer.getCollectionList(token, tokenId, page, limit);
    }

    /**
     * 添加会员收藏视频
     */

    public Observable<BaseResult<AddCollectionBean>> addCollectionList(String token, String tokenId, String vodId) {
        return sRetrofitApiServer.addCollectionList(token, tokenId, vodId);
    }


    /**
     * 删除会员收藏列表
     */

    public Observable<BaseResult<String>> delCollectionList(String token, String tokenId, String arrayIds) {
        return sRetrofitApiServer.delCollectionList(token, tokenId, arrayIds);
    }

    /**
     * 获取播放历史
     */

    public Observable<BaseResult<ListResult<HistoryListBean>>> getHistoryList(String token, String tokenId, int page, int limit) {
        return sRetrofitApiServer.getHistoryList(token, tokenId, page, limit);
    }

    /**
     * 添加播放历史
     */

    public Observable<BaseResult<String>> addHistoryList(String token, String tokenId, String vodId, String videoTitle,
                                                         String videoUrl, long totalTime, long currentTime) {
        return sRetrofitApiServer.addHistoryList(token, tokenId, vodId, videoTitle, videoUrl, totalTime, currentTime);
    }

    /**
     * 删除播放历史
     */

    public Observable<BaseResult<String>> delHistoryList(String token, String tokenId, String arrayIds) {
        return sRetrofitApiServer.delHistoryList(token, tokenId, arrayIds);
    }

    /**
     * 获取用户信息
     */

    public Observable<BaseResult<UserCenterBean>> getUserInfo(String token, String tokenId) {
        return sRetrofitApiServer.getUserInfo(token, tokenId);
    }

    /**
     * 反馈播放问题
     */

    public Observable<BaseResult<String>> feedbackInfo(String vodId, String info) {
        return sRetrofitApiServer.feedbackInfo(vodId, info);
    }


    /**
     * 上传头像
     */

    public Observable<BaseResult<UploadAvatarBean>> uploadAvatar(RequestBody token, RequestBody tokenId, MultipartBody.Part file) {
        return sRetrofitApiServer.uploadAvatar(token, tokenId, file);
    }

    /**
     * 更新头像地址
     */

    public Observable<BaseResult<String>> updateAvatarUrl(String token, String tokenId, String avatarUrl) {
        return sRetrofitApiServer.updateAvatarUrl(token, tokenId, avatarUrl);
    }


    /**
     * 兑换Vip功能
     */
    public Observable<BaseResult<ExchangeBean>> exchageVip(String token, String tokenId) {
        return sRetrofitApiServer.exchageVip(token, tokenId);
    }

    /**
     * 获取版本信息
     */

    public Observable<BaseResult<VersionInfoBean>> getVersionInfo() {
        return sRetrofitApiServer.getVersionInfo();
    }


    /**
     * 获取闪屏页广告等信息
     */

    public Observable<BaseResult<SplashBean>> getSplashInfo() {
        return sRetrofitApiServer.getSplashInfo();
    }

    /**
     * 获取分享页信息
     */

    public Observable<BaseResult<ShareInfoBean>> getShareInfo(String token, String tokenId) {
        return sRetrofitApiServer.getShareInfo(token, tokenId);
    }

    /**
     * 获取分享兑换信息
     */
    public Observable<BaseResult<ShareExchangeListBean>> getShareExchange(String token, String tokenId) {
        return sRetrofitApiServer.getShareExchange(token, tokenId);
    }

    /**
     * 获取兑换vip历史信息
     */
    public Observable<BaseResult<ListResult<SwapHistoryBean>>> getSwapHistoryList(int page, int limit) {
        return sRetrofitApiServer.getSwapHistoryList(page, limit);
    }

    /**
     * 获取特权会员
     */
    public Observable<BaseResult<ActivityGiftBean>> getShareVip(String token, String tokenId, String shareId) {
        return sRetrofitApiServer.getShareVip(token, tokenId, shareId);
    }

    /**
     * 获取首页活动对话信息
     */

    public Observable<BaseResult<List<HomeDialogBean>>> getHomeDialogData() {
        return sRetrofitApiServer.getHomeDialogData();
    }

    /**
     * 获取热搜词
     */
    public Observable<BaseResult<List<SearchHotWordBean>>> getHotWords() {
        return sRetrofitApiServer.getHotwords();
    }

    /**
     * 获取电影评论
     */

    public Observable<BaseResult<List<VideoCommentBean>>> getVideoComment(String vodId) {
        return sRetrofitApiServer.getVideoComment(vodId);

    }

    /**
     * 评论视频
     */

    public Observable<BaseResult<String>> commentVideo(String vodId, String content, String token, String tokenId) {
        return sRetrofitApiServer.commentVideo(vodId, content, token, tokenId);
    }


    /**
     * 获取系统通知消息
     */

    public Observable<BaseResult<List<NoticeBean>>> getSystemNotice() {
        return sRetrofitApiServer.getSystemNotice();
    }


    /**
     * 获取筛选的类型
     */

    public Observable<BaseResult<ScreenBean>> getScreenType() {
        return sRetrofitApiServer.getScreenType();
    }


    /**
     * 获取筛选后的电影类型
     */

    public Observable<BaseResult<ScreenResultBean>> getScreenResult(int page, int limnt, int videoPid,
                                                                    String area, String type, String year, String sort) {
        return sRetrofitApiServer.getScreenVideos(page, limnt, videoPid, area, type, year, sort);
    }


    /**
     * 用户绑定手机号码
     */

    public Observable<BaseResult<BindPhoneBean>> bindPhone(String countryCode, String phone, String captcha,
                                                           String isForce, String token, String tokenId) {
        return sRetrofitApiServer.bindPhone(countryCode, phone, captcha, isForce, token, tokenId);
    }


    /**
     * 修改用户手机号码
     */

    public Observable<BaseResult<BindPhoneBean>> updatePhone(String countryCode, String phone, String captcha,
                                                             String isForce, String token, String tokenId) {
        return sRetrofitApiServer.updatePhone(countryCode, phone, captcha, isForce, token, tokenId);
    }


    /**
     * 获取验证码
     */
    public Observable<BaseResult<String>> getCheckCode(String countryCode, String mobile) {
        return sRetrofitApiServer.getCheckCode(countryCode, mobile);
    }


    /**
     * 返回域名列表
     */

    public Observable<BaseResult<List<String>>> getDomianUrls() {
        return sRetrofitApiServer.getDomainUrls();
    }

    public Observable<BaseResult<List<FeedbackTypeBean>>> getFeedbackTypes() {
        return sRetrofitApiServer.getFeedbackTypes();
    }

    public Observable<BaseResult<String>> commitFeedback(@NotNull String feedType, @NotNull String content, @Nullable String contact,
                                                         @Nullable String image, @Nullable String vodId, @Nullable String phoneInfo) {
        return sRetrofitApiServer.commitFeedback(feedType, content, contact, image, vodId, phoneInfo);
    }

    public Observable<BaseResult<List<FeedbackBean>>> getFeedbacks() {
        return sRetrofitApiServer.getFeedbacks();
    }

    /**
     * 增加资源的播放量、下载量、收藏量
     *
     * @param vodId 资源id
     * @param type  1播放量 2下载量 3收藏量
     */
    public Observable<BaseResult> addVideoInfo(String vodId, String type) {
        return sRetrofitApiServer.addVideoInfo(vodId, type);
    }

    public Observable<BaseResult<HotSearchWordListBean>> getHotSearchWord() {
        return sRetrofitApiServer.getHotSearchWord();
    }

    public Observable<BaseResult<List<HottestVideoBean>>> getHottestVideos(String showId) {
        return sRetrofitApiServer.getHottestVideos(showId);
    }

    public Observable<BaseResult<Object>> addAdInfo(int adType, String adId) {
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        return sRetrofitApiServer.addAdInfo(adType, adId, BuildConfig.APP_ID, 1, BuildConfig.AD_VERSION, userTokenId);
    }

    public Observable<BaseResult<Object>> getClewWord(int searchWord) {
        return sRetrofitApiServer.getClewWord(searchWord);
    }

    public Observable<BaseResult<String>> uploadWatchTime(String token, String tokenId, String version, String cid, String pid) {
        return sRetrofitApiServer.uplodaWatchTime(BuildConfig.APP_ID, token, tokenId, version, "1", cid, pid);
    }

    public Observable<BaseResult<AddCollectionBean>> addTopicCollection(String token, String tokenId, String topicId) {
        return sRetrofitApiServer.addTopicCollection(token, tokenId, BuildConfig.APP_ID, topicId);
    }

    public Observable<BaseResult<String>> delTopicCollection(String token, String tokenId, String topicArrayIds) {
        return sRetrofitApiServer.delTopicCollection(token, tokenId, BuildConfig.APP_ID, topicArrayIds);
    }

    public Observable<BaseResult<String>> updateBackOrForward(String clickType, String clickVersion) {
        return sRetrofitApiServer.updateBackOrForward(clickType, clickVersion, "1");
    }

    public Observable<BaseResult<List<ProfilePictureBean>>> getProfilePics() {
        return sRetrofitApiServer.getProfilePics();
    }

    public Observable<BaseResult<String>> updateProfilePic(String picId, String token, String tokenId) {
        return sRetrofitApiServer.updateProfilePic(BuildConfig.APP_ID, picId, token, tokenId);
    }

    public Observable<BaseResult<CollectionBean>> getAllCollection(String token, String tokenId) {
        return sRetrofitApiServer.getAllCollection(BuildConfig.APP_ID, token, tokenId);
    }

    public Observable<BaseResult<UserCenterAdBean>> getUserCenterAd(String adChannel, String token, String tokenId) {
        return sRetrofitApiServer.getUserCenterAd(BuildConfig.APP_ID, adChannel, token, tokenId);
    }

    public Observable<BaseResult<AdBean>> getUserCenterAdInfo() {
        return sRetrofitApiServer.getUserCenterAdInfo(BuildConfig.APP_ID, BuildConfig.AD_VERSION);
    }

}

