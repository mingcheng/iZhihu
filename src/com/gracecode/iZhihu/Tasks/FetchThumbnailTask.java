package com.gracecode.iZhihu.Tasks;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import com.gracecode.iZhihu.Dao.ThumbnailsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-9
 */
public class FetchThumbnailTask extends AsyncTask<Void, Integer, Integer> {
    private static final String USER_AGENT_STRING = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_1_1 like Mac OS X; en) " +
        "AppleWebKit/534.46.0 (KHTML, like Gecko) CriOS/19.0.1084.60 Mobile/9B206 Safari/7534.48.3";

    private static final int DEFAULT_HEIGHT = 0;
    private static final int DEFAULT_WIDTH = 0;
    private static final int DOWNLOAD_NOTIFY_ID = 0;
    private final ThumbnailsDatabase database;
    private final int TIMEOUT_SECONDS = 5000;
    private final DefaultHttpClient httpClient;
    private final List<String> urls;
    private final Context context;
    private final NotificationManager notificationManager;
    private NotificationCompat.Builder notificationCompat;


    public FetchThumbnailTask(Context context, ThumbnailsDatabase database, List<String> urls) {
        this.database = database;
        this.httpClient = new DefaultHttpClient();
        this.urls = urls;
        this.context = context;

        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_SECONDS);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SECONDS);
        HttpProtocolParams.setUserAgent(httpParams, USER_AGENT_STRING);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int downloaded = 0;
        for (int i = 0, size = urls.size(); i < size; i++) {
            String url = urls.get(i);
            if (!database.isCached(url)) {
                try {
                    HttpGet getRequest = new HttpGet(url);
                    getRequest.setHeader("Accept-Language", "zh-cn");
                    getRequest.setHeader("Accept-Encoding", "gzip, deflate");
                    getRequest.setHeader("Connection", "Keep-Alive");
                    getRequest.setHeader("Referer", url);

                    HttpResponse httpResponse = httpClient.execute(getRequest);

                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = httpResponse.getEntity();
                        File localCacheFile = new File(getLocalPath(url));

                        if (Util.putFileContent(localCacheFile, entity.getContent())) {
                            Header contentType = httpResponse.getFirstHeader("Content-Type");
                            boolean isCached = database.markAsCached(url,
                                localCacheFile.getAbsolutePath(),
                                contentType.getValue(),
                                statusCode,
                                DEFAULT_WIDTH,
                                DEFAULT_HEIGHT);

                            if (isCached) {
                                publishProgress(i);
                                downloaded++;
                            }
                        }
                    } else {
                        database.markAsCached(url, null, null, statusCode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                        getRequest.abort();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }

        return downloaded;
    }

    private String getLocalPath(String url) {
        long timeStamp = System.currentTimeMillis();
        File cacheDir = database.getLocalCacheDirectory();
        return cacheDir + File.separator + timeStamp;
    }

    @Override
    protected void onPreExecute() {
        notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.downloading_offline_image))
            .setSmallIcon(R.drawable.ic_launcher);

        if (urls.size() > 0) {
            notificationCompat.setProgress(urls.size(), 0, false);
            notificationManager.notify(DOWNLOAD_NOTIFY_ID, notificationCompat.build());
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        for (Integer value : values) {
            notificationCompat.setProgress(urls.size(), value, false);
            notificationManager.notify(DOWNLOAD_NOTIFY_ID, notificationCompat.build());
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        notificationCompat.setContentTitle(context.getString(R.string.app_name))
            .setContentText(String.format(context.getString(R.string.downloading_offline_image_complete), result))
            .setSmallIcon(R.drawable.ic_launcher);

        if (result != 0) {
            notificationCompat.setProgress(0, 0, false);
            notificationManager.notify(DOWNLOAD_NOTIFY_ID, notificationCompat.build());
        } else {
            notificationManager.cancel(DOWNLOAD_NOTIFY_ID);
        }
    }

    @Override
    protected void onCancelled() {

    }
}
