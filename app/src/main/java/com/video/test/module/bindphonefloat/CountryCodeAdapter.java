package com.video.test.module.bindphonefloat;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.test.R;

/**
 * @author : AhhhhDong
 * @date : 2019/5/14 16:34
 */
public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.ViewHolder> {

    private String[] mCountryCode;
    private String[] mCountryName;
    private OnCountryCodeSelectedListener mListener;

    public CountryCodeAdapter(String[] countryCode, String[] countryName, OnCountryCodeSelectedListener listener) {
        this.mCountryCode = countryCode;
        this.mCountryName = countryName;
        this.mListener = listener;
    }

    public String getCountryCode(int position) {
        return mCountryCode[position];
    }

    public String getCountryName(int position) {
        return mCountryName[position];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bean_reacycle_item_select_country_code, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvName.setText(mCountryName[i] + " +" + mCountryCode[i]);
        viewHolder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onSelected(mCountryCode[i], mCountryName[i]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCountryCode.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnCountryCodeSelectedListener {
        void onSelected(String code, String name);
    }
}
