package com.video.test.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.SearchHistoryWordBean;
import com.video.test.javabean.SearchHotWordBean;
import com.video.test.ui.adapter.BeanSearchBarHistoryAdapter;
import com.video.test.ui.adapter.BeanSearchBarHotAdapter;
import com.video.test.ui.listener.OnItemClickListener;
import com.video.test.ui.listener.SearchViewListener;
import com.video.test.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * @author Enoch
 * Created by enoch on 08/09/2017.
 */

public class BeanSearchBarView extends ConstraintLayout {

    private static final String TAG = "BeanSearchBarView";

    private SearchViewListener mSearchViewListener;
    private Context mContext;
    private EditText mEtSearchContent;
    private ImageView mIvDelete;
    private ImageView mIvClear;
    private RecyclerView mRvHistoryWord;
    private RecyclerView mRvHotWord;
    private BeanSearchBarHistoryAdapter mBeanSearchBarHistoryAdapter;
    private Group mHistoryGroup;
    private View mHotGroup;
    private BeanSearchBarHotAdapter mBeanSearchBarHotAdapter;
    private ImageView mBackBtn;
    private TextView mTvSearch;


    public BeanSearchBarView(Context context) {
        this(context, null);
    }

    public BeanSearchBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.bean_include_search_bar, this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        mEtSearchContent = findViewById(R.id.et_searchBar);
        mBackBtn = findViewById(R.id.iv_back_searchBar);
        mIvDelete = findViewById(R.id.iv_deleteBtn_searchBar);
        mIvClear = findViewById(R.id.iv_clear_searchBar);
        mRvHistoryWord = findViewById(R.id.rv_history_searchBar);
        mRvHotWord = findViewById(R.id.rv_hotWord_searchBar);
        mHistoryGroup = findViewById(R.id.group_history_searchBar);
        mHotGroup = findViewById(R.id.group_hot_searchBar);
        mTvSearch = findViewById(R.id.tv_search_searchBar);
        //控件初始化完成之后,加载Listener
        setAdapter();
        setListener();
        mEtSearchContent.requestFocus();
    }

    private void setAdapter() {
        mBeanSearchBarHistoryAdapter = new BeanSearchBarHistoryAdapter();
        mBeanSearchBarHotAdapter = new BeanSearchBarHotAdapter();

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        mRvHistoryWord.setLayoutManager(layoutManager);
        mRvHistoryWord.setAdapter(mBeanSearchBarHistoryAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRvHotWord.setLayoutManager(linearLayoutManager);
        mRvHotWord.setAdapter(mBeanSearchBarHotAdapter);

        querySearchHistoryWord();
        querySearchHotWords();
    }

    /**
     * 设置响应的监听器
     */
    private void setListener() {
        mBackBtn.setOnClickListener(new ViewOnclickListener());
        mIvDelete.setOnClickListener(new ViewOnclickListener());
        mIvClear.setOnClickListener(new ViewOnclickListener());
//        mEtSearchContent.setOnClickListener(new ViewOnclickListener());
        mEtSearchContent.addTextChangedListener(new EditChangedListener());
        mBeanSearchBarHistoryAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                LogUtils.d(TAG, "onFeedbackClick  position == " + position);
                CharSequence searchWord = ((TextView) view).getText();
                setEditTextContentAndSearchImmediately(searchWord.toString());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mBeanSearchBarHotAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.d(TAG, "onFeedbackClick  position == " + position);
                CharSequence searchWord = ((TextView) view).getText();
                setEditTextContentAndSearchImmediately(searchWord.toString());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });


        mEtSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && null != mEtSearchContent) {
                    notifyStartSearching(mEtSearchContent.getText().toString().trim());
                }
                return true;
            }
        });

        mTvSearch.setOnClickListener(v -> {
            if (null != mEtSearchContent) {
                notifyStartSearching(mEtSearchContent.getText().toString().trim());
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(mEtSearchContent.getWindowToken(), 0);
        }
    }

    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(mEtSearchContent, 0);
        }
    }

    /**
     * 搜索的回调监听
     */
    public void setSearchViewListener(SearchViewListener searchViewListener) {
        this.mSearchViewListener = searchViewListener;
    }

    /**
     * 设置EditText内容,并且立即进行搜索，此时应该停止搜索词联想
     */
    public void setEditTextContentAndSearchImmediately(String content) {
        if (null != mEtSearchContent) {
            if (mSearchViewListener != null) {
                mSearchViewListener.stopAssociation();
            }
            mEtSearchContent.setText(content);
            notifyStartSearching(mEtSearchContent.getText().toString().trim());
        }
    }

    private void hideSearchRecycleView() {
        if (null != mHistoryGroup && null != mHotGroup) {
            mHistoryGroup.setVisibility(GONE);
            mHotGroup.setVisibility(GONE);
        }
    }

    /**
     * 通知监听器, 进行搜索操作
     */
    public void notifyStartSearching(String text) {
        if (null != mSearchViewListener) {
            LogUtils.i(TAG, "notifyStartSearching  Start");
            mSearchViewListener.onSearch(text);
            insertSearchHistory(text);
            if (!TextUtils.isEmpty(text)) {
                hideSoftInput();
                hideSearchRecycleView();
                mEtSearchContent.clearFocus();
            }
        }
    }

    private void showSearchRecycleView() {
        if (null != mHistoryGroup && null != mHotGroup) {
            mHistoryGroup.setVisibility(VISIBLE);
            mHotGroup.setVisibility(VISIBLE);
        }
    }

    private void insertSearchHistory(String searchKeyword) {
        //判断是否是重复搜索
        boolean isHistoryWord = false;
        if (mBeanSearchBarHistoryAdapter != null && mBeanSearchBarHistoryAdapter.getData() != null) {
            for (SearchHistoryWordBean bean : mBeanSearchBarHistoryAdapter.getData()) {
                if (TextUtils.equals(bean.getKeyword(), searchKeyword)) {
                    isHistoryWord = true;
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(searchKeyword) && !isHistoryWord) {
            DBManager dbManager = DBManager.getInstance(TestApp.getContext());
            SearchHistoryWordBean searchHistoryWordBean = new SearchHistoryWordBean();
            searchHistoryWordBean.setKeyword(searchKeyword);
            dbManager.insertSearchHistoryWord(searchHistoryWordBean);
            LogUtils.d(TAG, "inserted ,ID ==" + searchHistoryWordBean.getId());
        }
    }

    private void deleteSearchHistory() {
        DBManager dbManager = DBManager.getInstance(TestApp.getContext());
        dbManager.deleteAllSearchHistoryWord();
        //删除所有数据后 重新查询,更新数据列表
        querySearchHistoryWord();
    }

    private void querySearchHistoryWord() {
        DBManager dbManager = DBManager.getInstance(TestApp.getContext());
        // 设置需要查询历史记录的数量  默认只需要10个历史记录
        List<SearchHistoryWordBean> searchHistoryWordBeans = dbManager.querySearchHistoryWord(8);
        for (SearchHistoryWordBean searchHistoryWordBean : searchHistoryWordBeans) {
            LogUtils.d(TAG, "querySearchHistoryWord keyword == " + searchHistoryWordBean.getKeyword() + " id == " + searchHistoryWordBean.getId());
        }

        if (null != mBeanSearchBarHistoryAdapter) {
            if (searchHistoryWordBeans.isEmpty()) {
                mHistoryGroup.setVisibility(GONE);
            } else {
                mHistoryGroup.setVisibility(VISIBLE);
                mBeanSearchBarHistoryAdapter.setData(searchHistoryWordBeans);
            }
        }
    }

    private void querySearchHotWords() {
        DBManager dbManager = DBManager.getInstance(TestApp.getContext());
        // 设置需要查询历史记录的数量  默认只需要10个历史记录
        List<SearchHotWordBean> searchHotWordBeans = dbManager.querySearchHot(10);
        for (SearchHotWordBean searchHotWordBean : searchHotWordBeans) {
            LogUtils.d(TAG, "querySearchHotWords keyword == " + searchHotWordBean.getVod_keyword() + " id == " + searchHotWordBean.getWord_id());
        }

        if (null != mBeanSearchBarHotAdapter) {
            mBeanSearchBarHotAdapter.setData(searchHotWordBeans);
        }
    }

    /**
     * 搜索内容改变的监听器
     */
    private class EditChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String string = s.toString();
            //这里进行搜索词联想，当搜索词为空，则隐藏联想列表，否则显示联想词列表并且进行联想
            mSearchViewListener.onAssociation(string);
            if (!"".equals(string)) {
                mIvDelete.setVisibility(VISIBLE);
            } else {
                mIvDelete.setVisibility(INVISIBLE);
                showSearchRecycleView();
                querySearchHistoryWord();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * 控件被点击的监听器
     */
    private class ViewOnclickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back_searchBar:
                    LogUtils.i(getClass(), "返回键被点击了");
                    ((Activity) mContext).finish();
                    break;
                case R.id.iv_deleteBtn_searchBar:
                    LogUtils.i(getClass(), "删除键被点击了");
                    mEtSearchContent.setText("");
                    mEtSearchContent.requestFocus();
                    showSoftInput();
                    break;
                case R.id.et_searchBar:
                    LogUtils.i(getClass(), "搜索框被点击了");
                    break;
                case R.id.iv_clear_searchBar:
                    LogUtils.i(getClass(), "清空历史按钮被点击了");
                    deleteSearchHistory();
                    break;
                default:
                    break;
            }
        }
    }

    @NotNull
    public String getInputWord() {
        return mEtSearchContent.getText().toString().trim();
    }

}
