package com.gracecode.iZhihu.Tasks;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Dao.Requester;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchQuestionTask extends AsyncTask<Boolean, Void, Void> {
    private final static int MAX_FETCH_TIMES = 3600 * 1000 * 2; // 2 hours
    private final Callback callback;
    private final Requester requester;
    private final Database database;
    Context context;

    public abstract interface Callback {
        public void onPostExecute();

        public void onPreExecute();
    }

    public FetchQuestionTask(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.requester = new Requester(context);
        this.database = new Database(context);
    }

    @Override
    protected Void doInBackground(Boolean... focusRefresh) {
        Boolean focus = focusRefresh[0];
        if (!focus && System.currentTimeMillis() - requester.getLastRequestTimeStamp() < MAX_FETCH_TIMES) {
            Log.i(getClass().getName(), "Already fetched at recently, so ignore.");
            return null;
        }

        try {
            //Thread.sleep(2000);

            JSONArray fetchedData = requester.fetch();
            for (int i = 0, length = fetchedData.length(); i < length; i++) {
                JSONObject item = (JSONObject) fetchedData.get(i);
                database.insertSingleQuestion(item);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NetworkErrorException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPreExecute() {
        callback.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void voids) {
        callback.onPostExecute();
    }
}
