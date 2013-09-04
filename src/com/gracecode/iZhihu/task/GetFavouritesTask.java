package com.gracecode.iZhihu.task;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
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

public class GetFavouritesTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = GetFavouritesTask.class.getName();
    private static final int NOTIFY_ID = 0xff00ff;

    private final Requester mHttpRequester;
    private final QuestionsDatabase mQuestionsDatabase;
    private final Context mContext;
    private String mErrorMessage;
    private ProgressDialog mProgressDialog;

    public GetFavouritesTask(Context context) {
        mContext = context;
        mHttpRequester = new Requester(context);
        mQuestionsDatabase = new QuestionsDatabase(context);
    }

    private int markStaredQuestions(List<String> ids) {
        int result = 0;
        for (String id : ids) {
            try {
                int i = Integer.parseInt(id);
                if (i > 0) {
                    if (mQuestionsDatabase.markQuestionAsStared(i, true) > 0) {
                        mQuestionsDatabase.markAsRead(i);
                        result++;
                    }
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(
                mContext,
                mContext.getString(R.string.app_name), mContext.getString(R.string.loading), false, false);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            JSONObject jsonObject = mHttpRequester.getFavourites();

            if (jsonObject == null) {
                return 0;
            }

            String favourites = jsonObject.getString("favourites").trim();
            if (favourites.isEmpty()) {
                return 0;
            }

            // @see http://stackoverflow.com/questions/7347856/how-to-convert-a-string-into-an-arraylist
            return markStaredQuestions(Arrays.asList(favourites.split("\\s*,\\s*")));
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            mErrorMessage = e.getMessage();
            return 0;
        } catch (NetworkErrorException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            mErrorMessage = e.getMessage();
            return 0;
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            mErrorMessage = e.getMessage();
            return 0;
        } catch (NullPointerException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
            mErrorMessage = e.getMessage();
            return 0;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        mProgressDialog.dismiss();

        String message = (result > 0) ?
                String.format(mContext.getString(R.string.syncd_message), result) : mContext.getString(R.string.get_favourites_faild);

        if (mErrorMessage != null && !mErrorMessage.isEmpty()) {
            message = mErrorMessage;
        }

        Helper.showShortToast(mContext, message);
    }
}
