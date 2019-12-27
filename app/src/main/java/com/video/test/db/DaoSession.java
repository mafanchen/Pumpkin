package com.video.test.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.video.test.javabean.CountryCodeBean;
import com.video.test.javabean.DomainNameBean;
import com.video.test.javabean.HistoryListBean;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.javabean.SearchHistoryWordBean;
import com.video.test.javabean.SearchHotWordBean;

import com.video.test.db.CountryCodeBeanDao;
import com.video.test.db.DomainNameBeanDao;
import com.video.test.db.HistoryListBeanDao;
import com.video.test.db.M3U8DownloadBeanDao;
import com.video.test.db.SearchHistoryWordBeanDao;
import com.video.test.db.SearchHotWordBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig countryCodeBeanDaoConfig;
    private final DaoConfig domainNameBeanDaoConfig;
    private final DaoConfig historyListBeanDaoConfig;
    private final DaoConfig m3U8DownloadBeanDaoConfig;
    private final DaoConfig searchHistoryWordBeanDaoConfig;
    private final DaoConfig searchHotWordBeanDaoConfig;

    private final CountryCodeBeanDao countryCodeBeanDao;
    private final DomainNameBeanDao domainNameBeanDao;
    private final HistoryListBeanDao historyListBeanDao;
    private final M3U8DownloadBeanDao m3U8DownloadBeanDao;
    private final SearchHistoryWordBeanDao searchHistoryWordBeanDao;
    private final SearchHotWordBeanDao searchHotWordBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        countryCodeBeanDaoConfig = daoConfigMap.get(CountryCodeBeanDao.class).clone();
        countryCodeBeanDaoConfig.initIdentityScope(type);

        domainNameBeanDaoConfig = daoConfigMap.get(DomainNameBeanDao.class).clone();
        domainNameBeanDaoConfig.initIdentityScope(type);

        historyListBeanDaoConfig = daoConfigMap.get(HistoryListBeanDao.class).clone();
        historyListBeanDaoConfig.initIdentityScope(type);

        m3U8DownloadBeanDaoConfig = daoConfigMap.get(M3U8DownloadBeanDao.class).clone();
        m3U8DownloadBeanDaoConfig.initIdentityScope(type);

        searchHistoryWordBeanDaoConfig = daoConfigMap.get(SearchHistoryWordBeanDao.class).clone();
        searchHistoryWordBeanDaoConfig.initIdentityScope(type);

        searchHotWordBeanDaoConfig = daoConfigMap.get(SearchHotWordBeanDao.class).clone();
        searchHotWordBeanDaoConfig.initIdentityScope(type);

        countryCodeBeanDao = new CountryCodeBeanDao(countryCodeBeanDaoConfig, this);
        domainNameBeanDao = new DomainNameBeanDao(domainNameBeanDaoConfig, this);
        historyListBeanDao = new HistoryListBeanDao(historyListBeanDaoConfig, this);
        m3U8DownloadBeanDao = new M3U8DownloadBeanDao(m3U8DownloadBeanDaoConfig, this);
        searchHistoryWordBeanDao = new SearchHistoryWordBeanDao(searchHistoryWordBeanDaoConfig, this);
        searchHotWordBeanDao = new SearchHotWordBeanDao(searchHotWordBeanDaoConfig, this);

        registerDao(CountryCodeBean.class, countryCodeBeanDao);
        registerDao(DomainNameBean.class, domainNameBeanDao);
        registerDao(HistoryListBean.class, historyListBeanDao);
        registerDao(M3U8DownloadBean.class, m3U8DownloadBeanDao);
        registerDao(SearchHistoryWordBean.class, searchHistoryWordBeanDao);
        registerDao(SearchHotWordBean.class, searchHotWordBeanDao);
    }
    
    public void clear() {
        countryCodeBeanDaoConfig.clearIdentityScope();
        domainNameBeanDaoConfig.clearIdentityScope();
        historyListBeanDaoConfig.clearIdentityScope();
        m3U8DownloadBeanDaoConfig.clearIdentityScope();
        searchHistoryWordBeanDaoConfig.clearIdentityScope();
        searchHotWordBeanDaoConfig.clearIdentityScope();
    }

    public CountryCodeBeanDao getCountryCodeBeanDao() {
        return countryCodeBeanDao;
    }

    public DomainNameBeanDao getDomainNameBeanDao() {
        return domainNameBeanDao;
    }

    public HistoryListBeanDao getHistoryListBeanDao() {
        return historyListBeanDao;
    }

    public M3U8DownloadBeanDao getM3U8DownloadBeanDao() {
        return m3U8DownloadBeanDao;
    }

    public SearchHistoryWordBeanDao getSearchHistoryWordBeanDao() {
        return searchHistoryWordBeanDao;
    }

    public SearchHotWordBeanDao getSearchHotWordBeanDao() {
        return searchHotWordBeanDao;
    }

}
