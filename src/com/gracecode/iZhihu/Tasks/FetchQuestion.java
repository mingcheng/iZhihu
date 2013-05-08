package com.gracecode.iZhihu.Tasks;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import com.gracecode.iZhihu.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchQuestion extends BaseTasks<Boolean, Void, Integer> {
    private final static int MAX_FETCH_TIMES = 3600 * 1000 * 2; // 2 hours

    public FetchQuestion(Context context, Callback callback) {
        super(context, callback);
    }

    @Override
    protected Integer doInBackground(Boolean... booleans) {
        int affectedRows = 0;

        try {
            for (Boolean focus : booleans) {
                if (!focus && System.currentTimeMillis() - requester.getLastRequestTimeStamp() < MAX_FETCH_TIMES) {
                    return null;
                }

                int startId = questionsDatabase.getStartId();
                JSONArray fetchedData = requester.fetch(startId);

                for (int i = 0, length = fetchedData.length(); i < length; i++) {
                    JSONObject item = (JSONObject) fetchedData.get(i);
                    if (questionsDatabase.insertSingleQuestion(item) >= 1) {
                        affectedRows++;
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
