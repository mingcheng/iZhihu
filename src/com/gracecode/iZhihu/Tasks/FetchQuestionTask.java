package com.gracecode.iZhihu.Tasks;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Dao.ThumbnailsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class FetchQuestionTask extends BaseTasks<Boolean, Void, Integer> {
    private final static int MAX_FETCH_TIMES = 3600 * 1000 * 2; // 2 hours
    private final static String TAG = FetchQuestionTask.class.getName();
    private static ThumbnailsDatabase fetchThumbnailsDatabase;

    public FetchQuestionTask(Context context, Callback callback) {
        super(context, callback);
        fetchThumbnailsDatabase = new ThumbnailsDatabase(context);
    }

    @Override
    protected Integer doInBackground(Boolean... booleans) {
        String heap = "";
        int affectedRows = 0;

        try {
            for (Boolean focus : booleans) {
                if (!focus && System.currentTimeMillis() - HTTPRequester.getLastRequestTimeStamp() < MAX_FETCH_TIMES) {
                    return null;
                }

                int startId = questionsDatabase.getStartId();
                JSONArray fetchedData = HTTPRequester.fetch(startId);

                for (int i = 0, length = fetchedData.length(); i < length; i++) {
                    JSONObject item = (JSONObject) fetchedData.get(i);

                    synchronized (questionsDatabase) {
                        if (questionsDatabase.insertSingleQuestion(item) >= 1) {
                            affectedRows++;

                            heap += item.getString(QuestionsDatabase.COLUM_CONTENT) +
                                item.getString(QuestionsDatabase.COLUM_QUESTION_DESCRIPTION);
                        }
                    }
                }

                // @todo 增加配置判断
                if (true) {
                    List<String> needCachedUrls = Util.getImageUrls(heap);
                    for (String url : needCachedUrls) {
                        synchronized (fetchThumbnailsDatabase) {
                            if (!fetchThumbnailsDatabase.add(url)) {
                                Log.i(TAG, "Cant add image " + url + " into cache request queue.");
                            }
                        }
                    }
                }
            }

            return affectedRows;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NetworkErrorException e) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
        } finally {
            return affectedRows;
        }
    }
}
