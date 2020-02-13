package com.video.test.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.video.test.javabean.base.ISelectableBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史纪录 收藏 等页面的基类，控制全选反选
 *
 * @author : AhhhhDong
 * @date : 2019/4/1 14:53
 */
public abstract class BaseSelectableAdapter<T extends ISelectableBean, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected static final String TAG = "BaseSelectableAdapter";

    public void selectAllItem() {
        List<T> mList = getList();
        if (mList == null) return;
        //未选中的
        List<T> unSelectList = new ArrayList<>();
        //是否已经全部选中
        boolean isAllSelected = true;
        for (T bean : mList) {
            if (!bean.isSelected()) {
                isAllSelected = false;
                unSelectList.add(bean);
            }
        }
        List<T> list = isAllSelected ? mList : unSelectList;
        for (T bean : list) {
            bean.setSelected(!bean.isSelected());
            LogUtils.d(TAG, "selectAllItem == " + bean.isSelected());
        }
        notifyDataSetChanged();
    }

    protected abstract List<T> getList();

}
