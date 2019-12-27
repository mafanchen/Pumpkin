package com.video.test.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Enoch
 * Created by Enoch on 2017/5/17.
 */

public class BeanViewStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mTitles;
    private List<Fragment> mFragments;

    public BeanViewStatePagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new ArrayList<>();
        this.mTitles = new ArrayList<>();
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }


    public void setItems(List<Fragment> fragments) {
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    public void setItems(List<Fragment> fragments, List<String> titles) {
        this.mFragments = fragments;
        this.mTitles = titles;
        notifyDataSetChanged();
    }

    public void setItems(List<Fragment> fragments, String[] titles) {
        this.mFragments = fragments;
        this.mTitles = Arrays.asList(titles);
        notifyDataSetChanged();
    }

    public void addItem(Fragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
        notifyDataSetChanged();
    }

    public void delItem(int position) {
        mFragments.remove(position);
        mTitles.remove(position);
        notifyDataSetChanged();
    }

    public int delItem(String title) {
        int index = mTitles.indexOf(title);
        if (index != -1) {
            delItem(index);
        }
        return index;
    }

    public void modifyTitle(int position, String title) {
        mTitles.set(position, title);
        notifyDataSetChanged();
    }

}
