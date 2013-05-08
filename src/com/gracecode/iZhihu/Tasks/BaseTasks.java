package com.gracecode.iZhihu.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Dao.Requester;

abstract class BaseTasks<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected final Requester requester;
    protected final QuestionsDatabase questionsDatabase;
    protected final Context context;
    protected final Callback callback;

    public abstract interface Callback {
        public abstract void onPostExecute(Object result);

        public abstract void onPreExecute();
    }

    public BaseTasks(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.requester = new Requester(context);
        this.questionsDatabase = new QuestionsDatabase(context);
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
