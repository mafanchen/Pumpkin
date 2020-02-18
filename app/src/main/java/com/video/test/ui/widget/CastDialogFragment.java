package com.video.test.ui.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.video.test.R;
import com.video.test.ui.adapter.CastDeviceAdapter;
import com.video.test.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author Enoch Created on 2018/9/28.
 */
public class CastDialogFragment extends DialogFragment {
    private static final String TAG = "CastDialogFragment";

    CastItemClickListener mCastItemClickListener;
    private RecyclerView mRvDeviceList;
    private TextView mTvHelper;
    private TextView mTvCancel;
    private CastDeviceAdapter mCastDeviceAdapter;


    public static CastDialogFragment newInstance() {
        Bundle bundle = new Bundle();
        CastDialogFragment fragment = new CastDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialog);
    }


    @Override
    public void onStart() {
        super.onStart();

        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(false);

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //设置 dialog的背景为 透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //注册 EventBus
        EventBus.getDefault().register(this);
        //去除标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.bean_dialog_cast, container, false);
    }


    public void setCastItemClickListener(CastItemClickListener mCastItemClickListener) {
        this.mCastItemClickListener = mCastItemClickListener;
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);

        } catch (IllegalStateException e) {
            Log.d(TAG, "show state error: " + e.getMessage());
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvDeviceList = view.findViewById(R.id.rv_castDialog);
        mTvHelper = view.findViewById(R.id.tv_helper_castDialog);
        mTvCancel = view.findViewById(R.id.tv_cancel_castDialog);
        mCastDeviceAdapter = new CastDeviceAdapter();
        mRvDeviceList.setAdapter(mCastDeviceAdapter);

        mCastDeviceAdapter.setDeviceListener((position, info) -> {
            if (mCastItemClickListener != null) {
                mCastItemClickListener.onCastItemClick(position, info);
            }
        });
        mTvHelper.setOnClickListener(v -> {
            if (mCastItemClickListener != null) {
                mCastItemClickListener.onHelperClick();
            }
        });
        mTvCancel.setOnClickListener(v -> {
            if (mCastItemClickListener != null) {
                mCastItemClickListener.onCancelClick();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reciveInfos(List<LelinkServiceInfo> lelinkServiceInfos) {
        for (LelinkServiceInfo info : lelinkServiceInfos) {
            LogUtils.d(TAG, "EventBus lelinkServiceInfo name : " + info.getName());
        }
        mCastDeviceAdapter.setDeviceData(lelinkServiceInfos);
    }


    public interface CastItemClickListener {

        void onCastItemClick(int position, LelinkServiceInfo info);

        void onCancelClick();

        void onHelperClick();
    }
}
