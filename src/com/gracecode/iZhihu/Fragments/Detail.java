package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.R;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Detail extends WebViewFragment {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String TEMPLATE_DETAIL_FILE = "detail.html";
    private static final String URL_ASSETS_PREFIX = "file:///android_asset/";
    private static final String MIME_TYPE = "text/html";
    public static final int ID_NOT_FOUND = 0;

    private final int id;
    private final Context context;
    private Database database;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private Cursor cursor;
    private String title;
    private String content;
    private String author;
    private String description;
    private int questionId;

    private String getFileContent(InputStream fis) throws IOException {
        InputStreamReader isr = new InputStreamReader(fis, DEFAULT_CHARSET);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sbContent = new StringBuffer();
        String sLine = "";

        while ((sLine = br.readLine()) != null) {
            String s = sLine.toString() + "\n";
            sbContent = sbContent.append(s);
        }

        isr.close();
        br.close();
        return sbContent.toString();
    }

    private String getFileContent(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        String content = getFileContent(fis);
        fis.close();
        return content;
    }

    private String getTemplateString() {
        String template = "";
        try {
            template = getFileContent(activity.getAssets().open(TEMPLATE_DETAIL_FILE));
        } catch (IOException e) {
            return "%s";
        }

        return template;
    }


    public Detail(int id, Context context) {
        this.id = id;
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.activity = getActivity();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        this.database = new Database(context);

        database.markSingleQuestionAsReaded(id);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        getQuestionData();
        String data = String.format(getTemplateString(), getClassName(),
            title, description, author, formatContent(content));

        getWebView().loadDataWithBaseURL(URL_ASSETS_PREFIX, data, MIME_TYPE, DEFAULT_CHARSET, null);
    }

    private void getQuestionData() {
        this.cursor = getCursorFromDatabase();

        if (cursor == null) {
            return;
        }
        this.title = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE));
        this.content = cursor.getString(cursor.getColumnIndex(Database.COLUM_CONTENT));
        this.author = cursor.getString(cursor.getColumnIndex(Database.COLUM_USER_NAME));
        this.description = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_DESCRIPTION));
        this.questionId = cursor.getInt(cursor.getColumnIndex(Database.COLUM_QUESTION_ID));
    }

    public boolean isStared() {
        int index = cursor.getColumnIndex(Database.COLUM_STARED);
        return (getCursorFromDatabase().getInt(index) == Database.VALUE_STARED) ? true : false;
    }

    private Cursor getCursorFromDatabase() {
        Cursor cursor = database.getSingleQuestion(id);
        if (cursor.getCount() != 1) {
            Toast.makeText(activity, getString(R.string.notfound), Toast.LENGTH_LONG).show();
            return null;
        }
        cursor.moveToFirst();

        return cursor;
    }

    private String formatContent(String content) {

        Pattern pattern = Pattern.compile("<hr>");
        Matcher matcher = pattern.matcher(content);
        content = matcher.replaceAll("</p><p>");

        pattern = Pattern.compile("<p>\\s+");
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("<p>");

        pattern = Pattern.compile("<p></p>");
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        return "<p>" + content + "</p>";
    }

    private String getClassName() {
        String className = "";
        int fontSize = Integer.parseInt(
            sharedPreferences.getString(getString(R.string.key_font_size), getString(R.string.default_font_size)));

        boolean needIndent = sharedPreferences.getBoolean(getString(R.string.key_indent), false);

        switch (fontSize) {
            case 12:
                className += " tiny";
                break;
            case 14:
                className += " small";
                break;
            case 16:
                className += " normal";
                break;
            case 18:
                className += " big";
                break;
            case 20:
                className += " huge";
                break;
            default:
                className += " normal";
        }

        if (needIndent) {
            className += " indent";
        }

        return className;
    }

    public int getQuestionId() {
        return questionId;
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        if (database != null) {
            database.close();
            database = null;
        }

        super.onDestroy();
    }
}
