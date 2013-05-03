package com.gracecode.iZhihu.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.R;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class Detail extends BaseActivity {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String TEMPLATE_FILE_NAME = "detail.html";

    private static WebView webView;
    private Intent intent;
    private Database database;
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
            template = getFileContent(getAssets().open(TEMPLATE_FILE_NAME));
        } catch (IOException e) {
            return "%s";
        }

        return template;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);

        this.webView = (WebView) findViewById(R.id.webview);
        this.intent = getIntent();

        this.database = new Database(context);
        this.questionId = getIntent().getIntExtra(Database.COLUM_ID, 0);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        Cursor cursor = database.getSingleQuestion(questionId);
        if (cursor.getCount() != 1) {
            Toast.makeText(context, getString(R.string.notfound), Toast.LENGTH_LONG).show();
            finish();
        }

        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(Database.COLUM_CONTENT));
        String author = cursor.getString(cursor.getColumnIndex(Database.COLUM_USER_NAME));
        String description = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_DESCRIPTION));

        String data = String.format(getTemplateString(), title, description, author, content);

        webView.loadDataWithBaseURL("file:///android_asset/", data, "text/html", DEFAULT_CHARSET, null);

        database.markSingleQuestionAsReaded(questionId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
            database = null;
        }
    }
}
