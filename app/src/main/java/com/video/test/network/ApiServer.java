package com.video.test.network;

import com.video.test.ApiUrl;
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

    /**
     * @param pid   专题pid 都填2
     * @param order 专题排序 1=> 权重排序 , 2=> 最新排序
     * @return
     */
    @FormUrlEncoded
    @POST("App/Index/newIndex")
    Observable<BaseResult<BeanTopicListBean>> getHomepageBeanTopic(@Field("pid") int pid, @Field("order") int order);


    /**
     * @param pid       栏目id
     * @param appId     app ID
     * @param adChannel 广告渠道标识符
     * @return
     */
    @FormUrlEncoded
    @POST("App/Index/newBanner")
    Observable<BaseResult<BannerAndNoticeListBean>> getBannerAndNotice(@Field("pid") int pid, @Field("app_id") int appId, @Field("ad_version") int adChannel);


    @FormUrlEncoded
    @POST("App/Index/indexList")
    Observable<BaseResult<VideoListBean>> getTopicVideoList(@Field("app_id") int addId, @Field("pid") int pid, @Field("tag") String tag, @Field("type") String type, @Field("page") int page, @Field("limit") int videoCount);

    /**
     * @param keywords
     * @param sort     排序 1=> 综合 ,2=> 上映时间 ,3=>播放量, 4=> 评分
     * @return
     */
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

    // TODO 缺字段
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

    /**
     * 获取筛选页面上方筛选词的词组
     *
     * @return
     */
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
     * @param feedType   类型
     * @param content    内容
     * @param image      图片地址
     * @param vod_id     反馈的电影id  从播放页点击反馈时，传此值，可以为空
     * @param phone_init 机型品牌及类型
     */
    @FormUrlEncoded
    @POST("App/FeedInfo/addInfo")
    Observable<BaseResult<String>> commitFeedback(@Field("tag_id") String feedType, @Field("feed_cont") String content, @Field("contact_number") String contact,
                                                  @Field("feed_pic") String image, @Field("vod_id") String vodId, @Field("phone_init") String phoneInfo);

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
     * ad_type 广告类型 广告类型 1=>启动,2=>轮播,3=>热门栏目, 4=>电影栏目,5=>电视栏目,6=>综艺栏目,7=>动漫栏目,8=>播放页栏目,9=>片头,10=>暂停,11=>小卡片
     * ad_id  广告ID
     * app_id  应用ID =1 test1 =2 test2 =3 test3
     * app_type  手机类型 =1 安卓 =2 iOS
     * ad_version 统计广告版本号  1=> V1.3.3
     * user_id   会员ID  TODO 接口参数为 token_id
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

    //
    @FormUrlEncoded
    @POST("App/Index/backThinkVod")
    Observable<BaseResult<List<String>>> getClewWord(@Field("words") int searchWord);


    /**
     * @param version 版本号 1=>1.3.3 , 2=>1.3.4 ,3=>1.3.5
     * @param appType 手机类型 1=>安卓, 2=>iOS
     * @param cid     资源类型ID
     * @param pid     栏目id 1=>热门, 3=> 电影, 4=> 电视剧 ,5=>动漫 ,6=>综艺
     * @return
     */
    @FormUrlEncoded
    @POST("App/Index/addWatchInfo")
    Observable<BaseResult<String>> uploadWatchTime(@Field("version") String version, @Field("app_type") String appType,
                                                   @Field("cid") String cid, @Field("pid") String pid);


    /**
     * @param appId      appID
     * @param adVersion  广告渠道号  用来判断这个广告渠道号的广告是否展示
     * @return
     */

    @FormUrlEncoded
    @POST("App/UserInfo/backUserAd")
    Observable<BaseResult<UserCenterAdBean>> getUserCenterAd(@Field("ad_version") String adChannel);

    /**
     * JAVABEAN 可以与之前的普通收藏复用
     *
     * @param token
     * @param tokenId
     * @param topicId 专题的ID
     * @return
     */
    @FormUrlEncoded
    @POST("App/UserCollect/addZt")
    Observable<BaseResult<AddCollectionBean>> addTopicCollection(@Field("token") String token, @Field("token_id") String tokenId,
                                                                 @Field("app_id") int appId, @Field("zt_id") String topicId);

    /**
     * @param token
     * @param tokenId
     * @param topicArrayIds 专题的ID 要使用 jsonArray 样式 可传入多个 例如  {"ids":"[\"1\"]"}
     * @return
     */
    @FormUrlEncoded
    @POST("App/UserCollect/deleteZt")
    Observable<BaseResult<String>> delTopicCollection(@Field("token") String token, @Field("token_id") String tokenId,
                                                      @Field("app_id") int appId, @Field("ids") String topicArrayIds);

    /**
     * TODO 一直报点击类型错误
     * 快退 快进键统计
     *
     * @param clickType    点击类型 1=> 前进 ,2=> 后退
     * @param clickVersion 上报版本 1=>1.3.3 , 2=>1.3.4 ,3=>1.3.5
     * @param appType      手机类型 1=>android , 2=> iOS
     * @return
     */
    @FormUrlEncoded
    @POST("App/Index/addButInfo")
    Observable<BaseResult<String>> updateBackOrForward(@Field("click_type") String clickType, @Field("click_version") String clickVersion,
                                                       @Field("app_type") String appType);


    /**
     * 获取服务器配置好的头像组
     *
     * @return
     */
    @FormUrlEncoded
    @POST("App/User/backUserPic")
    Observable<BaseResult<List<ProfilePictureBean>>> getProfilePics();


    /**
     * @param picID 头像的ID
     * @return
     */
    @FormUrlEncoded
    @POST("App/UserInfo/newUpdatePic")
    Observable<BaseResult<String>> updateProfilePic(@Field("id") String picId);


    /**
     * 获取用户分类别的全部收藏
     *
     * @return
     */
    @POST("App/UserCollect/newIndex")
    Observable<BaseResult<CollectionBean>> getAllCollection();


    @FormUrlEncoded
    @POST("App/UserInfo/backUserAd")
    Observable<BaseResult<AdBean>> getUserCenterAdInfo(@Field("app_id") int appId, @Field("ad_version") int adVersion);


}




