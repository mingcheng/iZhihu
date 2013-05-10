package com.gracecode.iZhihu.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.gracecode.iZhihu.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public final class QuestionsDatabase {
    public static final String COLUM_ID = "id";
    public static final String COLUM_QUESTION_ID = "question_id";
    public static final String COLUM_SERVER_ID = "server_id";
    public static final String COLUM_ANSWER_ID = "answer_id";

    public static final String COLUM_QUESTION_TITLE = "question_title";
    public static final String COLUM_CONTENT = "content";
    public static final String COLUM_USER_NAME = "user_name";
    public static final String COLUM_UNREAD = "unread";
    public static final String COLUM_STARED = "stared";
    public static final String COLUM_QUESTION_DESCRIPTION = "question_description";
    public static final String COLUM_UPDATE_AT = "update_at";
    public static final String COLUM_USER_AVATAR = "user_avatar";

    public static final int VALUE_READED = 1;
    public static final int VALUE_UNREADED = 0;
    public static final int VALUE_STARED = 1;
    public static final int VALUE_UNSTARED = 0;

    private static final int DATABASE_VERSION = 1;
    private static final String FILE_DATABASE_NAME = "zhihu.sqlite";
    private static final String DATABASE_QUESTIONS_TABLE_NAME = "izhihu";

    private final static String[] SQL_CREATE_TABLES = {
        "CREATE TABLE " + DATABASE_QUESTIONS_TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT , " +
            COLUM_ID + " integer NOT NULL UNIQUE, " + COLUM_SERVER_ID + " integer, " +
            COLUM_ANSWER_ID + " integer, " + COLUM_QUESTION_ID + " integer, " +
            COLUM_USER_NAME + " text,  " + COLUM_USER_AVATAR + " text, " + COLUM_QUESTION_TITLE + " text, " +
            COLUM_QUESTION_DESCRIPTION + " text, " + COLUM_CONTENT + " text, " + COLUM_UPDATE_AT + " text, " +
            COLUM_UNREAD + " integer DEFAULT 0, " + COLUM_STARED + " integer DEFAULT 0 );",
        "CREATE INDEX " + COLUM_ID + "_idx ON " + DATABASE_QUESTIONS_TABLE_NAME + "(" + COLUM_ID + ");",
        "CREATE INDEX " + COLUM_ANSWER_ID + "_idx ON " + DATABASE_QUESTIONS_TABLE_NAME + "(" + COLUM_ANSWER_ID + ");"
    };
    public static final int PRE_LIMIT_PAGE_SIZE = 25;
    public static final int FIRST_PAGE = 1;
    private static final String[] SELECT_ALL = new String[]{
        "_id", COLUM_ID, COLUM_QUESTION_ID, COLUM_ANSWER_ID,
        COLUM_STARED, COLUM_UNREAD,
        COLUM_USER_NAME, COLUM_USER_AVATAR,
        COLUM_UPDATE_AT,
        COLUM_QUESTION_TITLE, COLUM_QUESTION_DESCRIPTION,
        COLUM_CONTENT
    };

    protected File databaseFile;
    protected DatabaseOpenHelper databaseOpenHelper;
    protected Context context;
    private QuestionsDatabase questionsDatabase;

    private int idxId;
    private int idxQuestionId;
    private int idxTitle;
    private int idxContent;
    private int idxUserName;
    private int idxDespcrition;
    private int idxStared;
    private int idxUnread;
    private int idxUpdateAt;


    public int getStartId() {
        int returnId = HTTPRequester.DEFAULT_START_OFFSET;
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT max(" + COLUM_ID + ") AS " +
            COLUM_ID + " FROM " + DATABASE_QUESTIONS_TABLE_NAME + " LIMIT 1;", null);
        cursor.moveToFirst();

        int maxId = cursor.getInt(cursor.getColumnIndex(COLUM_ID));
        if (cursor.getCount() == 1 && maxId != 0) {
            returnId = maxId;
        }
        cursor.close();

        return returnId;
    }

    public long getTotalQuestionsCount() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(" + COLUM_ID + ") AS " +
            COLUM_ID + " FROM " + DATABASE_QUESTIONS_TABLE_NAME + " LIMIT 1;", null);
        cursor.moveToFirst();

        long count = cursor.getLong(cursor.getColumnIndex(COLUM_ID));
        cursor.close();
        return count;
    }

    public int getTotalPages() {
        return (int) Math.ceil(getTotalQuestionsCount() / PRE_LIMIT_PAGE_SIZE);
    }

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


    public QuestionsDatabase(Context context) {
        this.context = context;
        this.databaseFile = new File(context.getCacheDir(), FILE_DATABASE_NAME);
        this.databaseOpenHelper = new DatabaseOpenHelper(context, databaseFile.getAbsolutePath());
        this.questionsDatabase = this;
    }

    protected Cursor getRecentQuestionsCursor(int page) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_QUESTIONS_TABLE_NAME, SELECT_ALL, null, null, null, null,
            COLUM_UPDATE_AT + " DESC " + "LIMIT " + (page - 1) * PRE_LIMIT_PAGE_SIZE + "," + PRE_LIMIT_PAGE_SIZE);
        cursor.moveToFirst();
        return cursor;
    }

    protected Cursor getRecentQuestionsCursor() {
        return getRecentQuestionsCursor(FIRST_PAGE);
    }

    protected Cursor getStaredQuestionsCursor() {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(DATABASE_QUESTIONS_TABLE_NAME, SELECT_ALL, " stared = 1 ", null, null, null,
            COLUM_UPDATE_AT + " DESC");
    }

    public ArrayList<Question> getRecentQuestions() {
        return getRecentQuestions(FIRST_PAGE);
    }

    public ArrayList<Question> getRecentQuestions(int page) {
        ArrayList<Question> questionArrayList = new ArrayList<Question>();
        Cursor cursor = getRecentQuestionsCursor(page);

        for (getIndexFromCursor(cursor); cursor.moveToNext(); ) {
            Question question = convertCursorIntoQuestion(cursor);
            questionArrayList.add(question);
        }

        cursor.close();
        return questionArrayList;
    }

    public ArrayList<Question> getStaredQuestions() {
        ArrayList<Question> questionArrayList = new ArrayList<Question>();
        Cursor cursor = getStaredQuestionsCursor();

        for (getIndexFromCursor(cursor); cursor.moveToNext(); ) {
            Question question = convertCursorIntoQuestion(cursor);
            questionArrayList.add(question);
        }

        cursor.close();
        return questionArrayList;
    }

    private void getIndexFromCursor(Cursor cursor) {
        this.idxId = cursor.getColumnIndex(QuestionsDatabase.COLUM_ID);
        this.idxQuestionId = cursor.getColumnIndex(QuestionsDatabase.COLUM_QUESTION_ID);
        this.idxTitle = cursor.getColumnIndex(QuestionsDatabase.COLUM_QUESTION_TITLE);
        this.idxContent = cursor.getColumnIndex(QuestionsDatabase.COLUM_CONTENT);
        this.idxUserName = cursor.getColumnIndex(QuestionsDatabase.COLUM_USER_NAME);
        this.idxDespcrition = cursor.getColumnIndex(QuestionsDatabase.COLUM_QUESTION_DESCRIPTION);
        this.idxStared = cursor.getColumnIndex(QuestionsDatabase.COLUM_STARED);
        this.idxUnread = cursor.getColumnIndex(QuestionsDatabase.COLUM_UNREAD);
        this.idxUpdateAt = cursor.getColumnIndex(QuestionsDatabase.COLUM_UPDATE_AT);
    }

    private Question convertCursorIntoQuestion(Cursor cursor) {
        Question question = new Question(this);
        question.id = cursor.getInt(idxId);
        question.questionId = cursor.getInt(idxQuestionId);

        question.title = cursor.getString(idxTitle);
        question.content = cursor.getString(idxContent);
        question.description = cursor.getString(idxDespcrition);
        question.userName = cursor.getString(idxUserName);
        question.updateAt = cursor.getString(idxUpdateAt);

        question.stared = (cursor.getInt(idxStared) == VALUE_STARED) ? true : false;
        question.unread = (cursor.getInt(idxUnread) == VALUE_UNREADED) ? true : false;

        return question;
    }

    protected Cursor getSingleQuestionCursor(int id) throws QuestionNotFoundException {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(DATABASE_QUESTIONS_TABLE_NAME, SELECT_ALL, COLUM_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() < 1) {
            cursor.close();
            throw new QuestionNotFoundException(context.getString(R.string.notfound));
        }

        return cursor;
    }


    /**
     * 获取单条问题
     *
     * @param id
     * @return
     * @throws QuestionNotFoundException
     */
    public Question getSingleQuestion(int id) {
        Question question = new Question(this);
        try {
            Cursor cursor = getSingleQuestionCursor(id);
            getIndexFromCursor(cursor);
            question = convertCursorIntoQuestion(cursor);
            cursor.close();
        } catch (QuestionNotFoundException e) {
            e.printStackTrace();
        }
        return question;
    }

    public int markSingleQuestionAsReaded(int id) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUM_UNREAD, VALUE_READED);

        return db.update(DATABASE_QUESTIONS_TABLE_NAME, contentValues, COLUM_ID + " = ?", new String[]{String.valueOf(id)});
    }

    protected boolean isStared(int id) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        String sql = "SELECT " + COLUM_STARED + " FROM " + DATABASE_QUESTIONS_TABLE_NAME +
            " WHERE " + COLUM_ID + " = " + id + " LIMIT 1";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 1) {
            return false;
        }

        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex(QuestionsDatabase.COLUM_STARED));
        cursor.close();
        return (result == VALUE_STARED) ? true : false;
    }

    public int markQuestionAsStared(int id, boolean flag) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUM_STARED, flag ? VALUE_STARED : VALUE_UNSTARED);

        return db.update(DATABASE_QUESTIONS_TABLE_NAME, contentValues, COLUM_ID + " = ?", new String[]{String.valueOf(id)});
    }


    /**
     * 从 JSON 数据中直接插入到数据库
     *
     * @param question
     * @return
     * @throws JSONException
     * @throws SQLiteException
     */
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

        long result = db.insert(DATABASE_QUESTIONS_TABLE_NAME, null, contentValues);
        db.close();
        return result;
    }


    /**
     * 关闭数据库
     */
    public void close() {
        if (databaseOpenHelper != null) {
            databaseOpenHelper.close();
            databaseOpenHelper = null;
        }
    }

    public class QuestionNotFoundException extends SQLiteException {
        public QuestionNotFoundException(String message) {
            super(message);
        }
    }
}
