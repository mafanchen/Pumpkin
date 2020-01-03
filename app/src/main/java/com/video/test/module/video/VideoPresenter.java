package com.video.test.module.video;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.HistoryListBean;
import com.video.test.javabean.ScreenBean;
import com.video.test.module.videorecommend.VideoRecommendFragment;
import com.video.test.module.videotype.BaseVideoTypeListFragment;
import com.video.test.module.videotype.SpecialFragment;
import com.video.test.module.videotype.VideoTypeListFragment;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoPresenter extends VideoContract.Presenter<VideoModel> {
    private static final int THUMB_SIZE = 150;
    private static final String TAG = "VideoRecommendPresenter";
    private IWXAPI mWxApi;
    private List<String> mHotSearchWordList = new ArrayList<>();
    private Disposable mHotSearchWordTimer;
    private String mHotSearchWord;
    private Map<String, List<String>> mHotSearchWordMap;

    @Override
    public void subscribe() {

    }


    @Override
    ArrayList<Fragment> initFragmentList() {

        ArrayList<Fragment> fragments = new ArrayList<>();

        Fragment specialFragment = SpecialFragment.Companion.newInstance();
        Fragment recommendFragment = VideoRecommendFragment.newInstance();
        Fragment movieFragment = VideoTypeListFragment.Companion.newInstance(AppConstant.VIDEO_LIST_PID_MOVIE, AppConstant.AD_TYPE_MOVIE);
        Fragment teleplayFragment = VideoTypeListFragment.Companion.newInstance(AppConstant.VIDEO_LIST_PID_TELEPLAY, AppConstant.AD_TYPE_TELEPLAY);
        Fragment cartoonFragment = VideoTypeListFragment.Companion.newInstance(AppConstant.VIDEO_LIST_PID_CARTOON, AppConstant.AD_TYPE_CARTOON);
        Fragment varietyShowFragment = VideoTypeListFragment.Companion.newInstance(AppConstant.VIDEO_LIST_PID_VARIETYSHOW, AppConstant.AD_TYPE_VARIETY);

        fragments.add(specialFragment);
        fragments.add(recommendFragment);
        fragments.add(movieFragment);
        fragments.add(teleplayFragment);
        fragments.add(varietyShowFragment);
        fragments.add(cartoonFragment);

        return fragments;
    }

    @Override
    void initWeChatApi() {
        mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
    }

    @Override
    void share2WeChat(Resources resources, String shareUrl, int targetSession) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject(shareUrl);
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = TestApp.getContext().getString(R.string.share_invite_title);
        message.description = TestApp.getContext().getString(R.string.share_invite_description);
        Bitmap logo = BitmapFactory.decodeResource(resources, R.mipmap.ic_logo);
        Bitmap thumbLogo = Bitmap.createScaledBitmap(logo, THUMB_SIZE, THUMB_SIZE, true);
        logo.recycle();
        message.thumbData = WeChatUtil.bmpToByteArray(thumbLogo, true);

        //构造一个 Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = targetSession;
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
        }
        if (mWxApi.isWXAppInstalled()) {
            mWxApi.sendReq(req);
        } else {
            ToastUtils.showToast(TestApp.getContext(), "您还未安装微信");
        }
    }


    @Override
    void getScreenTypes() {
        Disposable disposable = mModel.getScreenTypes()
                .doOnNext(new Consumer<ScreenBean>() {
                    @Override
                    public void accept(ScreenBean screenBean) {
                        LogUtils.d(TAG, "getScreenTypes Thread ==" + Thread.currentThread().getName());

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ScreenBean>() {
                    @Override
                    public void accept(ScreenBean screenBean) {
                        LogUtils.d(TAG, "getScreenTypes success Thread == " + Thread.currentThread().getName());
                        mView.setScreenTypes(screenBean);

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getScreenTypes Error = " + throwable.getMessage());


                    }
                }));
        addDisposable(disposable);
    }

    /**
     * 获取Fragment的实例
     *
     * @param fragmentPath
     * @return
     */
    private Fragment getFragment(String fragmentPath) {
        return (Fragment) ARouter.getInstance().build(fragmentPath).navigation();
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 获取历史纪录
     */
    public void getHistory() {
        boolean isOpenHistory = SpUtils.getBoolean(TestApp.getContext(), AppConstant.SWITCH_HOMEPAGE_HISTORY, true);
        if (!isOpenHistory) {
            return;
        }
        String vodId = SpUtils.getString(TestApp.getContext(), "historyVodId", "");
        if (TextUtils.isEmpty(vodId)) {
            return;
        }
        Disposable subscribe =
                Observable.create(
                        (ObservableOnSubscribe<HistoryListBean>) emitter -> {
                            DBManager manager = DBManager.getInstance(TestApp.getContext());
                            HistoryListBean bean = manager.queryHistoryByVodId(vodId);
                            if (bean != null) {
                                emitter.onNext(bean);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((Function<HistoryListBean, ObservableSource<Long>>) bean -> {
                            mView.showHistoryLayout();
                            mView.initHistoryLayout("正在播放" + bean.getVod_name(), getTimeString(Long.parseLong(bean.getNowtime())), bean.getVod_id());
                            return Observable.timer(10, TimeUnit.SECONDS);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> mView.hideHistoryLayout());
        addDisposable(subscribe);
    }

    private String getTimeString(long time) {
        long seconds = time / 1000;
        long second = seconds % 60;
        long minutes = seconds / 60;
        long minute = minutes % 60;
        long hours = minutes / 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minute, second);
    }

    @Override
    void getHotSearchWord() {
        Disposable subscribe = mModel.getHotSearchWord()
                .subscribe(bean -> {
                            mHotSearchWordMap = bean.getData();
                            String pid = getCurrentPagePid();
                            onChangeHotSearchWord(pid);
                        }
                        , e -> Log.e(TAG, "getHotSearchWord Error," + e.getMessage())
                );
        addDisposable(subscribe);
    }

    private String getCurrentPagePid() {
        Fragment fragment = mView.getCurrentFragment();
        if (fragment instanceof BaseVideoTypeListFragment) {
            return String.valueOf(((BaseVideoTypeListFragment) fragment).getPid());
        }
        return "1";
    }

    /**
     * 搜索框热词切换，每隔十秒钟切换一次
     */
    private void startHotSearchWordTimer() {
        stopHotSearchWordTimer();
        mHotSearchWordTimer = Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mHotSearchWordList != null && !mHotSearchWordList.isEmpty()) {
                        int index;
                        if (mHotSearchWord == null) {
                            index = 0;
                        } else {
                            index = mHotSearchWordList.indexOf(mHotSearchWord) + 1;
                            if (index == mHotSearchWordList.size()) {
                                index = 0;
                            }
                        }
                        mHotSearchWord = mHotSearchWordList.get(index);
                    }
                    if (mHotSearchWord != null) {
                        mView.setHotSearchWord(mHotSearchWord);
                    }
                });
        addDisposable(mHotSearchWordTimer);
    }

    private void stopHotSearchWordTimer() {
        if (mHotSearchWordTimer != null && !mHotSearchWordTimer.isDisposed()) {
            mHotSearchWordTimer.dispose();
        }
    }

    void onChangeHotSearchWord(String pid) {
        mHotSearchWord = null;
        //这里如接口请求失败了，map会为空
        if (mHotSearchWordMap != null && pid != null) {
            mHotSearchWordList = mHotSearchWordMap.get(pid);
        }
        startHotSearchWordTimer();
    }

}
