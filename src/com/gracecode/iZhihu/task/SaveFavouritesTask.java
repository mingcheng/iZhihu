package com.gracecode.iZhihu.task;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.gracecode.iZhihu.BuildConfig;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.api.Requester;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.db.QuestionsDatabase;
import com.gracecode.iZhihu.util.Helper;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SaveFavouritesTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = SaveFavouritesTask.class.getName();

    private final Requester mHttpRequester;
    private final QuestionsDatabase mQuestionsDatabase;
    private final Context mContext;

    public SaveFavouritesTask(Context context) {
        mContext = context;
        mHttpRequester = new Requester(context);
        mQuestionsDatabase = new QuestionsDatabase(context);
    }

    private ArrayList<Integer> getStaredQuestionsId() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Question question : mQuestionsDatabase.getStaredQuestions()) {
            result.add(question.getId());
        }

        return result;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ArrayList<Integer> ids = getStaredQuestionsId();

        try {
            String idsString = ids.toString();
            mHttpRequester.saveFavourites(idsString.substring(1, idsString.length() - 1));
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            return false;
        } catch (NetworkErrorException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            return false;
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            return;
        }

        Helper.showShortToast(mContext, mContext.getString(R.string.save_favourites_ok));
    }
}
