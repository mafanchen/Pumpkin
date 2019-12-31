package com.video.test.module.player;

import android.content.res.Resources;
import android.widget.EditText;
import android.widget.TextView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.javabean.VideoPlayerBean;
import com.video.test.network.BaseResult;
import com.video.test.ui.widget.LandLayoutVideo;
import com.video.test.ui.widget.TimeCloseDialogFragment;
import com.video.test.utils.cast.LelinkHelper;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface PlayerContract {

    interface Model extends IModel {
        Observable<VideoPlayerBean> getVideoPlayerContent(Map<String, String> queryMap);

        Observable<AddCollectionBean> addCollections(String vodId, String userToken, String userTokenId);

        Observable<String> delCollections(String ids, String userToken, String userTokenId);

        Observable<String> addHistory(String vodId, String videoName, String palyUrl, long currentTime, long totalTime,
                                      String userToken, String userTokenId);

        Observable<List<VideoCommentBean>> getVideoComment(String videoId);

        Observable<String> commentVideo(String vodId, String content, String token, String tokenId);

        Observable<String> feedbackSuggest(String vodId, String suggestMessage);

        Observable<Long> getSDCardFreeSize();

        Observable<VideoAdDataBean> getVideoAd();

        Observable<BaseResult> addVideoInfo(String vodId, String type);

        Observable<BaseResult<Object>> addAdInfo(int adType, String adId);
    }

    interface View extends IView {

        void getVideoPlayerContentSuccess(VideoPlayerBean videoPlayerBean);

        void addHistoryBeanSuccess(String message);

        void feedbackMessageSuccess(String message);

        void setSwipeRefreshStatus(Boolean status);

        void startVideoPlay(String videoPlayUrl, String videoName, String videoUrl);

        void startPlayLogic();

        void setNetSpeed(String netSpeed);

        void setVideoComment(List<VideoCommentBean> videoCommentBeans);

        void getSelectedItemPosition(int selectedPosition);

        void addCollectionSuccess(AddCollectionBean addCollectionBean);

        void showMobileNetworkDialog();

        void showMobileNetworkDownloadDialog(String downloadUrl, String videoId, String videoName, String videoItemName);

        void showDownloadFunctionDialog();

        void showCastFunctionDialog();

        void showNetworkErrorView();

        void isOpenMobileSwitch();

        void finishActivity();

        void showWaitPic();

        void hideWaitPic();

        void showCleanCacheDialog();

        void showSofaBackground();

        void hideSofaBackground();

        void updateBrowseAdapter();

        void videoReset();

        void setVideoCollected(boolean isCollected);

        void setVideoUrl(String playUrl);

        void setDegree(String degree);

        void setPlayerCloseTimerText(String time);

        void setPlayerCloseTimeStatus(int closeTime);

        void showTimerCloseDialog(TimeCloseDialogFragment.OnButtonClickListener onButtonClickListener);

        void playNext();

        void showPlayNextNotice();

        void pausePlay();

        void resumePlay();

        void resetTimerCloseText();

        void uploadWatchTime();

        void setAdData(VideoAdDataBean videoAdDataBean);

        String getVideoId();

        PlayerDownloadSelectItemAdapter getDownloadAdapter();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getVideoPlayerContent(String videoId);

        abstract void addCollections(String vodId);

        abstract void delCollections(String ids);

        abstract void addHistory(String vodId, String videoTitle, String videoName, String playUrl, long currentTime, long totalTime);

        abstract void getVideoComment(String videoId);

        abstract void feedbackSuggest(String vodId, String suggestMessage);

        abstract void getVideoPlayUrl(String videoUrl, String videoName);

        abstract void castVideoUrl(LelinkHelper helper, String videoUrl, String videoName);

        abstract void commentVideo(String vodId, EditText etContent, TextView tvComment);

        abstract void isMobileNetwork();

        abstract void isMobileNetworkDownload(String downloadUrl, String videoId, String videoName, String videoItemName);

        abstract void setDownloadUrl(String downloadUrl, String videoId, String videoName, String videoItemName);

        abstract void setItemSelectedPosition(int selectedPosition);

        abstract void getSDCardFreeSize();

        abstract void initWeChatApi();

        abstract void share2WeChat(Resources resources, String shareUrl, int targetSession);

        abstract void getCurrentNetSpeed(LandLayoutVideo landLayoutVideo);

        abstract void initBrowserListener();

        abstract void updateDeviceAdapter();

        abstract boolean isContains(LelinkServiceInfo selectInfo, LelinkServiceInfo info);

        abstract void startCloseTimer(int minutes);

        abstract void stopCloseTimer();

        abstract void onTimerClose();

        abstract void onDestroy();

        abstract void initCloseTimer();

        abstract void addPlayCount(String vodId);

        abstract void addDownloadCount(String vodId);

        abstract void addCollectCount(String vodId);

        abstract void addAdInfo(int adType, @Nullable String adId);

        abstract void deleteDownloadTask(String videoUrl);

        abstract void watchVideoTimer(LandLayoutVideo landLayoutVideo);

        abstract void cancelWatchVideoTimer();
    }
}
