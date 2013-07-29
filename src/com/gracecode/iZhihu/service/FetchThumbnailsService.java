package com.gracecode.iZhihu.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.gracecode.iZhihu.db.ThumbnailsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.task.FetchThumbnailTask;
import com.gracecode.iZhihu.util.Helper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-10
 */
public class FetchThumbnailsService extends Service {
    private static ThumbnailsDatabase database = null;
    private Context context;
    private SharedPreferences sharedPreferences;

    public FetchThumbnailsService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        this.database = new ThumbnailsDatabase(context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<String> notCachedUrls = database.getNotCachedThumbnails();

        if (notCachedUrls.size() > 0) {
            Boolean isNeedWifiToDownload = sharedPreferences.getBoolean(getString(R.string.key_only_wifi_cache), true);

            // @todo 使用 handle 控制线程
            FetchThumbnailTask fetchThumbnailTask =
                    new FetchThumbnailTask(this, database, notCachedUrls, new FetchThumbnailTask.Callback() {
                        @Override
                        public void onPostExecute(Object result) {
                            stopSelf();
                        }

                        @Override
                        public void onPreExecute() {

                        }
                    });

            boolean isExternalStorageExists = Helper.isExternalStorageExists();
            if (isExternalStorageExists) {
                if (isNeedWifiToDownload) {
                    if (Helper.isWifiConnected(context)) {
                        fetchThumbnailTask.execute();
                    } else {
                        Helper.showShortToast(context, getString(R.string.download_when_wifi_avaiable));
                        stopSelf();
                    }
                } else {
                    fetchThumbnailTask.execute();
                }
            } else {
                stopSelf();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
