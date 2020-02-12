package com.video.test.module.share;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.GiftBean;
import com.video.test.javabean.ShareInfoBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseFragment;
import com.video.test.ui.widget.RegisterDialogFragment;
import com.video.test.utils.GeneratePictureUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.SDCardUtils;
import com.video.test.utils.ToastUtils;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/share/fragment")
public class ShareFragment extends BaseFragment<SharePresenter> implements ShareContract.View {
    private static final String TAG = "ShareActivity";

    @BindView(R.id.iv_qrCode_share_fragment)
    ImageView mIvQrCode;
    @BindView(R.id.iv_weChat_share_fragment)
    TextView mTvWeChat;
    @BindView(R.id.iv_friend_share_fragment)
    TextView mTvFriendShare;
    @BindView(R.id.iv_url_share_fragment)
    TextView mTvUrlShare;
    @BindView(R.id.tv_swapBtn_share_fragment)
    TextView mTvSwapBtn;
    @BindView(R.id.tv_rule_share_fragment)
    TextView mTvRuleBtn;
    @BindView(R.id.tv_save_code_share_fragment)
    TextView mTvSaveCode;
    @BindView(R.id.tv_website_share_fragment)
    TextView mTvWebsite;
    @BindView(R.id.tv_invite_count_share_fragment)
    TextView mTvInviteCount;
    private Bitmap mShareQRCode;
    private RuleDialogFragment mRuleDialog;

    /**
     * 用于控制是否在{@link #loadData()} 中进行获取分享信息
     * 这里{@link #onHiddenChanged(boolean)} 中才会调用数据
     * 会导致在{@link com.video.test.module.sharepage.SharePageActivity} 中add此页面时
     * 获取不到分享信息
     */
    private boolean singlePage;
    private ShareInfoBean mShareInfo;

    public static Fragment newInstance(String title) {
        ShareFragment videoFragment = new ShareFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    /**
     * 在单个页面复用此页面时调用{@link com.video.test.module.sharepage.SharePageActivity}
     *
     * @param title
     * @param singlePage {@link #singlePage}
     * @return
     */
    public static Fragment newInstance(String title, boolean singlePage) {
        Fragment fragment = newInstance(title);
        Bundle arguments = fragment.getArguments();
        assert arguments != null;
        arguments.putBoolean("singlePage", singlePage);
        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.getShareInfo();
        }
    }


    @Override
    protected void loadData() {
//        requireStoragePerm();
        Bundle arguments = getArguments();
        if (arguments != null) {
            singlePage = arguments.getBoolean("singlePage", false);
        }
        if (singlePage) {
            mPresenter.getShareInfo();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.initWeChatApi();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.bean_fragment_share;
    }

    @Override
    public void createQRCode(String webUrl) {
        if (null != mIvQrCode) {
            String url = TestApp.isOpen() ? webUrl : mShareInfo.getWebUrl();
            mShareQRCode = CodeUtils.createImage(url, 360, 360, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo));
            GlideApp.with(this).load(mShareQRCode).transition(withCrossFade()).into(mIvQrCode);
        }
    }

    @Override
    public void showGiftDialog(GiftBean giftBean) {

        if (!TextUtils.isEmpty(giftBean.getActivity_pic())) {
            Bundle bundle = new Bundle();
            bundle.putString("closeUrl", giftBean.getClose_pic());
            bundle.putString("picUrl", giftBean.getActivity_pic());
            bundle.putString("expireTime", giftBean.getVip_time());

            RegisterDialogFragment registerDialogFragment = RegisterDialogFragment.newInstance(bundle);
            registerDialogFragment.show(getChildFragmentManager(), "giftDialog");
            registerDialogFragment.setDialogItemClickListener(registerDialogFragment::dismiss);
        }
        mPresenter.getShareInfo();
    }

    @Override
    public void setShareCount(String count) {
        SpannableString string = new SpannableString(getString(R.string.activity_share_invite_count, count));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.share_invite_count));
        string.setSpan(colorSpan, 4, string.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvInviteCount.setText(string);
    }

    @OnClick({R.id.iv_weChat_share_fragment, R.id.iv_friend_share_fragment,
            R.id.tv_swapBtn_share_fragment, R.id.tv_save_code_share_fragment,
            R.id.iv_url_share_fragment, R.id.tv_rule_share_fragment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_weChat_share_fragment:
                LogUtils.d(TAG, "微信分享");
                if (mShareInfo != null) {
                    mPresenter.share2WeChat(getResources(), mShareInfo.getShareUrl(), AppConstant.WX_SCENE_SESSION);
                }
                break;
            case R.id.iv_friend_share_fragment:
                LogUtils.d(TAG, "微信朋友圈");
                if (mShareInfo != null) {
                    mPresenter.share2WeChat(getResources(), mShareInfo.getShareUrl(), AppConstant.WX_SCENE_TIMELINE);
                }
                break;
            case R.id.tv_swapBtn_share_fragment:
                ARouter.getInstance().build("/swap/activity").navigation();
                LogUtils.d(TAG, "tv_swapBtn");

                break;
            case R.id.tv_save_code_share_fragment:
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(mContext, perms)) {
                    LogUtils.d(TAG, "tv_save_code_share");
                    MobclickAgent.onEvent(TestApp.getContext(), "share_click_save_QR", "分享页面保存二维码");
                    if (mShareInfo != null) {
                        createSharePic();
                    }
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perms);
                }
                break;
            case R.id.iv_url_share_fragment:
                LogUtils.d(TAG, "复制分享链接");
                copyUrlToClipBoard();
                break;
            case R.id.tv_rule_share_fragment:
                LogUtils.d(TAG, "tv_rule_share_fragment");
                showRuleDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 复制分享链接
     */
    private void copyUrlToClipBoard() {
        if (mShareInfo != null && mShareInfo.getShareUrl() != null) {
            ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            String shareText = getString(R.string.activity_share_text_share_url, mShareInfo.getShareUrl());
            manager.setPrimaryClip(ClipData.newPlainText(null, shareText));
            ToastUtils.showToast("已将分享链接复制到剪切板");
        }
    }

    /**
     * 弹出活动规则页面
     */
    private void showRuleDialog() {
        if (mRuleDialog == null) {
            mRuleDialog = new RuleDialogFragment();
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            mRuleDialog.show(fragmentManager, RuleDialogFragment.TAG);
        }
    }


    @Override
    public void setShareInfo(ShareInfoBean shareInfo) {
        mShareInfo = shareInfo;
        if (shareInfo != null) {
            if (!TextUtils.isEmpty(shareInfo.getShareUrl())) {
                createQRCode(shareInfo.getShareUrl());
            }
            setShareCount(shareInfo.getTotalNum());
            mTvWebsite.setText(getString(R.string.activity_share_website, shareInfo.getWebUrl()));
        }
    }

    public void createSharePic() {
        GenerateModel generateModel = new GenerateModel((ViewGroup) Objects.requireNonNull(getActivity()).getWindow().getDecorView(), mShareQRCode, mShareInfo.getWebUrl(), mShareInfo.getSharePic());
        String cachePath = SpUtils.getString(TestApp.getContext(), "cachePath", SDCardUtils.getSDRootPath());
        String fileName = getString(R.string.activity_share_name_image_qr_code, "-" + System.currentTimeMillis());
        String qrCodePath = cachePath + File.separator + "DCIM" + File.separator + fileName;
        generateModel.setSavePath(qrCodePath);

        GeneratePictureUtils.getInstance().generate(generateModel, ((throwable, bitmap) -> {
            if (null != throwable || null == bitmap) {
                ToastUtils.showLongToast(R.string.activity_share_generate_pic_error);
            } else {
                String successContent = getResources().getString(R.string.activity_share_generate_pic_success);
                ToastUtils.showLongToast(successContent);
                notifyUpdateImages(qrCodePath);
            }
        }));
    }

    private void notifyUpdateImages(String path) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);
    }
}
