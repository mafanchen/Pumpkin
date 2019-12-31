package com.video.test.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.video.test.javabean.M3U8DownloadBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "M3_U8_DOWNLOAD_BEAN".
*/
public class M3U8DownloadBeanDao extends AbstractDao<M3U8DownloadBean, Long> {

    public static final String TABLENAME = "M3_U8_DOWNLOAD_BEAN";

    /**
     * Properties of entity M3U8DownloadBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property VideoId = new Property(1, String.class, "videoId", false, "VIDEO_ID");
        public final static Property VideoName = new Property(2, String.class, "videoName", false, "VIDEO_NAME");
        public final static Property VideoTotalName = new Property(3, String.class, "videoTotalName", false, "VIDEO_TOTAL_NAME");
        public final static Property VideoUrl = new Property(4, String.class, "videoUrl", false, "VIDEO_URL");
        public final static Property IsDownloaded = new Property(5, boolean.class, "isDownloaded", false, "IS_DOWNLOADED");
        public final static Property TaskStatus = new Property(6, int.class, "taskStatus", false, "TASK_STATUS");
        public final static Property TotalTime = new Property(7, long.class, "totalTime", false, "TOTAL_TIME");
        public final static Property TotalFileSize = new Property(8, long.class, "totalFileSize", false, "TOTAL_FILE_SIZE");
        public final static Property M3u8FilePath = new Property(9, String.class, "m3u8FilePath", false, "M3U8_FILE_PATH");
        public final static Property DirFilePath = new Property(10, String.class, "dirFilePath", false, "DIR_FILE_PATH");
        public final static Property CurTs = new Property(11, int.class, "curTs", false, "CUR_TS");
        public final static Property TotalTs = new Property(12, int.class, "totalTs", false, "TOTAL_TS");
        public final static Property Progress = new Property(13, float.class, "progress", false, "PROGRESS");
        public final static Property LocalHistory = new Property(14, double.class, "localHistory", false, "LOCAL_HISTORY");
    }


    public M3U8DownloadBeanDao(DaoConfig config) {
        super(config);
    }
    
    public M3U8DownloadBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"M3_U8_DOWNLOAD_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"VIDEO_ID\" TEXT NOT NULL ," + // 1: videoId
                "\"VIDEO_NAME\" TEXT NOT NULL ," + // 2: videoName
                "\"VIDEO_TOTAL_NAME\" TEXT NOT NULL ," + // 3: videoTotalName
                "\"VIDEO_URL\" TEXT NOT NULL ," + // 4: videoUrl
                "\"IS_DOWNLOADED\" INTEGER NOT NULL ," + // 5: isDownloaded
                "\"TASK_STATUS\" INTEGER NOT NULL ," + // 6: taskStatus
                "\"TOTAL_TIME\" INTEGER NOT NULL ," + // 7: totalTime
                "\"TOTAL_FILE_SIZE\" INTEGER NOT NULL ," + // 8: totalFileSize
                "\"M3U8_FILE_PATH\" TEXT," + // 9: m3u8FilePath
                "\"DIR_FILE_PATH\" TEXT," + // 10: dirFilePath
                "\"CUR_TS\" INTEGER NOT NULL ," + // 11: curTs
                "\"TOTAL_TS\" INTEGER NOT NULL ," + // 12: totalTs
                "\"PROGRESS\" REAL NOT NULL ," + // 13: progress
                "\"LOCAL_HISTORY\" REAL NOT NULL );"); // 14: localHistory
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"M3_U8_DOWNLOAD_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, M3U8DownloadBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getVideoId());
        stmt.bindString(3, entity.getVideoName());
        stmt.bindString(4, entity.getVideoTotalName());
        stmt.bindString(5, entity.getVideoUrl());
        stmt.bindLong(6, entity.getIsDownloaded() ? 1L: 0L);
        stmt.bindLong(7, entity.getTaskStatus());
        stmt.bindLong(8, entity.getTotalTime());
        stmt.bindLong(9, entity.getTotalFileSize());
 
        String m3u8FilePath = entity.getM3u8FilePath();
        if (m3u8FilePath != null) {
            stmt.bindString(10, m3u8FilePath);
        }
 
        String dirFilePath = entity.getDirFilePath();
        if (dirFilePath != null) {
            stmt.bindString(11, dirFilePath);
        }
        stmt.bindLong(12, entity.getCurTs());
        stmt.bindLong(13, entity.getTotalTs());
        stmt.bindDouble(14, entity.getProgress());
        stmt.bindDouble(15, entity.getLocalHistory());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, M3U8DownloadBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getVideoId());
        stmt.bindString(3, entity.getVideoName());
        stmt.bindString(4, entity.getVideoTotalName());
        stmt.bindString(5, entity.getVideoUrl());
        stmt.bindLong(6, entity.getIsDownloaded() ? 1L: 0L);
        stmt.bindLong(7, entity.getTaskStatus());
        stmt.bindLong(8, entity.getTotalTime());
        stmt.bindLong(9, entity.getTotalFileSize());
 
        String m3u8FilePath = entity.getM3u8FilePath();
        if (m3u8FilePath != null) {
            stmt.bindString(10, m3u8FilePath);
        }
 
        String dirFilePath = entity.getDirFilePath();
        if (dirFilePath != null) {
            stmt.bindString(11, dirFilePath);
        }
        stmt.bindLong(12, entity.getCurTs());
        stmt.bindLong(13, entity.getTotalTs());
        stmt.bindDouble(14, entity.getProgress());
        stmt.bindDouble(15, entity.getLocalHistory());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public M3U8DownloadBean readEntity(Cursor cursor, int offset) {
        M3U8DownloadBean entity = new M3U8DownloadBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // videoId
            cursor.getString(offset + 2), // videoName
            cursor.getString(offset + 3), // videoTotalName
            cursor.getString(offset + 4), // videoUrl
            cursor.getShort(offset + 5) != 0, // isDownloaded
            cursor.getInt(offset + 6), // taskStatus
            cursor.getLong(offset + 7), // totalTime
            cursor.getLong(offset + 8), // totalFileSize
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // m3u8FilePath
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // dirFilePath
            cursor.getInt(offset + 11), // curTs
            cursor.getInt(offset + 12), // totalTs
            cursor.getFloat(offset + 13), // progress
            cursor.getDouble(offset + 14) // localHistory
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, M3U8DownloadBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setVideoId(cursor.getString(offset + 1));
        entity.setVideoName(cursor.getString(offset + 2));
        entity.setVideoTotalName(cursor.getString(offset + 3));
        entity.setVideoUrl(cursor.getString(offset + 4));
        entity.setIsDownloaded(cursor.getShort(offset + 5) != 0);
        entity.setTaskStatus(cursor.getInt(offset + 6));
        entity.setTotalTime(cursor.getLong(offset + 7));
        entity.setTotalFileSize(cursor.getLong(offset + 8));
        entity.setM3u8FilePath(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDirFilePath(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setCurTs(cursor.getInt(offset + 11));
        entity.setTotalTs(cursor.getInt(offset + 12));
        entity.setProgress(cursor.getFloat(offset + 13));
        entity.setLocalHistory(cursor.getDouble(offset + 14));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(M3U8DownloadBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(M3U8DownloadBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(M3U8DownloadBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
