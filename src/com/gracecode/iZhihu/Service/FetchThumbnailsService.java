package com.gracecode.iZhihu.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.gracecode.iZhihu.Dao.ThumbnailsDatabase;
import com.gracecode.iZhihu.Tasks.FetchThumbnailTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-10
 */
public class FetchThumbnailsService extends Service {
    private static ThumbnailsDatabase database = null;

    public FetchThumbnailsService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.database = new ThumbnailsDatabase(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<String> notCachedUrls = database.getNotCachedThumbnails();

        // @todo 使用 handle 控制线程
        FetchThumbnailTask fetchThumbnailTask = new FetchThumbnailTask(getApplicationContext(), database, notCachedUrls);
        fetchThumbnailTask.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (database != null) {
            database.close();
            database = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
