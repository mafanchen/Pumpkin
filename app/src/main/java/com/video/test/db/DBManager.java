package com.video.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.video.test.javabean.CountryCodeBean;
import com.video.test.javabean.DomainNameBean;
import com.video.test.javabean.HistoryListBean;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.javabean.SearchHistoryWordBean;
import com.video.test.javabean.SearchHotWordBean;
import com.video.test.utils.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * @author Enoch Created on 2018/8/3.
 */
public class DBManager {
    private static final String TAG = "DBManager";

    private Context mContext;
    private static final String dbName = "bean_db";

    private DaoMaster.DevOpenHelper mDevOpenHelper;
    private static volatile DBManager mInstance;

    private DBManager(Context context) {
        this.mContext = context;
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }


    public static DBManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (DBManager.class) {
                if (null == mInstance) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    private SQLiteDatabase getReadableDatabase() {
        if (null == mDevOpenHelper) {
            mDevOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
        }
        return mDevOpenHelper.getReadableDatabase();
    }


    private SQLiteDatabase getWritableDatabase() {
        if (null == mDevOpenHelper) {
            mDevOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
        }
        return mDevOpenHelper.getWritableDatabase();
    }

    public void insertSearchHistoryWord(SearchHistoryWordBean searchHistoryWordBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchHistoryWordBeanDao searchHistoryWordBeanDao = daoSession.getSearchHistoryWordBeanDao();
        searchHistoryWordBeanDao.insert(searchHistoryWordBean);
    }

    public void deleteSearchHistoryWord(SearchHistoryWordBean searchHistoryWordBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchHistoryWordBeanDao searchHistoryWordBeanDao = daoSession.getSearchHistoryWordBeanDao();
        searchHistoryWordBeanDao.delete(searchHistoryWordBean);
    }

    public void deleteAllSearchHistoryWord() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchHistoryWordBeanDao searchHistoryWordBeanDao = daoSession.getSearchHistoryWordBeanDao();
        searchHistoryWordBeanDao.deleteAll();
    }


    public List<SearchHistoryWordBean> querySearchHistoryWord(int limitNumber) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<SearchHistoryWordBean> searchHistoryWordBeanQueryBuilder = daoSession.queryBuilder(SearchHistoryWordBean.class);
        searchHistoryWordBeanQueryBuilder.orderDesc(SearchHistoryWordBeanDao.Properties.Id);
        return searchHistoryWordBeanQueryBuilder.limit(limitNumber).list();
    }

    public void insertCountryCode(CountryCodeBean countryCodeBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CountryCodeBeanDao countryCodeBeanDao = daoSession.getCountryCodeBeanDao();
        countryCodeBeanDao.insert(countryCodeBean);
    }

    public void deleteCountryCode(CountryCodeBean countryCodeBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CountryCodeBeanDao countryCodeBeanDao = daoSession.getCountryCodeBeanDao();
        countryCodeBeanDao.delete(countryCodeBean);
    }


    public List<CountryCodeBean> queryCountryCode() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<CountryCodeBean> countryCodeBeanQueryBuilder = daoSession.queryBuilder(CountryCodeBean.class);
        return countryCodeBeanQueryBuilder.list();

    }


    public void insertSearchHotWord(SearchHotWordBean searchHotWordBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchHotWordBeanDao searchHotWordBeanDao = daoSession.getSearchHotWordBeanDao();
        searchHotWordBeanDao.insert(searchHotWordBean);
    }

    public void deleteSearchHotWord(SearchHotWordBean searchHotWordBean) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchHotWordBeanDao searchHotWordBeanDao = daoSession.getSearchHotWordBeanDao();
        searchHotWordBeanDao.delete(searchHotWordBean);
    }

    public void deleteAllHotHistoryWord() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SearchHotWordBeanDao searchHotWordBeanDao = daoSession.getSearchHotWordBeanDao();
        searchHotWordBeanDao.deleteAll();
    }

    public List<SearchHotWordBean> querySearchHot(int limitNumber) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<SearchHotWordBean> searchHotWordBeanQueryBuilder = daoSession.queryBuilder(SearchHotWordBean.class);
        return searchHotWordBeanQueryBuilder.orderAsc().limit(limitNumber).list();
    }


    public long insertM3U8Task(M3U8DownloadBean m3U8DownloadBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        M3U8DownloadBeanDao m3U8DownloadBeanDao = daoSession.getM3U8DownloadBeanDao();
        long rowId = m3U8DownloadBeanDao.insert(m3U8DownloadBean);
        LogUtils.d(TAG, "insertM3U8Task success rowId : " + rowId);
        return rowId;
    }

    public void deleteM3U8Task(M3U8DownloadBean... beans) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        M3U8DownloadBeanDao m3U8DownloadBeanDao = daoSession.getM3U8DownloadBeanDao();
        for (M3U8DownloadBean bean : beans) {
            m3U8DownloadBeanDao.delete(bean);
        }
    }

    public void deleteM3U8TaskByUrl(String url) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        M3U8DownloadBeanDao m3U8DownloadBeanDao = daoSession.getM3U8DownloadBeanDao();
        QueryBuilder<M3U8DownloadBean> queryBuilder = m3U8DownloadBeanDao.queryBuilder();
        List<M3U8DownloadBean> list = queryBuilder.where(M3U8DownloadBeanDao.Properties.VideoUrl.eq(url)).list();
        if (!list.isEmpty()) {
            m3U8DownloadBeanDao.delete(list.get(0));
        }
    }

    public void deleteAllM3U8Task() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        M3U8DownloadBeanDao m3U8DownloadBeanDao = daoSession.getM3U8DownloadBeanDao();
        m3U8DownloadBeanDao.deleteAll();
    }

    public void updateM3U8Task(M3U8DownloadBean m3U8DownloadBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        M3U8DownloadBeanDao m3U8DownloadBeanDao = daoSession.getM3U8DownloadBeanDao();
        m3U8DownloadBeanDao.update(m3U8DownloadBean);
    }

    public M3U8DownloadBean queryM3U8BeanFromVideoUrl(String playUrl) {
        List<M3U8DownloadBean> list = queryM3U8Tasks();
        M3U8DownloadBean m3U8DownloadBean = null;
        for (M3U8DownloadBean m3U8Bean : list) {
            if (playUrl.equals(m3U8Bean.getVideoUrl())) {
                m3U8DownloadBean = m3U8Bean;
            }
        }
        return m3U8DownloadBean;
    }


    public List<M3U8DownloadBean> queryM3U8Tasks() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<M3U8DownloadBean> m3U8DownloadBeanQueryBuilder = daoSession.queryBuilder(M3U8DownloadBean.class);
        return m3U8DownloadBeanQueryBuilder.list();
    }

    public List<M3U8DownloadBean> queryM3U8DownloadedTasks() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<M3U8DownloadBean> m3U8DownloadBeanQueryBuilder = daoSession.queryBuilder(M3U8DownloadBean.class);
        return m3U8DownloadBeanQueryBuilder.where(M3U8DownloadBeanDao.Properties.IsDownloaded.eq(true)).list();
    }


    public List<M3U8DownloadBean> queryM3U8DownloadingTasks() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<M3U8DownloadBean> m3U8DownloadBeanQueryBuilder = daoSession.queryBuilder(M3U8DownloadBean.class);
        return m3U8DownloadBeanQueryBuilder.where(M3U8DownloadBeanDao.Properties.IsDownloaded.eq(false)).list();
    }

    public List<M3U8DownloadBean> queryM3U8TasksByVideoId(String id) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<M3U8DownloadBean> m3U8DownloadBeanQueryBuilder = daoSession.queryBuilder(M3U8DownloadBean.class);
        return m3U8DownloadBeanQueryBuilder.where(M3U8DownloadBeanDao.Properties.VideoId.eq(id)).list();
    }

    public void insertDomainUrls(DomainNameBean domainNameBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DomainNameBeanDao domainNameBeanDao = daoSession.getDomainNameBeanDao();
        domainNameBeanDao.insert(domainNameBean);
    }

    public void deleteDomainUrls(DomainNameBean domainNameBean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DomainNameBeanDao domainNameBeanDao = daoSession.getDomainNameBeanDao();
        domainNameBeanDao.delete(domainNameBean);
    }

    public void deleteAllDomainUrls() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DomainNameBeanDao domainNameBeanDao = daoSession.getDomainNameBeanDao();
        domainNameBeanDao.deleteAll();
    }


    public List<DomainNameBean> queryDomainUrls() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<DomainNameBean> domainNameBeanQueryBuilder = daoSession.queryBuilder(DomainNameBean.class);
        domainNameBeanQueryBuilder.orderDesc(SearchHistoryWordBeanDao.Properties.Id);
        return domainNameBeanQueryBuilder.list();
    }


    public HistoryListBean queryHistoryByVodId(String vodId) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        QueryBuilder<HistoryListBean> queryBuilder = daoSession.queryBuilder(HistoryListBean.class);
        List<HistoryListBean> list = queryBuilder.where(HistoryListBeanDao.Properties.Vod_id.eq(vodId)).list();
        if (list != null && !list.isEmpty()) {
            Log.d("DBManager", list.toString());
            return list.get(0);
        }
        return null;
    }

    public void addOrReplaceHistory(HistoryListBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        daoSession.insertOrReplace(bean);
    }
}
