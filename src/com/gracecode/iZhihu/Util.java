package com.gracecode.iZhihu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Dao.Question;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final String DEFAULT_CHARSET = "utf-8";
    public static final String REGEX_MATCH_IMAGE = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
    public static final String MIME_IMAGE_PNG = "image/png";
    private static final String MIME_PLAIN_TEXT = "text/plain";

    public static void openWithBrowser(Activity activity, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(i);
    }

    public static String replaceSymbol(String content) {
        content = content.replaceAll("(“)([\u4e00-\u9fa5|\\s]+)", "「$2");
        content = content.replaceAll("([\u4e30-\u9fa5|\\s]+)(”)", "$1」");
        content = content.replaceAll("(‘)([\u4e00-\u9fa5|\\s]+)", "『$2");
        content = content.replaceAll("([\u4e00-\u9fa5|\\s]+)(’)", "$1』");
        content = content.replaceAll("……", "…");
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

        for (int len; (len = inputStream.read(buffer)) != -1; ) {
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

        String sLine;
        while ((sLine = br.readLine()) != null) {
            String s = sLine + "\n";
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
        List<String> result = new ArrayList<>();
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


    /**
     * 判断 WIFI 是否已经打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null) && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }


    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null) && (activeNetInfo.isConnected());
    }


    /**
     * 外部存储是否存在
     *
     * @return boolean
     */
    public static boolean isExternalStorageExists() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取外部存储的路径
     *
     * @return String 路径
     */
    public static String getExternalStoragePath() {
        File externalStorageDirectory = null;

        if (isExternalStorageExists()) {
            externalStorageDirectory = Environment.getExternalStorageDirectory();
        }
        return (externalStorageDirectory != null) ? externalStorageDirectory.getAbsolutePath() : null;
    }


    /**
     * 两个文件的相互拷贝
     *
     * @param source File
     * @param dest   File
     * @throws IOException
     */
    public static void copyFile(File source, File dest) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    /**
     * 通过系统 Intent 分享到其他程序
     *
     * @param message   String
     * @param imagePath Uri
     */
    public static void openShareIntentWithImage(Context context, String message, Uri imagePath) {
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


    public static void openShareIntentWithPlainText(Context context, String title, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setType(MIME_PLAIN_TEXT);

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }


    public static void openShareIntentWithPlainText(Context context, String message) {
        openShareIntentWithPlainText(context, "", message);
    }

    public static String inputStream2String(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }

        return out.toString();
    }

    public static boolean isZhihuInstalled(Activity activity) {
        PackageInfo packageInfo;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo("com.zhihu.android", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        return (packageInfo == null) ? false : true;
    }

    public static void startDetailActivity(Activity activity, ArrayList<Question> questions, int position) {
        Intent intent = new Intent(activity, Detail.class);
        intent.putExtra(Detail.INTENT_EXTRA_CURRENT_POSITION, position);
        intent.putExtra(Detail.INTENT_EXTRA_QUESTIONS, questions);

        if (questions.get(position) != null) {
            intent.putExtra(Detail.INTENT_EXTRA_CURRENT_QUESTION, questions.get(position));
        }

        activity.startActivityForResult(intent, Intent.FILL_IN_PACKAGE);
    }
}
