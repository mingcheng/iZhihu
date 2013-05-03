package com.gracecode.iZhihu.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Fragments.QuestionDetail;
import com.gracecode.iZhihu.R;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class Detail extends BaseActivity {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String TEMPLATE_FILE_NAME = "detail.html";

    private Database database;
    private int questionId;
    private QuestionDetail fragQuestionDetail;
    private Cursor cursor;

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

    private Cursor getCursorFromDatabase() {
        this.cursor = database.getSingleQuestion(questionId);
        if (cursor.getCount() != 1) {
            Toast.makeText(context, getString(R.string.notfound), Toast.LENGTH_LONG).show();
            finish();
        }
        cursor.moveToFirst();

        return cursor;
    }

    private boolean isStared() {
        return getCursorFromDatabase().getInt(cursor.getColumnIndex(Database.COLUM_STARED))
            == Database.VALUE_STARED ? true : false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setDisplayHomeAsUpEnabled(true);

        this.database = new Database(context);
        this.questionId = getIntent().getIntExtra(Database.COLUM_ID, 0);
        this.fragQuestionDetail = new QuestionDetail();

        getCursorFromDatabase();
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, fragQuestionDetail)
            .commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        String title = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(Database.COLUM_CONTENT));
        String author = cursor.getString(cursor.getColumnIndex(Database.COLUM_USER_NAME));
        String description = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_DESCRIPTION));


        content = formatContent(content);

        String data = String.format(getTemplateString(), title, description, author, content);

        int fontSize = Integer.parseInt(
            sharedPreferences.getString(getString(R.string.key_font_size), getString(R.string.default_font_size)));


        fragQuestionDetail.getWebView()
            .loadDataWithBaseURL("file:///android_asset/", data, "text/html", DEFAULT_CHARSET, null);
        database.markSingleQuestionAsReaded(questionId);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.menu_favorite);
        item.setIcon(isStared() ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                database.markQuestionAsFavourated(questionId, !isStared());

                boolean isStared = isStared();
                item.setIcon(isStared ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
                Toast.makeText(context,
                    getString(isStared ? R.string.mark_as_stared : R.string.cancel_mark_as_stared), Toast.LENGTH_SHORT).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
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
