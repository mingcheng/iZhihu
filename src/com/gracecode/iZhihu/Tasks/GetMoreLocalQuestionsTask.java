package com.gracecode.iZhihu.Tasks;

import android.content.Context;
import android.database.Cursor;

public class GetMoreLocalQuestionsTask extends BaseTasks<Integer, Void, Cursor> {

    public GetMoreLocalQuestionsTask(Context context, Callback callback) {
        super(context, callback);
    }

    @Override
    protected Cursor doInBackground(Integer... pages) {
        for (Integer page : pages) {
            return database.getRecentQuestions(page);
        }

        return null;
    }
}
