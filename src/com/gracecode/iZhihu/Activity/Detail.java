package com.gracecode.iZhihu.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Fragments.QuestionDetail;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Tasks.ToggleStarTask;
import com.gracecode.iZhihu.Util;

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
    private static final String TEMPLATE_DETAIL_FILE = "detail.html";
    private static final String URL_ASSETS_PREFIX = "file:///android_asset/";

    private Database database;
    private int id;
    private QuestionDetail fragQuestionDetail;
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
            template = getFileContent(getAssets().open(TEMPLATE_DETAIL_FILE));
        } catch (IOException e) {
            return "%s";
        }

        return template;
    }

    private Cursor getCursorFromDatabase() {
        this.cursor = database.getSingleQuestion(id);
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
        this.id = getIntent().getIntExtra(Database.COLUM_ID, 0);
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

        this.title = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE));
        this.content = cursor.getString(cursor.getColumnIndex(Database.COLUM_CONTENT));
        this.author = cursor.getString(cursor.getColumnIndex(Database.COLUM_USER_NAME));
        this.description = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_DESCRIPTION));
        this.questionId = cursor.getInt(cursor.getColumnIndex(Database.COLUM_QUESTION_ID));

        int fontSize = Integer.parseInt(
            sharedPreferences.getString(getString(R.string.key_font_size), getString(R.string.default_font_size)));

        String data = String.format(getTemplateString(), fontSize, title, description, author, formatContent(content));
        fragQuestionDetail.getWebView()
            .loadDataWithBaseURL(URL_ASSETS_PREFIX, data, "text/html", DEFAULT_CHARSET, null);


        database.markSingleQuestionAsReaded(id);
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
        if (id != 0) {
            getMenuInflater().inflate(R.menu.detail, menu);
            MenuItem item = menu.findItem(R.id.menu_favorite);
            item.setIcon(isStared() ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                ToggleStarTask toggleStarTask = new ToggleStarTask(context, new ToggleStarTask.Callback() {
                    @Override
                    public void onPostExecute(Object o) {
                        boolean isStared = isStared();
                        item.setIcon(isStared ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);

                        String showMessage = getString(isStared ? R.string.mark_as_stared : R.string.cancel_mark_as_stared);
                        Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPreExecute() {
                    }
                });

                toggleStarTask.execute(new ToggleStarTask.Item(id, !isStared()));
                return true;

            case R.id.menu_view_at_zhihu:
                String url = getString(R.string.url_zhihu_questioin_pre) + questionId;
                Util.openWithBrowser(this, url);
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
