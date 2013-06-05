package com.gracecode.iZhihu.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.gracecode.iZhihu.Fragments.ScrollTabsFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Service.FetchThumbnailsService;
import com.gracecode.iZhihu.Tasks.FetchQuestionTask;
import com.gracecode.iZhihu.Util;

public class Main extends BaseActivity {
    private ScrollTabsFragment scrollTabsFragment;
    private Intent fetchThumbnailsServiceIntent;
    private final boolean isNeedCacheThumbnails = true;
    private MenuItem menuRefersh = null;

    /**
     * 判断是否第一次启动
     *
     * @return Boolean
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
        fetchThumbnailsServiceIntent = new Intent(this, FetchThumbnailsService.class);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, scrollTabsFragment)
                .commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        fetchQuestionsFromServer(isFirstRun());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menuRefersh = menu.findItem(R.id.menu_refresh);
        return true;
    }


    /**
     * 从服务器获取条目
     *
     * @param focus 强制刷新
     */
    void fetchQuestionsFromServer(final Boolean focus) {
        FetchQuestionTask task = new FetchQuestionTask(context, new FetchQuestionTask.Callback() {
            private ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                if (focus) {
                    progressDialog = ProgressDialog.show(Main.this,
                            getString(R.string.app_name), getString(R.string.loading), false, false);
                }

                if (menuRefersh != null) {
                    Animation rotation = AnimationUtils.loadAnimation(context, R.anim.refresh_rotate);

                    ImageView v = new ImageView(context);
                    v.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_refersh));
                    v.startAnimation(rotation);

                    menuRefersh.setActionView(v);
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
                    }
                    scrollTabsFragment.notifyDatasetChanged();
                } catch (RuntimeException e) {
                    Util.showShortToast(context, getString(R.string.rebuild_ui_faild));
                } finally {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    // 离线下载图片
                    if (Util.isExternalStorageExists() && isNeedCacheThumbnails && i > 0) {
                        startService(fetchThumbnailsServiceIntent);
                    } else {
                        stopService(fetchThumbnailsServiceIntent);
                    }

                    if (menuRefersh != null) {
                        menuRefersh.getActionView().clearAnimation();
                        menuRefersh.setActionView(null);
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
                fetchQuestionsFromServer(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
