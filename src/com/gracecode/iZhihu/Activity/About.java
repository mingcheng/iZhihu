package com.gracecode.iZhihu.Activity;

import android.os.Bundle;
import android.webkit.WebViewFragment;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-4
 */
public class About extends BaseActivity {
    private static final String TEMPLATE_README_FILE = "about.html";
    private static final String URL_ASSETS_PREFIX = "file:///android_asset/";
    private WebViewFragment fragWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setDisplayHomeAsUpEnabled(true);
        fragWebView = new WebViewFragment();
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, fragWebView)
            .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragWebView.getWebView().loadUrl(URL_ASSETS_PREFIX + TEMPLATE_README_FILE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
