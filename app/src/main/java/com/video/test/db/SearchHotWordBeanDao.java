package com.video.test.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.video.test.javabean.SearchHotWordBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEARCH_HOT_WORD_BEAN".
*/
public class SearchHotWordBeanDao extends AbstractDao<SearchHotWordBean, Void> {

    public static final String TABLENAME = "SEARCH_HOT_WORD_BEAN";

    /**
     * Properties of entity SearchHotWordBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Word_id = new Property(0, String.class, "word_id", false, "WORD_ID");
        public final static Property Vod_keyword = new Property(1, String.class, "vod_keyword", false, "VOD_KEYWORD");
    }


    public SearchHotWordBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SearchHotWordBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEARCH_HOT_WORD_BEAN\" (" + //
                "\"WORD_ID\" TEXT," + // 0: word_id
                "\"VOD_KEYWORD\" TEXT NOT NULL );"); // 1: vod_keyword
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEARCH_HOT_WORD_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SearchHotWordBean entity) {
        stmt.clearBindings();
 
        String word_id = entity.getWord_id();
        if (word_id != null) {
            stmt.bindString(1, word_id);
        }
        stmt.bindString(2, entity.getVod_keyword());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SearchHotWordBean entity) {
        stmt.clearBindings();
 
        String word_id = entity.getWord_id();
        if (word_id != null) {
            stmt.bindString(1, word_id);
        }
        stmt.bindString(2, entity.getVod_keyword());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public SearchHotWordBean readEntity(Cursor cursor, int offset) {
        SearchHotWordBean entity = new SearchHotWordBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // word_id
            cursor.getString(offset + 1) // vod_keyword
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SearchHotWordBean entity, int offset) {
        entity.setWord_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setVod_keyword(cursor.getString(offset + 1));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(SearchHotWordBean entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(SearchHotWordBean entity) {
        return null;
    }

    @Override
    public boolean hasKey(SearchHotWordBean entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
