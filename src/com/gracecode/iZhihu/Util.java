package com.gracecode.iZhihu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final String DEFAULT_CHARSET = "utf-8";
    public static final String REGEX_MATCH_IMAGE = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
    public static final String MIME_IMAGE_PNG = "image/png";

    public static void openWithBrowser(Activity activity, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        activity.startActivity(i);
    }

    public static String replaceSymbol(String content) {
        content = content.replaceAll("“", "「");
        content = content.replaceAll("”", "」");
        content = content.replaceAll("‘", "『");
        content = content.replaceAll("’", "』");
        return content;
    }

    public static void sendMail(Activity activity, String[] to, String subject, String content) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, to);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, content);
        activity.startActivity(Intent.createChooser(i, activity.getString(R.string.choose_email_client)));
    }

    public static boolean putFileContent(File file, InputStream inputStream) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            return false;
        }

        byte[] buffer = new byte[1024];
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        for (int len = -1; (len = inputStream.read(buffer)) != -1; ) {
            fileOutputStream.write(buffer, 0, len);
        }

        inputStream.close();
        fileOutputStream.close();
        return true;
    }

    public static String getFileContent(InputStream fis) throws IOException {
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

    public static String getFileContent(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        String content = getFileContent(fis);
        fis.close();
        return content;
    }

    public static List<String> getImageUrls(String content) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(REGEX_MATCH_IMAGE, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) {
                result.add(group);
            }
        }
        return result;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
            && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    public static String getExternalStoragePath() {
        File externalStorageDirectory = null;
        boolean isExternalStorageExists = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isExternalStorageExists) {
            externalStorageDirectory = Environment.getExternalStorageDirectory();
        }

        return externalStorageDirectory != null ? externalStorageDirectory.getAbsolutePath() : null;
    }


    /**
     * 通过系统 Intent 分享到其他程序
     *
     * @param message
     * @param imagePath
     */
    public static void openShareIntent(Context context, String message, Uri imagePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.putExtra(Intent.EXTRA_STREAM, imagePath);
        intent.setType(MIME_IMAGE_PNG);

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }


    public static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }
}
