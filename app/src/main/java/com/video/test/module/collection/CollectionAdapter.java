package com.video.test.module.collection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.video.test.R;
import com.video.test.framework.BaseViewHolder;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.CollectionListBean;
import com.video.test.ui.adapter.BaseSelectableAdapter;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/8/7.
 */
public class CollectionAdapter extends BaseSelectableAdapter<CollectionListBean, CollectionAdapter.CollectionViewHolder> {
    private static final String TAG = "CollectionAdapter";
    private boolean mIsShow;
    private List<CollectionListBean> mCollectionList;


    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bean_recycle_item_collection_video, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        holder.bindData(position, holder.getItemViewType(), mCollectionList);
        if (mIsShow) {
            holder.mGroupCollection.setVisibility(View.VISIBLE);
            LogUtils.d(TAG, "onBindViewHolder True mGroup ==" + holder.mCheckBox.getVisibility());
        } else {
            holder.mGroupCollection.setVisibility(View.GONE);
            LogUtils.d(TAG, "onBindViewHolder True mGroup ==" + holder.mCheckBox.getVisibility());
        }
    }


    @Override
    public int getItemCount() {
        return null == mCollectionList ? 0 : mCollectionList.size();
    }

    public void setData(List<CollectionListBean> historyListBeans) {
        if (null == mCollectionList) {
            mCollectionList = new ArrayList<>();
            mCollectionList.addAll(historyListBeans);
        } else {
            mCollectionList.clear();
            mCollectionList.addAll(historyListBeans);
        }
        notifyDataSetChanged();
    }

    public void addData(List<CollectionListBean> historyListBeans) {
        if (null != mCollectionList) {
            mCollectionList.addAll(historyListBeans);
        }
        notifyDataSetChanged();
    }

    void changeGroupVisibility(boolean isShow) {
        this.mIsShow = isShow;
        notifyDataSetChanged();
    }


    public String getSelectItem() {
        List<String> selectItem = new ArrayList<>();
        for (CollectionListBean collectionListBean : mCollectionList) {
            if (collectionListBean.isSelected()) {
                selectItem.add(collectionListBean.getId());
            }
        }
        Gson gson = new Gson();
        return gson.toJson(selectItem);
    }

    @Override
    protected List<CollectionListBean> getList() {
        return mCollectionList;
    }

    static class CollectionViewHolder extends BaseViewHolder<List<CollectionListBean>> {

        private final Context context;
        @BindView(R.id.iv_recycle_item_videoPic_collection)
        ImageView mIvVideoPic;
        @BindView(R.id.cb_recycle_item_collection)
        CheckBox mCheckBox;
        @BindView(R.id.tv_recycle_item_videoName_collection)
        TextView mTvName;
        @BindView(R.id.view_background_videoPic_collection)
        View mViewPicBackground;
        @BindView(R.id.ll_recycle_item_video_module)
        ConstraintLayout mCl;
        @BindView(R.id.group_recycle_item_collection)
        Group mGroupCollection;

        CollectionViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(int position, int viewType, List<CollectionListBean> data) {
            final CollectionListBean collectionListBean = data.get(position);
            GlideApp.with(context).load(collectionListBean.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(mIvVideoPic);
            mTvName.setText(collectionListBean.getVod_name());
            mCheckBox.setChecked(collectionListBean.isSelected());

            mCl.setOnClickListener(v -> {
                LogUtils.d(TAG, "mll == " + collectionListBean.toString());
                ARouter.getInstance().build("/player/activity").withString("vodId", collectionListBean.getVod_id()).navigation();
            });

            mCheckBox.setOnClickListener(v -> {
                collectionListBean.setSelected(mCheckBox.isChecked());
                LogUtils.d(TAG, "mCheckBox == " + collectionListBean.toString());
            });

            mViewPicBackground.setOnClickListener(v -> {
                mCheckBox.setChecked(!mCheckBox.isChecked());
                collectionListBean.setSelected(mCheckBox.isChecked());
                LogUtils.d(TAG, "PicBackground == " + collectionListBean.toString());
            });
        }
    }
}
