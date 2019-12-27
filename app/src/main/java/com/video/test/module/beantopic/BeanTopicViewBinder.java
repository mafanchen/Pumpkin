package com.video.test.module.beantopic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.BeanTopicBean;
import com.video.test.javabean.BeanTopicContentBean;
import com.video.test.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/9/20.
 */
public class BeanTopicViewBinder extends ItemViewBinder<BeanTopicBean, BeanTopicViewBinder.ViewHolder> {
    private static final String TAG = "BeanTopicViewBinder";


    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_item_bean_topic, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final BeanTopicBean item) {
        final BeanTopicContentBean beanTopicContentBean = item.getList().get(0);
        holder.mTvTopicName.setText(beanTopicContentBean.getZt_title());
        holder.mTvTopicContent.setText(beanTopicContentBean.getZt_content());
        holder.mTvTopicNum.setText(holder.itemView.getResources().getString(R.string.topic_video_num, beanTopicContentBean.getZt_num()));
        GlideApp.with(holder.context).load(beanTopicContentBean.getZt_pic()).transition(withCrossFade()).into(holder.mIvTopicPic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(TestApp.getContext(), "special_click_special", item.getType());

                LogUtils.d(TAG, "itemView Click id == " + beanTopicContentBean.getId() + " name == " + beanTopicContentBean.getZt_title()
                        + " tag == " + item.getTag() + " type == " + item.getType());

                ARouter.getInstance().build("/topicVideoList/activity")
                        .withInt("pid", item.getPid())
                        .withString("tag", item.getTag())
                        .withString("type", item.getType())
                        .withString("videoNum", beanTopicContentBean.getZt_num())
                        .navigation();

            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        @BindView(R.id.tv_topicName_topic)
        TextView mTvTopicName;
        @BindView(R.id.iv_topicPic_topic)
        ImageView mIvTopicPic;
        @BindView(R.id.tv_topicContent_topic)
        TextView mTvTopicContent;
        @BindView(R.id.tv_topicNum_topic)
        TextView mTvTopicNum;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);

        }
    }
}
