package com.video.test.module.download;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.utils.LogUtils;
import com.video.test.utils.NetworkUtils;
import com.video.test.utils.PixelUtils;
import com.video.test.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/download/activity")
public class DownloadActivity extends BaseActivity<DownloadPresenter> implements DownloadContract.View {
    private static final String TAG = "DownloadActivity";

    @BindView(R.id.rv_download)
    SwipeMenuRecyclerView mRvDownload;
    @BindView(R.id.iv_background_noCache_download)
    TextView mIvNoCache;
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.tv_editBtn_toolbar)
    TextView mTvEditBtn;
    @BindView(R.id.group_delete)
    Group mGroup;
    @BindView(R.id.tv_selectAll)
    TextView mTvSelectAll;
    @BindView(R.id.tv_delete)
    TextView mTvDelete;
    private String mUserLevel;
    private DownloadAdapter mAdapter;
    private List<Object> mM3U8List;
    private boolean mIsManager = false;
    private HashSet<String> mSelectedSet = new HashSet<>();

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.getSDCardFreeSize();
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_download;
    }

    @Override
    protected void initData() {
        NetworkUtils.checkNetConnectChange();
        mUserLevel = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, AppConstant.USER_NORMAL);
        requireStoragePerm();
        mAdapter = new DownloadAdapter();
        mPresenter.initM3U8Listener();
        /* 进入之前 先将所有的任务都变为暂停状态 防止因意外任务中断  有2个以上的 任务状态为Start 的任务  */
        mPresenter.pauseAllTasks();
        mPresenter.getM3U8Tasks(mUserLevel);
    }


    @Override
    protected void initToolBar() {
        if (null != mTvTitle && null != mIbBack) {
            mIbBack.setVisibility(View.VISIBLE);
            mTvTitle.setText(R.string.activity_download);
        }
        if (mTvEditBtn != null) {
            setManager(false);
        }
    }

    @Override
    protected void setAdapter() {

        mRvDownload.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
            if (mAdapter.getItemViewType(position) == DownloadAdapter.TYPE_TASK && !mIsManager) {
                int width = PixelUtils.dp2px(DownloadActivity.this, 75);
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                SwipeMenuItem swipeMenuItem = new SwipeMenuItem(DownloadActivity.this)
                        .setBackground(R.color.download_btn_delete)
                        .setText(R.string.activity_download_delete)
                        .setTextColor(ContextCompat.getColor(DownloadActivity.this, R.color.colorWhite))
                        .setTextSize(15)
                        .setHeight(height)
                        .setWidth(width);
                rightMenu.addMenuItem(swipeMenuItem);
            }
        });

        mRvDownload.setSwipeMenuItemClickListener((menuBridge, position) -> {
            if (mM3U8List.get(position) instanceof M3U8DownloadBean) {
                LogUtils.d(TAG, "Menu position : " + position + " VideoName : " + ((M3U8DownloadBean) mM3U8List.get(position)).getVideoName());
                menuBridge.closeMenu();
                mPresenter.deleteM3U8Task(mM3U8List, ((M3U8DownloadBean) mM3U8List.get(position)).getVideoUrl());
                setResult(RESULT_OK);
            }
        });

        mRvDownload.setAdapter(mAdapter);
        setListener();
    }

    /**
     * 设置监听器
     */
    private void setListener() {

        mAdapter.setClickListener(new DownloadItemClickListener() {

            @Override
            public void onItemSelected(boolean isSelected, M3U8DownloadBean bean) {
                if (mSelectedSet == null) {
                    mSelectedSet = new HashSet<>();
                }
                if (isSelected) {
                    mSelectedSet.add(bean.getVideoUrl());
                } else {
                    mSelectedSet.remove(bean.getVideoUrl());
                }
                setSelectCountText(mSelectedSet.size());
                setSelectAllText(mSelectedSet.size());
            }

            @Override
            public void startTask(M3U8DownloadBean downloadingBean, int position) {
                LogUtils.d(TAG, "startTask url : " + downloadingBean.getVideoUrl() + " position : " + position);
                mPresenter.startM3U8Task(downloadingBean);
            }

            @Override
            public void pauseTask(M3U8DownloadBean downloadingBean, int position) {
                LogUtils.d(TAG, "pauseTask url : " + downloadingBean.getVideoUrl() + " position : " + position);
                mPresenter.pauseM3U8Task(downloadingBean);
            }

            @Override
            public void downloadingTask(M3U8DownloadBean downloadingBean, int position) {
                LogUtils.d(TAG, "downloadingTask url : " + downloadingBean.getVideoUrl() + " position : " + position);
            }

            @Override
            public void playNetworkVideo(String videoId, String videoUrl) {

                new MaterialDialog.Builder(DownloadActivity.this)
                        .content(R.string.dialog_playVideo)
                        .positiveText(R.string.dialog_confirm)
                        .negativeText(R.string.dialog_cancel)
                        .onPositive((dialog, which) ->
                                ARouter.getInstance()
                                        .build("/player/activity")
                                        .withString("vodId", videoId)
                                        .withString("videoUrl", videoUrl)
                                        .withString("videoDegree", "0")
                                        .navigation()
                        ).show();
            }

            @Override
            public void playLocalVideo(String localUrl, String localName) {
                new MaterialDialog.Builder(DownloadActivity.this)
                        .content(R.string.dialog_playVideo)
                        .positiveText(R.string.dialog_confirm)
                        .negativeText(R.string.dialog_cancel)
                        .onPositive((dialog, which) ->
                                ARouter.getInstance().build("/localPlayer/activity")
                                        .withString("localUrl", localUrl)
                                        .withString("localName", localName)
                                        .navigation()).show();
            }

        });
    }


    @Override
    public void showNoCacheBackground() {
        if (null != mIvNoCache && null != mRvDownload) {
            mIvNoCache.setVisibility(View.VISIBLE);
            mRvDownload.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideNoCacheBackground() {
        if (null != mIvNoCache && null != mRvDownload) {
            mIvNoCache.setVisibility(View.GONE);
            mRvDownload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showCleanCacheDialog() {
        new MaterialDialog.Builder(DownloadActivity.this)
                .content(R.string.dialog_memoryCache_not_enough)
                .title(R.string.dialog_memoryCache_not_enough)
                .positiveText(R.string.dialog_confirm_clearCache)
                .negativeText(R.string.dialog_cancel_clearCache)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ARouter.getInstance().build("/download/activity").navigation();
                    }
                }).show();
    }

    @Override
    public void setDownloadBeans(List<Object> downloadBeans) {
        if (null != mAdapter && mRvDownload != null) {
            mM3U8List = downloadBeans;
            mAdapter.setData(downloadBeans);
        }
    }

    private void setManager(boolean isManager) {
        mIsManager = isManager;
        if (!isManager) {
            mTvEditBtn.setText(R.string.activity_download_edit);
            mTvEditBtn.setTextColor(Color.parseColor("#ffad43"));
            mGroup.setVisibility(View.GONE);
            if (mAdapter != null) {
                mAdapter.setIsManager(mIsManager);
            }
        } else {
            mTvEditBtn.setText(R.string.activity_download_edit_cancel);
            mTvEditBtn.setTextColor(Color.parseColor("#cccccc"));
//            mPresenter.getM3U8Tasks(mUserLevel);
            mGroup.setVisibility(View.VISIBLE);
            mIsManager = true;
            if (mAdapter != null) {
                mAdapter.setIsManager(mIsManager);
            }
        }
        deselectAll();
    }

    @OnClick({R.id.ib_back_toolbar, R.id.tv_editBtn_toolbar, R.id.tv_selectAll, R.id.tv_delete})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back_toolbar:
                finish();
                break;
            case R.id.tv_editBtn_toolbar:
                if (null != mTvEditBtn && null != mGroup) {
                    if (View.VISIBLE == mGroup.getVisibility()) {
                        setManager(false);
                    } else {
                        setManager(true);
                    }
                }
                break;
            case R.id.tv_selectAll:
                if (mAdapter == null || mM3U8List == null || mM3U8List.isEmpty()) {
                    break;
                }
                if (mSelectedSet == null || mSelectedSet.isEmpty()) {
                    selectAll();
                } else {
                    int allTaskCount = 0;
                    for (Object o : mM3U8List) {
                        if (o instanceof M3U8DownloadBean) {
                            allTaskCount++;
                        }
                    }
                    int selectCount = mSelectedSet.size();
                    if (allTaskCount > selectCount) {
                        selectAll();
                    } else {
                        deselectAll();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                if (mSelectedSet != null) {
                    if (mSelectedSet.size() >= 2) {
                        //大于两个弹窗
                        showDeleteConfirmDialog();
                    } else if (mSelectedSet.size() > 0) {
                        deleteSelected();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void deleteSelected() {
        //批量删除
        mPresenter.deleteM3U8Task(mM3U8List, mSelectedSet.toArray(new String[]{}));
        setManager(false);
        setResult(RESULT_OK);
    }

    private void showDeleteConfirmDialog() {
        new MaterialDialog.Builder(this)
                .titleColor(Color.parseColor("#333333"))
                .title("确定删除已经缓存视频？")
                .contentColor(Color.parseColor("#888888"))
                .content("删除后无法恢复")
                .negativeColor(Color.parseColor("#888888"))
                .onNegative((dialog, which) -> dialog.dismiss())
                .negativeText("取消")
                .positiveColor(Color.parseColor("#ffad43"))
                .onPositive(((dialog, which) -> {
                    deleteSelected();
                    dialog.dismiss();
                }))
                .positiveText("确定")
                .build()
                .show();
    }

    private void selectAll() {
        if (mAdapter != null) {
            mAdapter.selectAll();
            mAdapter.notifyDataSetChanged();
        }
        if (mM3U8List != null) {
            for (Object o : mM3U8List) {
                if (o instanceof M3U8DownloadBean) {
                    if (mSelectedSet != null) {
                        mSelectedSet.add(((M3U8DownloadBean) o).getVideoUrl());
                    }
                }
            }
        }
        if (mSelectedSet != null) {
            setSelectCountText(mSelectedSet.size());
            setSelectAllText(mSelectedSet.size());
        }
    }

    private void deselectAll() {
        if (mAdapter != null) {
            mAdapter.deSelectAll();
            mAdapter.notifyDataSetChanged();
        }
        if (mSelectedSet != null) {
            mSelectedSet.clear();
        }
        setSelectCountText(0);
        setSelectAllText(0);
    }

    private void setSelectAllText(int count) {
        if (mTvSelectAll == null) {
            return;
        }
        if (count == 0 || mM3U8List == null || mM3U8List.isEmpty()) {
            mTvSelectAll.setText(R.string.activity_download_selectAll);
        } else {
            int allTaskCount = 0;
            for (Object o : mM3U8List) {
                if (o instanceof M3U8DownloadBean) {
                    allTaskCount++;
                }
            }
            if (allTaskCount > count) {
                mTvSelectAll.setText(R.string.activity_download_selectAll);
            } else {
                mTvSelectAll.setText(R.string.activity_download_deselectAll);
            }
        }
    }

    private void setSelectCountText(int count) {
        if (mTvDelete != null) {
            mTvDelete.setEnabled(count > 0);
            if (count <= 0) {
                mTvDelete.setText(R.string.activity_download_delete);
            } else {
                String text = getString(R.string.activity_download_delete) + "(" + count + ")";
                mTvDelete.setText(text);
            }
        }
    }

    @Override
    public void showEditBtn() {
        if (mTvEditBtn != null) {
            mTvEditBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideEditBtn() {
        if (mTvEditBtn != null) {
            mTvEditBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void showToast(String text) {
        runOnUiThread(() -> ToastUtils.showToast(this, text));
    }
}

