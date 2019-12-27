package com.video.test.ui.listener;

import android.view.View;

/**
 * @author Enoch Created on 2018/8/3.
 */
public interface OnItemClickListener {


    /*短按*/
    void onItemClick(View view, int position);


    /*长按*/
    void onItemLongClick(View view, int position);


}
