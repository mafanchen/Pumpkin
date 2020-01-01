package com.video.test.module.usercenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.AdBean;
import com.video.test.javabean.AdInfoBean;
import com.video.test.javabean.BindPhoneBean;
import com.video.test.javabean.UploadAvatarBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseFragment;
import com.video.test.ui.widget.ShareDialogFragment;
import com.video.test.ui.widget.UpdateDialogFragment;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.IntentUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/userCenter/fragment")
public class UserCenterFragment extends BaseFragment<UserCenterPresenter> implements UserCenterContract.View {
    private static final String TAG = "UserCenterFragment";
    @BindView(R.id.civ_avatar_userCenter)
    CircleImageView mCivAvatar;
    @BindView(R.id.tv_nickname_userCenter)
    TextView mTvNickname;
    @BindView(R.id.tv_expireTime_userCenter)
    TextView mTvExpireTime;
    @BindView(R.id.tv_bindPhone_userCenter)
    TextView mTvBindPhone;
    @BindView(R.id.iv_notice_userCenter)
    ImageView mIvNotice;
    @BindView(R.id.iv_ad)
    ImageView mIvAd;
    @BindView(R.id.loadingImage_userCenter)
    AVLoadingIndicatorView mLoadImage;
    private MaterialDialog mProgressDialog;
    private String mCountryCode;
    private String mPhone;
    private ObservableEmitter<View> viewObservableEmitter;


    public static Fragment newInstance(String title) {
        UserCenterFragment userCenterFragment = new UserCenterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        userCenterFragment.setArguments(bundle);
        return userCenterFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mPresenter.initWeChatApi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewObservableEmitter = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindPhoneSuccess(BindPhoneBean bindPhoneBean) {
        LogUtils.d(TAG, "bindPhoneSuccess");
        mPresenter.getUserInfo(); //成功后通知 获取新的手机号码
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            LogUtils.d(TAG, "onHiddenChanged  hidden : " + hidden);

        } else {
            mPresenter.getUserInfo();
            mPresenter.getAdInfo();
        }
    }

    @Override
    protected void loadData() {
        LogUtils.d(TAG, "loadData ");
//        requireStoragePerm();
    }


    @Override
    protected int getContentViewId() {
        return R.layout.bean_activity_user_center;
    }


    @Override
    public void setUserInfo(UserCenterBean userCenterBean) {
        SpUtils.putSerializable(TestApp.getContext(), AppConstant.USER_INFO, userCenterBean);
        if (!TextUtils.isEmpty(userCenterBean.getPic())) {
            GlideApp.with(this).load(userCenterBean.getPic()).into(mCivAvatar);
        } else {
            GlideApp.with(this).load(R.drawable.ic_avatar_default).into(mCivAvatar);
        }

        mTvNickname.setText(userCenterBean.getUsername());
        identifyUserLevel(userCenterBean.getIs_vip(), userCenterBean.getVip_time());

        mCountryCode = userCenterBean.getCountry_code();
        mPhone = userCenterBean.getMobile();
        if (!TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(mCountryCode)) {
            mTvBindPhone.setText(R.string.activity_userCenter_set_phone);
        } else {
            mTvBindPhone.setText(R.string.activity_userCenter_bind_phone);
        }

    }

    @Override
    public void getUploadAvatarMessage(UploadAvatarBean uploadAvatarBean) {
        if (!TextUtils.isEmpty(uploadAvatarBean.getUrl_pic())) {
            GlideApp.with(this).load(uploadAvatarBean.getUrl_pic()).into(mCivAvatar);
            mPresenter.setAvatarUrl(uploadAvatarBean.getUrl_pic());
        } else {
            LogUtils.d(TAG, "uploadAvatarBean url is Empty");
        }
    }

    @Override
    public void getSetAvatarUrl(String message) {
        ToastUtils.showToast(TestApp.getContext(), message);
    }


    @Override
    public void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .content(R.string.dialog_progress_waiting)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideProgressDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setVersionInfo(VersionInfoBean.InfoBean infoBean) {
        int appVersion = TestApp.getAppVersion();
        if (appVersion < Integer.valueOf(infoBean.getVersions()) && TestApp.isOpen()) {
            LogUtils.d(TAG, "setVersionInfo 有新版本");
            getLatestVersion(mContext, infoBean);
        } else {
            LogUtils.d(TAG, "setVersionInfo 没有新版本");
            ToastUtils.showLongToast(TestApp.getContext(), "已是最新版本");
        }

    }

    @Override
    public void getLatestVersion(Context context, VersionInfoBean.InfoBean infoBean) {
        LogUtils.i("UpdateDialog", "getLatestVersion");
        String versionTitle = context.getString(R.string.dialog_check_newVersion, infoBean.getVersions_name());
        String versionSize = context.getString(R.string.dialog_version_size, infoBean.getSize());
        String downloadUrl = infoBean.getDownload();
        String versionContent = infoBean.getUpdate_details();
        boolean isForce = infoBean.isIs_update();

        Bundle bundle = new Bundle();
        bundle.putString("versionTitle", versionTitle);
        bundle.putString("versionSize", versionSize);
        bundle.putString("versionInfo", versionContent);
        bundle.putString("downloadUrl", downloadUrl);
        bundle.putBoolean("versionIsForce", isForce);
        UpdateDialogFragment.newInstance(bundle).show(getChildFragmentManager(), "updateDialog");
    }

    @Override
    public void setAdInfo(AdBean adBean) {
        if (adBean.isAd()) {
            mIvAd.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .load(adBean.getAdInfo().getAdPic())
                    .centerCrop()
                    .into(mIvAd);
            mIvAd.setTag(mIvAd.getId(), adBean.getAdInfo());
        } else {
            mIvAd.setVisibility(View.GONE);
        }
    }


    private void jump2Activity(String activityPath) {
        ARouter.getInstance().build(activityPath).navigation();
    }


    /**
     * 打开相册
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, AppConstant.INTENT_CODE_IMAGE_GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.INTENT_CODE_IMAGE_GALLERY) {
            try {
                mPresenter.getImageFromGallery(data);
            } catch (SecurityException e) {
                ToastUtils.showToast(TestApp.getContext(), R.string.permission_sdcard_error);
            }
        }
    }

    private void identifyUserLevel(String userLevel, String expireTime) {
        // 先把用户的VI信息存进去
        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, userLevel);
        int icCrownId = 0;
        switch (userLevel) {
            case AppConstant.USER_NORMAL:
            case AppConstant.USER_VIP_EXPIRE:
                mTvExpireTime.setText(R.string.activity_userCenter_normalUser);
                break;
            case AppConstant.USER_VIP:
            case AppConstant.USER_VIP_LASTDAY:
                mTvExpireTime.setText(getResources().getString(R.string.activity_userCenter_vip, expireTime));
                icCrownId = R.drawable.ic_vip_user;
                break;
            default:
                break;
        }
        mTvNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, icCrownId, 0);
    }

    @OnClick({R.id.tv_setting_userCenter, R.id.civ_avatar_userCenter, R.id.iv_ad,
            R.id.tv_bindPhone_userCenter, R.id.tv_collection_userCenter, R.id.tv_history_userCenter, R.id.tv_download_userCenter,
            R.id.tv_feedback_userCenter, R.id.tv_about_userCenter, R.id.tv_nickname_userCenter, R.id.iv_notice_userCenter,
            R.id.tv_update_userCenter})
    void onViewClicked(View view) {
        if (viewObservableEmitter == null) {
            Disposable disposable = Observable.create((ObservableOnSubscribe<View>)
                    emitter -> viewObservableEmitter = emitter)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(view1 -> {
                        switch (view1.getId()) {
                            case R.id.tv_setting_userCenter:
                                jump2Activity("/setting/activity");
                                break;
                            case R.id.civ_avatar_userCenter:
                                // TODO: 2019/7/29 暂时屏蔽头像上传
//                                openGallery();
                                break;
                            case R.id.tv_bindPhone_userCenter:
                                ARouter.getInstance().build("/setPhone/activity").withString("code", mCountryCode).withString("phone", mPhone).navigation();
                                break;
                            case R.id.tv_collection_userCenter:
                                jump2Activity("/collection/activity");
                                break;
                            case R.id.tv_history_userCenter:
                                jump2Activity("/history/activity");
                                break;
                            case R.id.tv_about_userCenter:
                                jump2Activity("/about/activity");
                                break;
                            case R.id.iv_notice_userCenter:
                                jump2Activity("/notice/activity");
                                Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.ic_mine_notice).into(mIvNotice);
                                MobclickAgent.onEvent(TestApp.getContext(), "usercenter_click_notice", "点击用户中心通知按钮");
                                break;
                            case R.id.tv_download_userCenter:
                                jump2Activity("/download/activity");
                                break;
                            case R.id.tv_feedback_userCenter:
                                jump2Activity("/feedback/activity");
                                break;
                            case R.id.tv_update_userCenter:
                                mPresenter.getVersionInfo();
                                break;
                            case R.id.iv_ad:
                                onAdClick();
                                break;
                            default:
                                break;
                        }
                    });
            mPresenter.addDisposable(disposable);
        }
        viewObservableEmitter.onNext(view);
    }

    private void onAdClick() {
        Object tag = mIvAd.getTag(mIvAd.getId());
        if (tag instanceof AdInfoBean) {
            AdInfoBean adInfoBean = (AdInfoBean) tag;
            switch (adInfoBean.getType()) {
                case AdInfoBean.Type.WEB:
                    LogUtils.d(TAG, "AD_TYPE_WEB");
                    if (adInfoBean.getAdUrl() != null) {
                        startActivity(IntentUtils.getBrowserIntent(adInfoBean.getAdUrl()));
                    }
                    break;
                case AdInfoBean.Type.DOWNLOAD:
                    LogUtils.d(TAG, "AD_TYPE_DOWNLOAD");
                    String downloadUrl = adInfoBean.getAndroidUrl();
                    Context context = getContext();
                    if (downloadUrl != null) {
                        DownloadUtil.startDownloadOrOpenDownloadedFile(context, downloadUrl);
                    }
                    break;
                default:
                    break;
            }
            // TODO: 2020/1/1 这里是否需要统计
//            mPresenter.addAdInfo(AppConstant.AD_TYPE_PLAYER, adInfoBean.getId());
        }
    }


    private void showShareDialog() {
        ShareDialogFragment shareDialogFragment = ShareDialogFragment.newInstance();
        String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
        shareDialogFragment.setShareItemClickListener(new ShareDialogFragment.ShareItemClickListener() {
            @Override
            public void onShareItemClick(int position) {
                switch (position) {
                    case AppConstant.WX_SCENE_SESSION:
                        LogUtils.d(TAG, "WX_SCENE_SESSION");
                        MobclickAgent.onEvent(TestApp.getContext(), "detail_share_via_wechat", "详情页微信分享");
                        mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_SESSION);
                        break;
                    case AppConstant.WX_SCENE_TIMELINE:
                        LogUtils.d(TAG, "WX_SCENE_TIMELINE");
                        MobclickAgent.onEvent(TestApp.getContext(), "detail_share_via_circle", "详情页朋友圈分享");
                        mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_TIMELINE);
                        break;
                    default:
                        break;
                }
            }
        });
        shareDialogFragment.show(getChildFragmentManager(), "dialog");
    }

}
