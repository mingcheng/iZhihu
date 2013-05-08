package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailFragment extends WebViewFragment {
    private static final String KEY_SCROLL_BY = "key_scroll_by_";
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String TEMPLATE_DETAIL_FILE = "detail.html";
    private static final String URL_ASSETS_PREFIX = "file:///android_asset/";
    private static final String MIME_TYPE = "text/html";
    public static final int ID_NOT_FOUND = 0;
    private static final int DONT_HAVE_SCROLLY = 0;
    private static final long AUTO_SCROLL_DELAY = 500;

    private final int id;
    private final Context context;
    private QuestionsDatabase questionsDatabase;
    private Activity activity;
    private SharedPreferences sharedPreferences;

    private Question question;

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

    public DetailFragment(int id, Context context) {
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
        this.questionsDatabase = new QuestionsDatabase(context);

        questionsDatabase.markSingleQuestionAsReaded(id);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            question = questionsDatabase.getSingleQuestion(id);
        } catch (QuestionsDatabase.QuestionNotFoundException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            activity.finish();
        }

        String data = String.format(getTemplateString(), getClassName(),
            question.title, question.description, question.userName, formatContent(question.content));

//        getWebView().setScrollbarFadingEnabled(false);
        getWebView().setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

        getWebView().getSettings().setLoadWithOverviewMode(true);
        getWebView().getSettings().setUseWideViewPort(true);
        getWebView().getSettings().setJavaScriptEnabled(true);

        getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                decideAutoScroll();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Util.openWithBrowser(getActivity(), url);
                return true;
            }
        });

        getWebView().loadDataWithBaseURL(URL_ASSETS_PREFIX, data, MIME_TYPE, DEFAULT_CHARSET, null);
        question.markAsRead();
    }

    private String getKeyScrollById() {
        return KEY_SCROLL_BY + this.id;
    }

    /**
     * 判断是否需要自动滚动
     */
    private void decideAutoScroll() {
        final int savedScrollY = sharedPreferences.getInt(getKeyScrollById(), DONT_HAVE_SCROLLY);
        boolean needAutoScroll = sharedPreferences.getBoolean(getString(R.string.key_auto_scroll), true);

        if (needAutoScroll && savedScrollY != DONT_HAVE_SCROLLY) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWebView().scrollTo(0, savedScrollY);
                }
            }, AUTO_SCROLL_DELAY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getKeyScrollById(), getWebView().getScrollY());
        editor.commit();
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

        content = "<p>" + content + "</p>";

        if (sharedPreferences.getBoolean(getString(R.string.key_symbol), false)) {
            content = content.replaceAll("“", "「");
            content = content.replaceAll("”", "」");

            content = content.replaceAll("‘", "『");
            content = content.replaceAll("’", "』");
        }

        return content + "<p class='update-at'>" + question.updateAt + "</p>";
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

    public boolean isStared() {
        if (question == null) {
            return false;
        }
        return question.isStared();
    }

    public int getQuestionId() {
        return question.questionId;
    }

    /**
     * 截取所有网页内容到 Bitmap
     *
     * @return
     * @TODO 内存的问题
     */
    public Bitmap getCapture() {
        WebView webView = getWebView();
        Picture picture = webView.capturePicture();

        int height = picture.getHeight() * (webView.getWidth() / picture.getWidth());
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(),
            height, Bitmap.Config.ARGB_8888);

        try {
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
        } catch (OutOfMemoryError error) {

        }
        return bitmap;
    }

    @Override
    public void onDestroy() {
        if (questionsDatabase != null) {
            questionsDatabase.close();
            questionsDatabase = null;
        }
        super.onDestroy();
    }
}
