package com.video.test.network;

import com.video.test.ApiUrl;
import com.video.test.javabean.ActivityGiftBean;
import com.video.test.javabean.AdBean;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.BannerAndNoticeListBean;
import com.video.test.javabean.BeanTopicListBean;
import com.video.test.javabean.BindPhoneBean;
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
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.javabean.VideoListBean;
import com.video.test.javabean.VideoPlayerBean;
import com.video.test.javabean.VideoRecommendBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by enoch on 11/10/2017.
 */

public interface ApiServer {

    @POST("App/Index/indexPid")
    Observable<BaseResult<List<IndexPidBean>>> getIndexPid();

    @POST("App/Index/getPlayAdInfo")
    Observable<BaseResult<VideoAdDataBean>> getVideoAd();

    @FormUrlEncoded
    @POST("App/UserInfo/indexPlay")
    Observable<BaseResult<VideoPlayerBean>> getVideoDetails(@FieldMap Map<String, String> fieldOption);

    @FormUrlEncoded
    @POST("App/Index/newIndex")
    Observable<BaseResult<HomePageVideoListBean>> getHomepage(@Field("pid") int pid);

    @FormUrlEncoded
    @POST("App/index/getColumnData")
    Observable<BaseResult<List<VideoRecommendBean>>> getRecommendVideo(@Field("parent_id") int parentId);

    @POST("App/Index/appIndex")
    Observable<BaseResult<HomePageVideoListBean>> getSimpleHomepage();

    @FormUrlEncoded
    @POST("App/Index/newIndex")
    Observable<BaseResult<BeanTopicListBean>> getHomepageBeanTopic(@Field("pid") int pid);

    @FormUrlEncoded
    @POST("App/Index/newBanner")
    Observable<BaseResult<BannerAndNoticeListBean>> getBannerAndNotice(@Field("pid") int pid, @Field("app_id") int appId, @Field("ad_version") int adVersion);

    @FormUrlEncoded
    @POST("App/Index/indexList")
    Observable<BaseResult<VideoListBean>> getVideoList(@Field("pid") int pid, @Field("tag") String tag, @Field("type") String type, @Field("page") int page, @Field("limit") int videoCount);

    @FormUrlEncoded
    @POST("App/Index/findMoreVod")
    Observable<BaseResult<SearchResultBean>> getSearchResult(@Field("keywords") String keywords, @Field("order_val") String sort);

    @POST("App/Index/findOrder")
    Observable<BaseResult<List<SearchSortTypeBean>>> getSortType();

    @FormUrlEncoded
    @POST("App/UserCollect/index")
    Observable<BaseResult<ListResult<CollectionListBean>>> getCollectionList(@Field("token") String token, @Field("token_id") String tokenId,
                                                                             @Field("page") int page, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("App/UserCollect/add")
    Observable<BaseResult<AddCollectionBean>> addCollectionList(@Field("token") String token, @Field("token_id") String tokenId, @Field("vod_id") String vodId);

    @FormUrlEncoded
    @POST("App/UserCollect/delete")
    Observable<BaseResult<String>> delCollectionList(@Field("token") String token, @Field("token_id") String tokenId, @Field("ids") String arrayIds);

    @FormUrlEncoded
    @POST("App/UserHistory/index")
    Observable<BaseResult<ListResult<HistoryListBean>>> getHistoryList(@Field("token") String token, @Field("token_id") String tokenId,
                                                                       @Field("page") int page, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("App/UserHistory/add")
    Observable<BaseResult<String>> addHistoryList(@Field("token") String token, @Field("token_id") String tokenId, @Field("vod_id") String vodId,
                                                  @Field("play_title") String videoTitle, @Field("play_url") String videoUrl, @Field("totaltime") long totalTime,
                                                  @Field("nowtime") long currentTime);

    @FormUrlEncoded
    @POST("App/UserHistory/delete")
    Observable<BaseResult<String>> delHistoryList(@Field("token") String token, @Field("token_id") String tokenId, @Field("ids") String arrayIds);

    @FormUrlEncoded
    @POST("App/UserInfo/userInfo")
    Observable<BaseResult<UserCenterBean>> getUserInfo(@Field("token") String token, @Field("token_id") String tokenId);

    @FormUrlEncoded
    @POST("App/User/feedback")
    Observable<BaseResult<String>> feedbackInfo(@Field("vod_id") String vodId, @Field("back_info") String info);

    @Multipart
    @POST(ApiUrl.UPLOAD)
    Observable<BaseResult<UploadAvatarBean>> uploadAvatar(@Part("token") RequestBody token, @Part("token_id") RequestBody tokenId, @Part() MultipartBody.Part file);

    @FormUrlEncoded
    @POST("App/UserInfo/updataPic")
    Observable<BaseResult<String>> updateAvatarUrl(@Field("token") String token, @Field("token_id") String tokenId, @Field("pic") String avatarUrl);


    @FormUrlEncoded
    @POST("App/UserInfo/getShareVip")
    Observable<BaseResult<ExchangeBean>> exchageVip(@Field("token") String token, @Field("token_id") String tokenId);

    @POST("App/VersionInfo/index")
    Observable<BaseResult<VersionInfoBean>> getVersionInfo();

    @POST("App/Config/splashInfo")
    Observable<BaseResult<SplashBean>> getSplashInfo();

    @FormUrlEncoded
    @POST("App/UserInfo/newShareInfo")
    Observable<BaseResult<ShareInfoBean>> getShareInfo(@Field("token") String token, @Field("token_id") String tokenId);

    @FormUrlEncoded
    @POST("App/UserInfo/shareExchange")
    Observable<BaseResult<ShareExchangeListBean>> getShareExchange(@Field("token") String token, @Field("token_id") String tokenId);

    @FormUrlEncoded
    @POST("App/UserInfo/getShareOrderList")
    Observable<BaseResult<ListResult<SwapHistoryBean>>> getSwapHistoryList(@Field("page") int page, @Field("limit") int limit);

    @FormUrlEncoded
    @POST("App/UserInfo/getShareVip")
    Observable<BaseResult<ActivityGiftBean>> getShareVip(@Field("token") String token, @Field("token_id") String tokenId, @Field("share_id") String shareId);

    @POST("App/Index/newActivity")
    Observable<BaseResult<List<HomeDialogBean>>> getHomeDialogData();

    @POST("App/VodHots/showHots")
    Observable<BaseResult<List<SearchHotWordBean>>> getHotwords();

    @FormUrlEncoded
    @POST("App/Index/indexComment")
    Observable<BaseResult<List<VideoCommentBean>>> getVideoComment(@Field("vod_id") String vodId);

    @FormUrlEncoded
    @POST("App/VodComment/addComment")
    Observable<BaseResult<String>> commentVideo(@Field("vod_id") String vodId, @Field("vod_comment") String content,
                                                @Field("token") String token, @Field("token_id") String tokenId);

    @POST("App/Index/getSysMess")
    Observable<BaseResult<List<NoticeBean>>> getSystemNotice();

    @FormUrlEncoded
    @POST("App/Index/backScreenData")
    Observable<BaseResult<ScreenResultBean>> getScreenVideos(@Field("page") int page, @Field("limit") int limnt,
                                                             @Field("pid") int videoPid, @Field("area_val") String area,
                                                             @Field("type_val") String type, @Field("year_val") String year,
                                                             @Field("play_val") String sort);

    @POST("App/Index/screenData")
    Observable<BaseResult<ScreenBean>> getScreenType();


    @FormUrlEncoded
    @POST("App/User/newLogin")
    Observable<BaseResult<LoginBean>> userLogin(@Field("old_key") String oldId,
                                                @Field("new_key") String newId,
                                                @Field("phone_type") int type,
                                                @Field("recommend") String recommendId);


    @FormUrlEncoded
    @POST("App/UserInfo/addMobile")
    Observable<BaseResult<BindPhoneBean>> bindPhone(@Field("country_code") String countryCode, @Field("mobile") String phone, @Field("captcha") String captcha,
                                                    @Field("is_must") String isForce, @Field("token") String token, @Field("token_id") String tokenId);

    @FormUrlEncoded
    @POST("/App/UserInfo/saveMobile")
    Observable<BaseResult<BindPhoneBean>> updatePhone(@Field("country_code") String countryCode, @Field("mobile") String phone, @Field("captcha") String captcha,
                                                      @Field("is_must") String isForce, @Field("token") String token, @Field("token_id") String tokenId);

    @FormUrlEncoded
    @POST("App/User/sendCap")
    Observable<BaseResult<String>> getCheckCode(@Field("country_code") String countryCode, @Field("mobile") String mobile);


    @POST("App/User/backInfo")
    Observable<BaseResult<List<String>>> getDomainUrls();

    /**
     * 获取反馈类型
     */
    @POST("App/Help/feedTag")
    Observable<BaseResult<List<FeedbackTypeBean>>> getFeedbackTypes();

    /**
     * 反馈
     *
     * @param feedType 类型
     * @param content  内容
     * @param image    图片地址
     */
    @FormUrlEncoded
    @POST("App/FeedInfo/addInfo")
    Observable<BaseResult<String>> commitFeedback(@Field("tag_id") String feedType,
                                                  @Field("feed_cont") String content,
                                                  @Field("contact_number") String contact,
                                                  @Field("feed_pic") String image);

    /**
     * 获取历史反馈
     */
    @POST("App/FeedInfo/feedHistory")
    Observable<BaseResult<List<FeedbackBean>>> getFeedbacks();

    /**
     * 增加资源的播放量、下载量、收藏量
     *
     * @param vodId 资源id
     * @param type  1播放量 2下载量 3收藏量
     */
    @FormUrlEncoded
    @POST("App/Index/addVodInfo")
    Observable<BaseResult> addVideoInfo(@Field("vod_id") String vodId, @Field("add_type") String type);

    @POST("App/Index/getSearchInfo")
    Observable<BaseResult<HotSearchWordListBean>> getHotSearchWord();

    @FormUrlEncoded
    @POST("App/Index/getPopulars")
    Observable<BaseResult<List<HottestVideoBean>>> getHottestVideos(@Field("show_id") String showId);

    /**
     * ad_type 广告类型    =1 启动 =2 轮播 =3 热门栏目 =4 电影栏目 =5 电视栏目 =6 综艺栏目 =7 动漫栏目 =8 播放页栏目 =9 片头 =10 暂停 =11 小卡片
     * ad_id  广告ID
     * app_id  应用ID =1 test1 =2 test2 =3 test3
     * app_type  手机类型 =1 安卓 =2 iOS
     * ad_version 广告版本号  1=> V1.3.3
     * user_id   会员ID
     * 返回参数 bool
     *
     * @return
     */
    @FormUrlEncoded
    @POST("App/Index/addAdInfo")
    Observable<BaseResult<Object>> addAdInfo(@Field("ad_type") int adType,
                                             @Field("ad_id") String adId,
                                             @Field("app_id") int appId,
                                             @Field("app_type") int appType,
                                             @Field("ad_version") int adVersion,
                                             @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("App/UserInfo/backUserAd")
    Observable<BaseResult<AdBean>> getUserCenterAdInfo(@Field("app_id") int appId, @Field("ad_version") int adVersion);
}




