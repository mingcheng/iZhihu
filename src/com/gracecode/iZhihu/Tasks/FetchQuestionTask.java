package com.gracecode.iZhihu.Tasks;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Dao.ThumbnailsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class FetchQuestionTask extends BaseTasks<Boolean, String, Integer> {
    private final static int MAX_FETCH_TIMES = 3600 * 1000 * 2; // 2 hours
    private final static String TAG = FetchQuestionTask.class.getName();
    private static ThumbnailsDatabase fetchThumbnailsDatabase;
    private boolean isNeedCacheThumbnails = true;

    public FetchQuestionTask(Context context, Callback callback) {
        super(context, callback);
        fetchThumbnailsDatabase = new ThumbnailsDatabase(context);
        isNeedCacheThumbnails = sharedPreferences.getBoolean(context.getString(R.string.key_enable_cache), true);
    }

    @Override
    protected Integer doInBackground(Boolean... booleans) {
        int affectedRows = 0;
        String heap = "";
        Boolean isFresh = System.currentTimeMillis() - HTTPRequester.getLastRequestTimeStamp() < MAX_FETCH_TIMES;

        try {
            for (Boolean focus : booleans) {
                if (!focus && isFresh) {
                    return affectedRows;
                }

                // Fetch new data from server.
                JSONArray fetchedData = HTTPRequester.fetch(questionsDatabase.getStartId());

                for (int i = 0, length = fetchedData.length(); i < length; i++) {
                    JSONObject item = (JSONObject) fetchedData.get(i);
                    if (questionsDatabase.insertSingleQuestion(item) >= 1) {
                        affectedRows++;

                        // Add heap for thumbnail use.
                        heap += item.getString(QuestionsDatabase.COLUM_CONTENT) +
                                item.getString(QuestionsDatabase.COLUM_QUESTION_DESCRIPTION);
                    }
                }

                if (isNeedCacheThumbnails) {
                    List<String> needCachedUrls = Util.getImageUrls(heap);
                    for (String url : needCachedUrls) {
                        if (!fetchThumbnailsDatabase.add(url)) {
                            Log.i(TAG, "Cant add image " + url + " into cache request queue.");
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        } catch (NetworkErrorException e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            publishProgress(e.getLocalizedMessage());
        }

        return affectedRows;
    }

    @Override
    protected void onProgressUpdate(String... messages) {
        for (String message : messages) {
            Util.showLongToast(context, message);
        }
    }
}
