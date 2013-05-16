package com.gracecode.iZhihu.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Fragments.BaseListFragment;
import com.gracecode.iZhihu.Fragments.ScrollTabsFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Service.FetchThumbnailsService;
import com.gracecode.iZhihu.Tasks.FetchQuestionTask;
import com.gracecode.iZhihu.Util;

public class Main extends BaseActivity {
    private ScrollTabsFragment scrollTabsFragment;
    private Intent fetchThumbnailsServiceIntent;
    private boolean isNeedCacheThumbnails = true;

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

        isNeedCacheThumbnails = sharedPreferences.getBoolean(getString(R.string.key_enable_cache), true);
        if (isNeedCacheThumbnails) {
            startService(fetchThumbnailsServiceIntent);
        }

        fetchQuestionsFromServer(isFirstRun());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    void fetchQuestionsFromServer(final Boolean focus) {
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
                int i = 0;
                try {
                    i = (Integer) affectedRows;
                    if (focus && i > 0) {
                        Toast.makeText(context,
                                String.format(getString(R.string.affectRows), i), Toast.LENGTH_LONG).show();
                        Util.savePref(sharedPreferences,
                                BaseListFragment.KEY_SELECTED_POSITION, BaseListFragment.SELECT_NONE);
                    }
                    scrollTabsFragment.notifyDatasetChanged();
                } catch (RuntimeException e) {
                    Util.showShortToast(context, getString(R.string.rebuild_ui_faild));
                } finally {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (isNeedCacheThumbnails && i > 0 && Util.isExternalStorageExists()) {
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
