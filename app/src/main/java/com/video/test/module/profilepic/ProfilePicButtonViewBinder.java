package com.video.test.module.profilepic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.javabean.ProfilePicButtonBean;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

public class ProfilePicButtonViewBinder extends ItemViewBinder<ProfilePicButtonBean, ProfilePicButtonViewBinder.ButtonViewHolder> {
    private static final String TAG = "ProfilePicButtonViewBin";
    ProfilePicButtonClickListener mListener;

    public ProfilePicButtonViewBinder(ProfilePicButtonClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    protected ButtonViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.bean_recycle_item_profile_pic_btn, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ButtonViewHolder holder, @NonNull ProfilePicButtonBean item) {

        holder.mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "mPictureCommit click");
                if (null != mListener) {
                    mListener.onClick();
                    holder.mTvCommit.setText(R.string.profile_saving);
                }
            }
        });
    }


    static class ButtonViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_commit_profilePic_activity)
        TextView mTvCommit;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
