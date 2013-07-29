package com.gracecode.iZhihu.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.gracecode.iZhihu.api.Requester;
import com.gracecode.iZhihu.db.QuestionsDatabase;

abstract class BaseTasks<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected final Requester Requester;
    protected final QuestionsDatabase questionsDatabase;
    protected final Context context;
    protected final Callback callback;
    protected final SharedPreferences sharedPreferences;

    public abstract interface Callback {
        public abstract void onPostExecute(Object result);

        public abstract void onPreExecute();
    }

    public BaseTasks(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.Requester = new Requester(context);
        this.questionsDatabase = new QuestionsDatabase(context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        callback.onPreExecute();
    }

    @Override
    protected void onPostExecute(Result result) {
        callback.onPostExecute(result);
    }
}
