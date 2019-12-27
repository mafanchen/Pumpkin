package com.video.test.ui.viewbinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.AdInfoBean;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.IntentUtils;
import com.video.test.utils.LogUtils;

import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * java类作用描述
 *
 * @author : AhhhhDong
 * @date : 2019/3/20 16:02
 */
public class VideoAdItemViewBinder extends ItemViewBinder<AdInfoBean, VideoAdItemViewBinder.ViewHolder> {

    private static final String TAG = "VideoAdItemViewBinder";

    private OnAdClickListener onAdClickListener;

    public void setOnAdClickListener(OnAdClickListener onAdClickListener) {
        this.onAdClickListener = onAdClickListener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.bean_recycle_item_video_ad, parent, false));

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull AdInfoBean item) {
        GlideApp.with(holder.itemView.getContext())
                .load(item.getAdPic())
                .transform(new CenterCrop())
                .into((ImageView) holder.itemView);
        holder.itemView.setOnClickListener(view -> onAdClick(holder.itemView.getContext(), item));
    }

    private void onAdClick(Context context, AdInfoBean adInfoBean) {
        switch (adInfoBean.getType()) {
            case AdInfoBean.Type.WEB:
                LogUtils.d(TAG, "AD_TYPE_WEB");
                if (adInfoBean.getAdUrl() != null) {
                    TestApp.getInstance().startActivity(IntentUtils.getBrowserIntent(adInfoBean.getAdUrl()));
                }
                break;
            case AdInfoBean.Type.DOWNLOAD:
                LogUtils.d(TAG, "AD_TYPE_DOWNLOAD");
                String downloadUrl = adInfoBean.getAndroidUrl();
                if (downloadUrl != null) {
                    DownloadUtil.startDownloadOrOpenDownloadedFile(context, downloadUrl);
                }
                break;
            default:
                break;
        }
        onAdClickListener.onAdClick(adInfoBean.getId());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAdClickListener {
        void onAdClick(String adId);
    }
}
