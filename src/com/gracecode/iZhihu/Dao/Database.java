package com.gracecode.iZhihu.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public final class Database {

    public static final String COLUM_ID = "id";
    public static final String COLUM_QUESTION_ID = "question_id";
    public static final String COLUM_QUESTION_TITLE = "question_title";
    public static final String COLUM_CONTENT = "content";
    public static final String COLUM_USER_NAME = "user_name";
    public static final String COLUM_UNREAD = "unread";
    public static final String COLUM_STARED = "stared";
    public static final String COLUM_ANSWER_ID = "answer_id";
    public static final String COLUM_QUESTION_DESCRIPTION = "question_description";
    public static final String COLUM_UPDATE_AT = "update_at";
    public static final String COLUM_USER_AVATAR = "user_avatar";

    public static final int VALUE_READED = 1;
    public static final int VALUE_STARED = 1;
    public static final int VALUE_UNSTARED = 0;

    private final static int DATABASE_VERSION = 1;
    private static final String FILE_DATABASE_NAME = "zhihu.sqlite";

    private static final String DATABASE_QUESTIONS_TABLE_NAME = "izhihu";
    private static final Object COLUM_SERVER_ID = "server_id";

    private final static String[] SQL_CREATE_TABLES = {
        "CREATE TABLE " + DATABASE_QUESTIONS_TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT , " +
            COLUM_ID + " integer NOT NULL UNIQUE, " + COLUM_SERVER_ID + " integer, " +
            COLUM_ANSWER_ID + " integer, " + COLUM_QUESTION_ID + " integer UNIQUE, " +
            COLUM_USER_NAME + " text,  " + COLUM_USER_AVATAR + " text, " + COLUM_QUESTION_TITLE + " text, " +
            COLUM_QUESTION_DESCRIPTION + " text, " + COLUM_CONTENT + " text, " + COLUM_UPDATE_AT + " text, " +
            COLUM_UNREAD + " integer DEFAULT 0, " + COLUM_STARED + " integer DEFAULT 0 );"
    };
    public static final int PRE_LIMIT_PAGE_SIZE = 25;


    protected static File databaseFile;
    protected static DatabaseOpenHelper databaseOpenHelper;
    protected static Context context;

    private class DatabaseOpenHelper extends SQLiteOpenHelper {
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


        @Override
        public synchronized void close() {
            super.close();
        }
    }


    public Database(Context context) {
        this.context = context;
        this.databaseFile = new File(context.getCacheDir(), FILE_DATABASE_NAME);
        this.databaseOpenHelper = new DatabaseOpenHelper(context, databaseFile.getAbsolutePath());
    }

    public Cursor getRecentQuestions(int offset) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(DATABASE_QUESTIONS_TABLE_NAME, null, null, null, null, null,
            COLUM_UPDATE_AT + " DESC LIMIT " + PRE_LIMIT_PAGE_SIZE + " OFFSET " + offset);
    }

    public Cursor getRecentQuestions() {
        return getRecentQuestions(0);
    }

    public Cursor getFavoritesQuestion() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(DATABASE_QUESTIONS_TABLE_NAME, null, " stared = 1 ", null, null, null, "update_at DESC");
    }

    public Cursor getSingleQuestion(int id) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DATABASE_QUESTIONS_TABLE_NAME + " WHERE " + COLUM_ID + " = " + id + " LIMIT 1";
        return db.rawQuery(sql, null);
    }

    public int markSingleQuestionAsReaded(int id) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUM_UNREAD, VALUE_READED);

        return db.update(DATABASE_QUESTIONS_TABLE_NAME, contentValues, COLUM_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int markQuestionAsFavourated(int id, boolean flag) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUM_STARED, flag ? VALUE_STARED : VALUE_UNSTARED);

        return db.update(DATABASE_QUESTIONS_TABLE_NAME, contentValues, COLUM_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public long insertSingleQuestion(JSONObject question) throws JSONException, SQLiteException {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUM_ID, question.getInt(COLUM_ID));
        contentValues.put(COLUM_QUESTION_ID, question.getInt(COLUM_QUESTION_ID));
        contentValues.put(COLUM_ANSWER_ID, question.getInt(COLUM_ANSWER_ID));

        contentValues.put(COLUM_QUESTION_TITLE, question.getString(COLUM_QUESTION_TITLE));
        contentValues.put(COLUM_QUESTION_DESCRIPTION, question.getString(COLUM_QUESTION_DESCRIPTION));
        contentValues.put(COLUM_CONTENT, question.getString(COLUM_CONTENT));

        contentValues.put(COLUM_USER_NAME, question.getString(COLUM_USER_NAME));
        contentValues.put(COLUM_UPDATE_AT, question.getString(COLUM_UPDATE_AT));
        contentValues.put(COLUM_USER_AVATAR, question.getString(COLUM_USER_AVATAR));

        return db.insertOrThrow(DATABASE_QUESTIONS_TABLE_NAME, null, contentValues);
    }


    public void close() {
        if (databaseOpenHelper != null) {
            databaseOpenHelper.close();
            databaseOpenHelper = null;
        }
    }
}
