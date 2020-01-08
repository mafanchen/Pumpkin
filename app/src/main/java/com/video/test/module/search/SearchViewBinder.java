package com.video.test.module.search;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.SearchResultVideoBean;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/9/20.
 */
public class SearchViewBinder extends ItemViewBinder<SearchResultVideoBean, SearchViewBinder.ViewHolder> {

    private OnCollectListener mOnCollectListener;

    private String searchWord;

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_item_search_result, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull SearchResultVideoBean item) {
        holder.bindData(searchWord, item, mOnCollectListener);
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public void setOnCollectListener(OnCollectListener onCollectListener) {
        this.mOnCollectListener = onCollectListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "SearchViewHolder";
        private final Context context;

        @BindView(R.id.iv_pic_recycle_search)
        ImageView mIvPic;
        @BindView(R.id.tv_picGrade_recycle_search)
        TextView mTvPicGrade;
        @BindView(R.id.tv_videoName_recycle_search)
        TextView mTvVideoName;
        @BindView(R.id.tv_grade_recycle_search)
        TextView mTvGrade;
        @BindView(R.id.tv_region_recycle_search)
        TextView mTvRegion;
        @BindView(R.id.tv_actor_recycle_search)
        TextView mTvActor;
        @BindView(R.id.tv_director_recycle_search)
        TextView mTvDirector;
        @BindView(R.id.tv_update_recycle_search)
        TextView mTvUpdate;
        @BindView(R.id.tv_year_recycle_search)
        TextView mTvYear;
        @BindView(R.id.cl_search)
        ConstraintLayout mCl;
        @BindView(R.id.checkbox_collect)
        CheckBox mCheckBoxCollect;


        ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);

        }

        public void bindData(String searchWord, final SearchResultVideoBean videoBean, OnCollectListener onCollectListener) {
            final int adapterPosition = getAdapterPosition();

            GlideApp.with(context).load(videoBean.getVod_pic()).transition(withCrossFade()).error(R.drawable.bg_video_default_vertical).into(mIvPic);
            String vodName = videoBean.getVod_name();
            if (TextUtils.isEmpty(searchWord) || vodName == null) {
                mTvVideoName.setText(vodName);
            } else {
                int start = vodName.indexOf(searchWord);
                if (start == -1) {
                    mTvVideoName.setText(vodName);
                } else {
                    SpannableStringBuilder builder = new SpannableStringBuilder(vodName);
                    ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ffad43"));
                    builder.setSpan(span, start, start + searchWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mTvVideoName.setText(builder);
                }
            }
            mTvActor.setText(videoBean.getVod_actor());
            mTvDirector.setText(videoBean.getVod_director());
            mTvPicGrade.setText(videoBean.getVod_scroe());
            mTvRegion.setText(videoBean.getVod_area());
            mTvUpdate.setText(videoBean.getVod_addtime());
            mTvYear.setText(videoBean.getVod_year());

            setScore(mTvPicGrade, String.valueOf(videoBean.getVideoType()), videoBean.getVod_continu(), videoBean.getVod_scroe(), videoBean.isEnd());
            String vodScore = videoBean.getVod_scroe();
            if (TextUtils.isEmpty(vodScore) || Double.parseDouble(vodScore) == 0 || Double.parseDouble(vodScore) == 10) {
                mTvGrade.setTextColor(ContextCompat.getColor(mTvGrade.getContext(), R.color.homepage_font_episode));
                mTvGrade.setText("暂无评分");
            } else {
                mTvGrade.setTextColor(ContextCompat.getColor(mTvGrade.getContext(), R.color.homepage_font_grade));
                mTvGrade.setText(vodScore);
            }

            mCl.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    LogUtils.d(TAG, "mCl Click  position == " + adapterPosition + " id == " + videoBean.getVod_id());
                    ARouter.getInstance().build("/player/activity").withString("vodId", videoBean.getVod_id()).navigation();
                }
            });
            mCheckBoxCollect.setChecked(videoBean.isIs_collect());
            mCheckBoxCollect.setText(videoBean.isIs_collect() ? "已收藏" : "收藏");
            mCheckBoxCollect.setOnClickListener(v -> {
                if (onCollectListener != null) {
                    onCollectListener.onCollect(!videoBean.isIs_collect(), videoBean);
                }
            });
        }

        private void setScore(TextView tvScore, String vodType, String vodContinue, String score, boolean isEnd) {
            //continue 字段等于0说明视频有多集
            if (TextUtils.isEmpty(vodContinue) || Integer.parseInt(vodContinue) == 0) {
                //不连载，显示豆瓣评分
                if (TextUtils.isEmpty(score) || Double.parseDouble(score) == 0 || Double.parseDouble(score) == 10) {
                    tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                    tvScore.setText("暂无评分");
                } else {
                    tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_grade));
                    tvScore.setText(score);
                }
            }
            //视频已经完结
            else if (isEnd && !TextUtils.equals(vodType, "3")) {
                tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                tvScore.setText(tvScore.getResources().getString(R.string.video_episode_all, vodContinue));
            }
            //视频还在更新
            else {
                //长度小于4 说明是电视剧或连载动漫
                if (vodContinue.length() <= 4) {
                    tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                    tvScore.setText(tvScore.getResources().getString(R.string.video_episode, vodContinue));
                }
                //大于4 说明是综艺节目
                else {
                    tvScore.setTextColor(ContextCompat.getColor(tvScore.getContext(), R.color.homepage_font_episode));
                    tvScore.setText(tvScore.getResources().getString(R.string.video_stage, vodContinue));
                }
            }
        }

    }

    interface OnCollectListener {
        void onCollect(boolean isCollect, SearchResultVideoBean item);
    }
}
