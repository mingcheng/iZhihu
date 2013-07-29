package com.gracecode.iZhihu.task;

import android.content.Context;
import android.os.AsyncTask;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.db.QuestionsDatabase;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-7-24
 */
public class SearchQuestionTask extends AsyncTask<String, Void, ArrayList<Question>> {
    private final QuestionsDatabase questionDatabase;
    private final Callback callback;

    public interface Callback {
        public void onPreExecute();

        public void onPostExecute(ArrayList<Question> result);
    }


    public SearchQuestionTask(Context context, Callback callback) {
        this.questionDatabase = new QuestionsDatabase(context);
        this.callback = callback;
    }

    protected void onPreExecute() {
        callback.onPreExecute();
    }

    protected void onPostExecute(ArrayList<Question> result) {
        callback.onPostExecute(result);
    }

    @Override
    protected ArrayList<Question> doInBackground(String... keys) {
        ArrayList<Question> result = new ArrayList<>();
        for (String key : keys) {
            ArrayList<Question> q = questionDatabase.searchQuestions(key);
            result.addAll(q);
        }

        return result;
    }
}
