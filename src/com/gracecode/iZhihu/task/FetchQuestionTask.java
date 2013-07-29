package com.gracecode.iZhihu.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.gracecode.iZhihu.util.Requester;
import com.gracecode.iZhihu.databases.QuestionsDatabase;
import com.gracecode.iZhihu.databases.ThumbnailsDatabase;
import com.gracecode.iZhihu.service.FetchThumbnailsService;
import com.gracecode.iZhihu.util.Helper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class FetchQuestionTask {
    private final static int MAX_FETCH_TIMES = 3600 * 1000 * 2; // 2 hours
    private final static String TAG = FetchQuestionTask.class.getName();
    private static ThumbnailsDatabase fetchThumbnailsDatabase;
    private final Requester requester;
    private final QuestionsDatabase questionsDatabase;
    private final Callback callback;
    private final Context context;
    private boolean focus = false;
    private boolean isNeedCacheThumbnails = true;
    private String errorMessage = "";
    private int affectedRows = 0;

    public interface Callback {
        public abstract void onFinished();
    }

    private Runnable fetcher = new Runnable() {
        private boolean isRunning = false;

        @Override
        public void run() {
            // Fetch new data from server.
            try {
                if (isRunning) {
                    return;
                }
                isRunning = true;

                if (isFresh() && !focus) {
                    return;
                }

                String heap = "";
                affectedRows = 0;
                JSONArray fetchedData = requester.fetch(questionsDatabase.getStartId());

                for (int i = 0, length = fetchedData.length(); i < length; i++) {
                    JSONObject item = (JSONObject) fetchedData.get(i);
                    if (questionsDatabase.insertSingleQuestion(item) >= 1) {
                        affectedRows++;

                        // Add heap for thumbnail use.
                        heap += (item.getString(QuestionsDatabase.COLUM_CONTENT) +
                                item.getString(QuestionsDatabase.COLUM_QUESTION_DESCRIPTION));
                    }
                }

                // Add url into database and mark.
                List<String> needCachedUrls = Helper.getImageUrls(heap);
                for (String url : needCachedUrls) {
                    if (!fetchThumbnailsDatabase.add(url)) {
                        Log.e(TAG, "Cant add image " + url + " into cache request queue.");
                    }
                }

                // 离线下载图片
                Intent fetchThumbnailsServiceIntent = new Intent(context, FetchThumbnailsService.class);
                if (isNeedCacheThumbnails() && getAffectedRows() > 0
                        && Helper.isWifiConnected(context)
                        && Helper.isExternalStorageExists()) {
                    context.startService(fetchThumbnailsServiceIntent);
                } else {
                    context.stopService(fetchThumbnailsServiceIntent);
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
            } finally {
                // Call then request is finished.
                callback.onFinished();
                isRunning = false;
            }
        }
    };

    public FetchQuestionTask(Context context, Callback callback) {
        this.context = context;
        this.fetchThumbnailsDatabase = new ThumbnailsDatabase(context);
        this.questionsDatabase = new QuestionsDatabase(context);
        this.requester = new Requester(context);
        this.callback = callback;
    }

    public void start() {
        this.start(false);
    }

    public void start(boolean f) {
        this.focus = f;
        new Thread(fetcher).start();
    }

//    public void cancel() {
//
//    }

    public boolean isNeedCacheThumbnails() {
        return isNeedCacheThumbnails;
    }

    public boolean isFresh() {
        return (System.currentTimeMillis() - requester.getLastRequestTimeStamp()) < MAX_FETCH_TIMES;
    }

    public void setIsNeedCacheThumbnails(boolean needs) {
        isNeedCacheThumbnails = needs;
    }

    public boolean hasError() {
        return (errorMessage != null && errorMessage.length() > 0) ? true : false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getAffectedRows() {
        return affectedRows;
    }
}
