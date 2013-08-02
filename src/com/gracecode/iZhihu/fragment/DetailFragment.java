package com.gracecode.iZhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.db.ThumbnailsDatabase;
import com.gracecode.iZhihu.util.Helper;
import taobe.tec.jcc.JChineseConvertor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailFragment extends WebViewFragment {
    private static final String TAG = DetailFragment.class.getName();
    private static final String KEY_SCROLL_BY = "key_scroll_by_";
    private static final String TEMPLATE_DETAIL_FILE = "detail.html";
    private static final String URL_ASSETS_PREFIX = "file:///android_asset/";
    private static final String MIME_HTML_TYPE = "text/html";
    public static final int ID_NOT_FOUND = 0;
    private static final int FIVE_MINUTES = 1000 * 60 * 5;
    private static final String NEED_CONVERT = "0x000";
    private static final String NONEED_CONVERT = "0x111";
    private static final int INITIAL_VIEW = 0x00;
    private static final int RESUME_JUMP = 0x01;

    private final Context context;
    private Activity activity;
    private static ThumbnailsDatabase thumbnailsDatabase;
    private SharedPreferences sharedPreferences;
    private static JChineseConvertor chineseConvertor = null;

    private Question question;
    private boolean isNeedIndent = false;
    private boolean isNeedReplaceSymbol = false;
    private boolean isNeedCacheThumbnails = true;
    private boolean isShareByTextOnly = false;
    private boolean isNeedConvertTraditionalChinese = false;

    private boolean isCustomFontEnabled = false;
    private String customFontPath;
    private String customBoldFontPath;

    /**
     * 刷新 UI 线程集中的地方
     */
    private final android.os.Handler UIChangedChangedHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INITIAL_VIEW:
                    getWebView().setVisibility(View.VISIBLE);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (getWebView() != null) {
                                UIChangedChangedHandler.sendEmptyMessage(RESUME_JUMP);
                            }
                        }
                    }, 1000);
                    break;

                case RESUME_JUMP:
                    getWebView().setScrollY(getSavedScrollYOffset());
                    break;
            }
        }
    };

    private void saveScrollYOffset() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(getKeyScrollById(), getWebView().getScrollY());
        edit.commit();
    }

    private int getSavedScrollYOffset() {
        return sharedPreferences.getInt(getKeyScrollById(), 0);
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (Helper.isExternalStorageExists() && !isShareByTextOnly) {
                new Thread(genScreenShots).start();
            }

            getWebView().pageDown(true);
            UIChangedChangedHandler.sendEmptyMessage(INITIAL_VIEW);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Helper.openWithBrowser(getActivity(), url);
            return true;
        }
    };

    private final Runnable genScreenShots = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = null;
            try {
                Thread.sleep(2000);
                if (!isTempScreenShotsFileCached()) {
                    File screenShotsFile = getTempScreenShotsFile();
                    FileOutputStream fileOutPutStream = new FileOutputStream(screenShotsFile);
                    bitmap = genCaptureBitmap();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutPutStream);
                        fileOutPutStream.flush();
                        fileOutPutStream.close();
                        Log.d(TAG, "Generated screenshots at " + screenShotsFile.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                // @todo mark do not generate again.
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    };


    public boolean isTempScreenShotsFileCached() {
        File tempScreenShotsFile = getTempScreenShotsFile();
        Boolean is5MinutesAgo = (System.currentTimeMillis() - tempScreenShotsFile.lastModified()) < FIVE_MINUTES;
        return tempScreenShotsFile.exists() && tempScreenShotsFile.length() > 0 && is5MinutesAgo;
    }


    public File getTempScreenShotsFile() {
        return new File(context.getCacheDir(), question.getAnswerId() + ".png");
    }

    private String getTemplateString() {
        String template;
        try {
            template = Helper.getFileContent(activity.getAssets().open(TEMPLATE_DETAIL_FILE));
        } catch (IOException e) {
            return "%s";
        }

        return template;
    }

    public DetailFragment(Context context, Question question) {
        this.context = context;
        this.question = question;

        try {
            chineseConvertor = JChineseConvertor.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        this.activity = getActivity();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        thumbnailsDatabase = new ThumbnailsDatabase(context);

        this.isNeedIndent = sharedPreferences.getBoolean(getString(R.string.key_indent), false);
        this.isNeedReplaceSymbol = sharedPreferences.getBoolean(getString(R.string.key_symbol), true);
        this.isNeedCacheThumbnails = sharedPreferences.getBoolean(getString(R.string.key_enable_cache), true);
        this.isShareByTextOnly = sharedPreferences.getBoolean(getString(R.string.key_share_text_only), false);
        this.isNeedConvertTraditionalChinese =
                sharedPreferences.getBoolean(getString(R.string.key_traditional_chinese), false);

        // Custom Fonts Preference
        this.isCustomFontEnabled = sharedPreferences.getBoolean(getString(R.string.key_custom_fonts_enabled), false);
        this.customFontPath = sharedPreferences.getString(getString(R.string.key_custom_fonts), "");
        this.customBoldFontPath = sharedPreferences.getString(getString(R.string.key_custom_fonts_bold), "");

        WebView webView = getWebView();
        WebSettings webSettings = webView.getSettings();

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);

        // Load page from generated HTML string.
        webView.loadDataWithBaseURL(URL_ASSETS_PREFIX, getFormatedContent(),
                MIME_HTML_TYPE, Helper.DEFAULT_CHARSET, null);

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.message() + "\nFrom line " + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }
        });
        webView.setVisibility(View.INVISIBLE);
    }

    private File getCachedFile() {
        String CONVERT_FLAG = isNeedConvertTraditionalChinese ? NEED_CONVERT : NONEED_CONVERT;
        String hashed = Base64.encodeToString((question.getId() + CONVERT_FLAG + getClassName()).getBytes(),
                Base64.DEFAULT);
        return new File(activity.getCacheDir(), hashed.trim());
    }

    private String getFormatedContent() {
        String className = getClassName(), templateString = getTemplateString(), data = "";


        // Cached by file.
        try {
            File cacheFile = getCachedFile();

            // @TODO Cache needed.
            if (false && cacheFile.exists()) {
                data = Helper.getFileContent(cacheFile.getAbsolutePath());
            } else {
                if (!isCustomFontEnabled) {
                    this.customFontPath = "";
                    this.customBoldFontPath = "";
                }

                data = String.format(templateString,
                        "file:///" + customFontPath,
                        "file:///" + customBoldFontPath,
                        className,
                        isNeedReplaceSymbol ? Helper.replaceSymbol(question.getTitle()) : question.getTitle(),
                        formatContent(question.getDescription()),
                        question.getUserName(),
                        "<p class='update-at'>" + question.getUpdateAt() + "</p>" + formatContent(question.getContent())
                );

                // @see https://code.google.com/p/jcc/
                data = (isNeedConvertTraditionalChinese) ?
                        chineseConvertor.s2t(data) : chineseConvertor.t2s(data);

//                util.putFileContent(cacheFile, new ByteArrayInputStream(data.getBytes()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return data;
        }
    }

    public String getPlainContent() {
        return Html.fromHtml(getFormatedContent()).toString();
    }

    private String getKeyScrollById() {
        return KEY_SCROLL_BY + question.getId();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveScrollYOffset();
    }

    private String formatParagraph(String content) {
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

        return content;
    }

    private String formatContent(String content) {
        content = formatParagraph(content);

        if (isNeedReplaceSymbol) {
            content = Helper.replaceSymbol(content);
        }

        if (isNeedCacheThumbnails) {
            List<String> imageUrls = Helper.getImageUrls(content);
            for (String url : imageUrls) {
                if (thumbnailsDatabase.isCached(url)) {
                    String localPathString = thumbnailsDatabase.getCachedPath(url);
                    Log.v(TAG, "Found offline cache file, replace online image " + url + " with file://" + localPathString);
                    content = content.replace(url, "file://" + localPathString);
                } else {
                    thumbnailsDatabase.add(url);
                }
            }
        }

        return content;
    }

    private String getClassName() {
        String className = "";
        int fontSize = Integer.parseInt(
                sharedPreferences.getString(getString(R.string.key_font_size), getString(R.string.default_font_size)));

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

        if (isNeedIndent) {
            className += " indent";
        }

        return className;
    }

    /**
     * 截取所有网页内容到 Bitmap
     *
     * @return Bitmap
     */
    Bitmap genCaptureBitmap() throws OutOfMemoryError {
        // @todo Future versions of WebView may not support use on other threads.
        try {
            Picture picture = getWebView().capturePicture();
            int height = picture.getHeight(), width = picture.getWidth();
            if (height == 0 || width == 0) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            return bitmap;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private int getPageScrollHeight() {
        return (int) (getWebView().getHeight() * 0.9);
    }

    synchronized public void scrollPageBy(int offset) {
        int contentHeight = (int) (getWebView().getContentHeight() * getWebView().getScale());
        int maxScrollHeight = contentHeight - getWebView().getHeight();
        int scrollY = getWebView().getScrollY() + offset;

        if (scrollY < 0) {
            scrollY = 0;
        }

        if (scrollY >= maxScrollHeight) {
            scrollY = maxScrollHeight;
        }

        getWebView().scrollTo(0, scrollY);
    }

    public void nextPage() {
        scrollPageBy(getPageScrollHeight());
    }

    public void prevPage() {
        scrollPageBy(getPageScrollHeight() * -1);
    }
}
