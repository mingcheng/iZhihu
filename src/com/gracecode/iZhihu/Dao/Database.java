package com.gracecode.iZhihu.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class Database {

    private final static int DATABASE_VERSION = 1;
    private static final String FILE_DATABASE_NAME = "zhihu.sqlite";

    private static final String DATABASE_QUESTIONS_TABLE_NAME = "izhihu";
    private final static String[] SQL_CREATE_TABLES = {
        "CREATE TABLE " + DATABASE_QUESTIONS_TABLE_NAME + " (" +
            "    _id INTEGER PRIMARY KEY AUTOINCREMENT , " +
            "    id integer NOT NULL, server_id integer, answer_id integer, question_id integer, " +
            "    user_name text,  user_avatar text, question_title text, " +
            "    question_description text, content text, update_at text, " +
            "    unread integer DEFAULT 0, stared integer DEFAULT 0 );"
    };

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

    public Cursor getRecentQuestions() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(DATABASE_QUESTIONS_TABLE_NAME, null, null, null, null, null, "update_at DESC");
    }

    public Cursor getFavoritesQuestion() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(DATABASE_QUESTIONS_TABLE_NAME, null, " stared = 1 ", null, null, null, "update_at DESC");
    }

    public Cursor getSingleQuestion(int id) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        return null;
    }

    public int markSingleQuestionAsReaded(int id) {
        return 0;
    }

    public int markSingleQuestionAsFavourated(int id) {
        return 0;
    }

    public long insertSingleQuestion(JSONObject question) throws JSONException {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("id", question.getInt("id"));
        contentValues.put("question_id", question.getInt("question_id"));
        contentValues.put("answer_id", question.getInt("answer_id"));

        contentValues.put("question_title", question.getString("question_title"));
        contentValues.put("question_description", question.getString("question_description"));
        contentValues.put("content", question.getString("content"));

        contentValues.put("user_name", question.getString("user_name"));
        contentValues.put("update_at", question.getString("update_at"));
        contentValues.put("user_avatar", question.getString("user_avatar"));

        return db.insert(DATABASE_QUESTIONS_TABLE_NAME, null, contentValues);
    }


    public void close() {
        if (databaseOpenHelper != null) {
            databaseOpenHelper.close();
            databaseOpenHelper = null;
        }
    }
}
