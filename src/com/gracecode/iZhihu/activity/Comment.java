package com.gracecode.iZhihu.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import com.gracecode.iZhihu.fragment.CommentsFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.task.FetchCommentTask;
import com.gracecode.iZhihu.util.Helper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Comment extends BaseActivity {
    public static final int CACHE_FILE_TIME = 1000 * 60 * 60 * 12;   // 12hours
    public static final String ANSWER_ID = "answerId";
    private ProgressDialog progressDialog;
    private long answerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = ProgressDialog.show(Comment.this,
                getString(R.string.app_name), getString(R.string.loading), false, false);

        answerId = getIntent().getIntExtra(ANSWER_ID, 0);
        if (answerId == 0) {
            showErrorAndFinish("");
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.show();
        getDataAndShow(this.answerId);
    }


    /**
     * Show comment from ArrayList
     *
     * @param comments
     */
    private void showCommentsFromArray(ArrayList<com.gracecode.iZhihu.dao.Comment> comments) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new CommentsFragment(comments))
                .commit();
    }


    /**
     * @param answerId
     * @throws IOException
     * @throws JSONException
     */
    private void getDataAndShow(long answerId) {
        final File cacheFile = getCachedFile();
        if (cacheFile.exists() && System.currentTimeMillis() - cacheFile.lastModified() < CACHE_FILE_TIME) {
            try {
                String json = Helper.getFileContent(cacheFile.getAbsolutePath());
                showCommentsFromArray(convertJSON2ArrayList(json));
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAndFinish(e.getMessage());
            }
        } else {
            new FetchCommentTask(context, new FetchCommentTask.Callback() {
                @Override
                public void onPostExecute(Object result) {
                    try {
                        if (result != null) {
                            String json = result.toString();
                            showCommentsFromArray(convertJSON2ArrayList(json));

                            // Write data into cache file.
                            Helper.putFileContent(cacheFile, new ByteArrayInputStream(json.getBytes()));

                        } else {
                            showErrorAndFinish("");
                        }
                    } catch (Exception e) {
                        cacheFile.delete();
                        e.printStackTrace();
                        showErrorAndFinish(e.getMessage());
                    }
                }

                @Override
                public void onPreExecute() {
                    // ...
                }
            }).execute(answerId);
        }
    }


    private void showErrorAndFinish(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name))
                .setCancelable(false);

        builder.setMessage((message.length() > 0) ? message : getString(R.string.network_error));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Comment.this.finish();
            }
        });

        builder.show();
    }


    /**
     * @return
     */

    private File getCachedFile() {
        String hashed = Base64.encodeToString(String.valueOf(answerId).getBytes(), Base64.DEFAULT);
        return new File(getCacheDir(), hashed.trim());
    }


    /**
     * Convert JSON String into ArrayList for ListAdapter use.
     *
     * @param jsonString
     * @return
     */
    private ArrayList<com.gracecode.iZhihu.dao.Comment> convertJSON2ArrayList(String jsonString) throws JSONException {
        ArrayList<com.gracecode.iZhihu.dao.Comment> comments = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0, len = data.length(); i < len; i++) {
            JSONObject item = (JSONObject) data.get(i);
            com.gracecode.iZhihu.dao.Comment comment = new com.gracecode.iZhihu.dao.Comment();
            comment.setAuthor(item.getJSONObject("author").getString("name"));
            comment.setContent(item.getString("content"));
            comment.setId(item.getLong("id"));
            comment.setTimeStamp(item.getLong("created_time"));
            comments.add(comment);
        }

        return comments;
    }


    @Override
    public void onStop() {
        progressDialog.dismiss();
        super.onStop();
    }
}
