package com.gracecode.iZhihu.Tasks;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Dao.ThumbnailsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchQuestionTask extends BaseTasks<Boolean, String, ArrayList<Question>> {
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
    protected ArrayList<Question> doInBackground(Boolean... booleans) {
        String heap = "";
        Boolean isFresh = System.currentTimeMillis() - HTTPRequester.getLastRequestTimeStamp() < MAX_FETCH_TIMES;
        ArrayList<Question> questions = new ArrayList<>();

        try {
            for (Boolean focus : booleans) {
                if (!focus && isFresh) {
                    return questions;
                }

                // Fetch new data from server.
                JSONArray fetchedData = HTTPRequester.fetch(questionsDatabase.getStartId());

                for (int i = 0, length = fetchedData.length(); i < length; i++) {
                    JSONObject item = (JSONObject) fetchedData.get(i);
                    if (questionsDatabase.insertSingleQuestion(item) >= 1) {
                        // Add and return new questions.
                        int answerId = item.getInt(QuestionsDatabase.COLUM_ANSWER_ID);
                        questions.add(questionsDatabase.getSingleQuestionByAnswerId(answerId));

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

        return questions;
    }

    @Override
    protected void onProgressUpdate(String... messages) {
        for (String message : messages) {
            Util.showLongToast(context, message);
        }
    }
}
