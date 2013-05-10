package com.gracecode.iZhihu.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Fragments.ScrollTabsFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Service.FetchThumbnailsService;
import com.gracecode.iZhihu.Tasks.FetchQuestionTask;

public class Main extends BaseActivity {
    private ScrollTabsFragment scrollTabsFragment;
    private Intent fetchThumbnailsServiceIntent;

    /**
     * 判断是否第一次启动
     *
     * @return
     */
    private boolean isFirstRun() {
        Boolean isFirstrun = sharedPreferences.getBoolean(getString(R.string.app_name), true);
        if (isFirstrun) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.app_name), false);
            editor.commit();
        }
        return isFirstrun;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrollTabsFragment = new ScrollTabsFragment();
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, scrollTabsFragment)
            .commit();

        fetchThumbnailsServiceIntent = new Intent(this, FetchThumbnailsService.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (true) {
            startService(fetchThumbnailsServiceIntent);
        }
        fetchQuestionsFromServer(isFirstRun() ? true : false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void fetchQuestionsFromServer(final Boolean focus) {
        FetchQuestionTask task = new FetchQuestionTask(context, new FetchQuestionTask.Callback() {
            private ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                if (focus) {
                    progressDialog = ProgressDialog.show(Main.this,
                        getString(R.string.app_name), getString(R.string.loading), false, false);
                }
            }

            @Override
            public void onPostExecute(Object affectedRows) {
                int i = (Integer) affectedRows;
                try {
                    if (focus && i > 0) {
                        Toast.makeText(context,
                            String.format(getString(R.string.affectRows), i), Toast.LENGTH_LONG).show();
                    }
                    scrollTabsFragment.notifyDatasetChanged();
                } catch (RuntimeException e) {
                    Toast.makeText(context, getString(R.string.rebuild_ui_faild), Toast.LENGTH_LONG).show();
                } finally {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (true && i > 0) {
                        startService(fetchThumbnailsServiceIntent);
                    }
                }
            }

        });

        task.execute(focus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                fetchQuestionsFromServer(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
