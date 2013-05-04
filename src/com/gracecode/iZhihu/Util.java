package com.gracecode.iZhihu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Util {

    public static void openWithBrowser(Activity activity, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        activity.startActivity(i);
    }

    public static void sendMail(Activity activity, String[] to, String subject, String content) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, to);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, content);
        activity.startActivity(Intent.createChooser(i, activity.getString(R.string.choose_email_client)));
    }
}
