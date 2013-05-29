package com.gracecode.iZhihu.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import org.apache.http.HttpStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThumbnailsDatabase {
    private static final int DATABASE_VERSION = 1;
    private static final String FILE_DATABASE_NAME = "thumbnails.sqlite";
    private static final String DATABASE_THUMBNAILS_TABLE_NAME = "thumbnails";
    private static final String DIR_THUMBNAILS_NAME = "/thumbnails";

    protected static final String COLUM_URL = "url";
    protected static final String COLUM_LOCAL_PATH = "local";
    protected static final String COLUM_HASH = "hash";
    protected static final String COLUM_WIDTH = "width";
    protected static final String COLUM_HEIGHT = "height";
    protected static final String COLUM_SIZE = "size";
    protected static final String COLUM_TIMESTAMP = "timeStamp";
    protected static final String COLUM_ID = "id";
    protected static final String COLUM_MIME_TYPE = "mimeType";
    protected static final String COLUM_STATUS = "status";

    private final static String[] SQL_CREATE_TABLES = {
            "CREATE TABLE " + DATABASE_THUMBNAILS_TABLE_NAME + " (" +
                    COLUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                    COLUM_URL + " text NOT NULL UNIQUE," +
                    COLUM_LOCAL_PATH + " text DEFAULT NULL UNIQUE," +
                    COLUM_HASH + " text," +
                    COLUM_STATUS + " int," +
                    COLUM_MIME_TYPE + " text," +
                    COLUM_TIMESTAMP + " long," +
                    COLUM_SIZE + " long," +
                    COLUM_WIDTH + " int," +
                    COLUM_HEIGHT + " int" + ");",
            "CREATE INDEX " + COLUM_URL + "_idx ON " + DATABASE_THUMBNAILS_TABLE_NAME + "(" + COLUM_URL + ");",
            "CREATE INDEX " + COLUM_STATUS + "_idx ON " + DATABASE_THUMBNAILS_TABLE_NAME + "(" + COLUM_STATUS + ");",
            "CREATE INDEX " + COLUM_LOCAL_PATH + "_idx ON " + DATABASE_THUMBNAILS_TABLE_NAME + "(" + COLUM_LOCAL_PATH + ");"
    };

    private final Context context;
    private final File databaseFile;
    private final DatabaseOpenHelper databaseOpenHelper;

    public void close() {
        databaseOpenHelper.close();
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        public DatabaseOpenHelper(Context context, String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            for (String sql : SQL_CREATE_TABLES) {
                sqLiteDatabase.execSQL(sql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }

    }

    public ThumbnailsDatabase(Context context) {
        this.context = context;

        this.databaseFile = new File(context.getCacheDir(), FILE_DATABASE_NAME);
        this.databaseOpenHelper = new DatabaseOpenHelper(context, databaseFile.getAbsolutePath());
    }

    public File getLocalCacheDirectory() {
        File directory = new File(context.getCacheDir().getAbsolutePath() + DIR_THUMBNAILS_NAME);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }


    /**
     * 增加到缓存列表中
     *
     * @param url
     * @return
     */
    public boolean add(String url) {
        if (isCached(url)) {
            return false;
        }

        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUM_URL, url);

            long values = db.insertOrThrow(DATABASE_THUMBNAILS_TABLE_NAME, null, contentValues);
            return values > 1;
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.close();
        }
    }


    /**
     * 获取已缓存的列表
     *
     * @param url
     * @return
     */
    public String getCachedPath(String url) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_THUMBNAILS_TABLE_NAME,
                new String[]{COLUM_LOCAL_PATH},
                COLUM_URL + "=?", new String[]{url}, null, null, null, "1");

        try {
            cursor.moveToFirst();
            int count = cursor.getCount();
            int index = cursor.getColumnIndex(COLUM_LOCAL_PATH);

            String result = (count > 0) ? cursor.getString(index) : null;
            if (result == null) {
                result = "";
            }
            return result.trim();
        } finally {
            cursor.close();
            db.close();
        }
    }


    /**
     * 判断是否被缓存
     *
     * @param url
     * @return
     */
    public boolean isCached(String url) {
        String path = getCachedPath(url);
        return path.length() > 0;
    }


    /**
     * 清除数据
     *
     * @return
     */
    public void clearAll() {
        List<String> cachedThumbnails = getCachedThumbnails();
        for (int i = 0, size = cachedThumbnails.size(); i < size; i++) {
            File thumbnail = new File(cachedThumbnails.get(i));
            thumbnail.delete();
        }

        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        try {
            db.delete(DATABASE_THUMBNAILS_TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    public boolean markAsCached(String url, String localPath, String mimeType, int status) {
        File localPathFile = new File(localPath);
        if (!localPathFile.isFile() || !localPathFile.exists()) {
            return false;
        }

        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUM_LOCAL_PATH, localPath);
        contentValues.put(COLUM_WIDTH, com.gracecode.iZhihu.Tasks.FetchThumbnailTask.DEFAULT_WIDTH);
        contentValues.put(COLUM_HEIGHT, com.gracecode.iZhihu.Tasks.FetchThumbnailTask.DEFAULT_HEIGHT);
        contentValues.put(COLUM_SIZE, localPathFile.length());
        contentValues.put(COLUM_MIME_TYPE, mimeType);
        contentValues.put(COLUM_TIMESTAMP, System.currentTimeMillis());
        contentValues.put(COLUM_STATUS, status);

        try {
            int result = db.update(DATABASE_THUMBNAILS_TABLE_NAME, contentValues, COLUM_URL + " = ?", new String[]{url});
            return result >= 1;
        } finally {
            db.close();
        }
    }


    public List<String> getNotCachedThumbnails() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_THUMBNAILS_TABLE_NAME, new String[]{COLUM_URL},
                COLUM_LOCAL_PATH + " IS NULL AND " + COLUM_STATUS + " IS NULL", null, null, null, COLUM_ID + " DESC", null);

        int idxUrl = cursor.getColumnIndex(COLUM_URL);
        List<String> result = new ArrayList<>();

        try {
            for (int i = 0, count = cursor.getCount(); i < count; i++) {
                cursor.moveToPosition(i);
                result.add(cursor.getString(idxUrl));
            }
            return result;
        } finally {
            cursor.close();
            db.close();
        }
    }


    public List<String> getCachedThumbnails() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_THUMBNAILS_TABLE_NAME, new String[]{COLUM_LOCAL_PATH},
                COLUM_LOCAL_PATH + " IS NULL AND " + COLUM_STATUS + "=" + HttpStatus.SC_OK, null, null, null, COLUM_ID + " DESC", null);

        int idxUrl = cursor.getColumnIndex(COLUM_LOCAL_PATH);
        List<String> result = new ArrayList<>();

        try {
            for (int i = 0, count = cursor.getCount(); i < count; i++) {
                cursor.moveToPosition(i);
                result.add(cursor.getString(idxUrl));
            }
            return result;
        } finally {
            cursor.close();
            db.close();
        }
    }


    public int getTotalCachedCount() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_THUMBNAILS_TABLE_NAME, new String[]{"COUNT(" + COLUM_ID + ") AS " + COLUM_ID},
                COLUM_LOCAL_PATH + " NOT NULL AND " + COLUM_STATUS + "=" + HttpStatus.SC_OK, null, null, null, null, null);

        try {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(COLUM_ID));
        } finally {
            cursor.close();
            db.close();
        }
    }


    public long getTotalCachedSize() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_THUMBNAILS_TABLE_NAME, new String[]{"SUM(" + COLUM_SIZE + ") AS " + COLUM_ID},
                COLUM_LOCAL_PATH + " NOT NULL AND " + COLUM_STATUS + "=" + HttpStatus.SC_OK, null, null, null, null, null);

        try {
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(COLUM_ID));
        } finally {
            cursor.close();
            db.close();
        }
    }
}
