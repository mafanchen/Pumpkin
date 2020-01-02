package com.video.test.module.collection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.CollectionListBean;
import com.video.test.javabean.CollectionTopicListBean;
import com.video.test.javabean.base.ISelectableBean;
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
public class CollectionAdapter extends BaseSelectableAdapter<ISelectableBean, RecyclerView.ViewHolder> {
    private static final String TAG = "CollectionAdapter";
    private static final int ITEM_TYPE_VIDEO = 1;
    private static final int ITEM_TYPE_TOPIC = 2;
    private boolean mIsShow;

    private List<ISelectableBean> mCollectionList;

    @Override
    public int getItemViewType(int position) {
        if (mCollectionList.get(position) instanceof CollectionListBean) {
            return ITEM_TYPE_VIDEO;
        } else {
            return ITEM_TYPE_TOPIC;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE_VIDEO) {
            View view = inflater.inflate(R.layout.bean_recycle_item_collection_video, parent, false);
            return new VideoViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.bean_recycle_item_collection_topic, parent, false);
            return new TopicViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ISelectableBean item = mCollectionList.get(position);
        if (holder instanceof VideoViewHolder && item instanceof CollectionListBean) {
            ((VideoViewHolder) holder).bindData((CollectionListBean) item, mIsShow);
        } else if (holder instanceof TopicViewHolder) {
            ((TopicViewHolder) holder).bindData((CollectionTopicListBean) item, mIsShow);
        }

    }


    @Override
    public int getItemCount() {
        return null == mCollectionList ? 0 : mCollectionList.size();
    }

    public void setData(List<ISelectableBean> historyListBeans) {
        if (null == mCollectionList) {
            mCollectionList = new ArrayList<>();
            mCollectionList.addAll(historyListBeans);
        } else {
            mCollectionList.clear();
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
        for (ISelectableBean bean : mCollectionList) {
            if (bean.isSelected()) {
                if (bean instanceof CollectionListBean) {
                    selectItem.add(((CollectionListBean) bean).getId());
                } else if (bean instanceof CollectionTopicListBean) {
                    selectItem.add(((CollectionTopicListBean) bean).getTopicId());
                }
            }
        }
        Gson gson = new Gson();
        return gson.toJson(selectItem);
    }

    @Override
    protected List<ISelectableBean> getList() {
        return mCollectionList;
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

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

        VideoViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }


        public void bindData(CollectionListBean item, boolean mIsShow) {
            if (mIsShow) {
                mGroupCollection.setVisibility(View.VISIBLE);
            } else {
                mGroupCollection.setVisibility(View.GONE);
            }
            GlideApp.with(context).load(item.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(mIvVideoPic);
            mTvName.setText(item.getVod_name());
            mCheckBox.setChecked(item.isSelected());
            mCl.setOnClickListener(v -> {
                LogUtils.d(TAG, "mll == " + item.toString());
                ARouter.getInstance().build("/player/activity").withString("vodId", item.getVod_id()).navigation();
            });
            mCheckBox.setOnClickListener(v -> {
                item.setSelected(mCheckBox.isChecked());
                LogUtils.d(TAG, "mCheckBox == " + item.toString());
            });
            mViewPicBackground.setOnClickListener(v -> {
                mCheckBox.setChecked(!mCheckBox.isChecked());
                item.setSelected(mCheckBox.isChecked());
                LogUtils.d(TAG, "PicBackground == " + item.toString());
            });
        }
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_topic_cover)
        ImageView mIvCover;
        @BindView(R.id.tv_topic_name)
        TextView mTvName;
        @BindView(R.id.tv_topic_video_count)
        TextView mTvCount;
        @BindView(R.id.checkbox_select)
        CheckBox mCheckBox;

        TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(CollectionTopicListBean item, boolean mIsShow) {
            if (mIsShow) {
                mCheckBox.setVisibility(View.VISIBLE);
            } else {
                mCheckBox.setVisibility(View.GONE);
            }
            Context context = itemView.getContext();
            GlideApp.with(context).load(item.getTopicPic()).transition(withCrossFade()).error(R.drawable.bg_video_default_horizontal).into(mIvCover);
            mTvName.setText(item.getTopicTitle());
            mCheckBox.setChecked(item.isSelected());
            mTvCount.setText(context.getString(R.string.activity_collect_topic_video_count, item.getTopicNum()));
            itemView.setOnClickListener(v ->
                    ARouter.getInstance().build("/topicVideoList/activity")
                            .withInt("pid", 2)
                            .withString("tag", item.getTopicTag())
                            .withString("type", item.getTopicTitle())
                            .withString("videoNum", item.getTopicNum())
                            .navigation()
            );
            mCheckBox.setOnClickListener(v -> {
                item.setSelected(mCheckBox.isChecked());
                LogUtils.d(TAG, "mCheckBox == " + item.toString());
            });
        }
    }
}
