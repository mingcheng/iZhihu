package com.gracecode.iZhihu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.gracecode.iZhihu.R;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class Detail extends BaseActivity {
    private static View webView;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);
        webView = findViewById(R.id.webview);

        intent = getIntent();

    }


}
