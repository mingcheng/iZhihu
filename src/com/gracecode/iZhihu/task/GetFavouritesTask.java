package com.gracecode.iZhihu.task;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.gracecode.iZhihu.BuildConfig;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.api.Requester;
import com.gracecode.iZhihu.db.QuestionsDatabase;
import com.gracecode.iZhihu.util.Helper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GetFavouritesTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = GetFavouritesTask.class.getName();

    private final Requester mHttpRequester;
    private final QuestionsDatabase mQuestionsDatabase;
    private final Context mContext;

    public GetFavouritesTask(Context context) {
        mContext = context;
        mHttpRequester = new Requester(context);
        mQuestionsDatabase = new QuestionsDatabase(context);
    }

    private void markStaredQuestions(List<String> ids) {
        for (String id : ids) {
            int i = Integer.parseInt(id);
            if (i > 0) {
                mQuestionsDatabase.markQuestionAsStared(i, true);
            }
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            JSONObject jsonObject = mHttpRequester.getFavourites();
            String favourites = jsonObject.getString("favourites");

            // @see http://stackoverflow.com/questions/7347856/how-to-convert-a-string-into-an-arraylist
            markStaredQuestions(Arrays.asList(favourites.split("\\s*,\\s*")));
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            return false;
        } catch (NetworkErrorException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            return false;
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            return false;
        } catch (NullPointerException e) {
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
