package com.gracecode.iZhihu.Activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.gracecode.iZhihu.Adapter.ListPagerAdapter;
import com.gracecode.iZhihu.Fragments.BaseListFragment;
import com.gracecode.iZhihu.Fragments.QuestionsListFragment;
import com.gracecode.iZhihu.Fragments.ScrollTabsFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Tasks.FetchQuestionTask;
import com.gracecode.iZhihu.Util;

public class Main extends BaseActivity {
    private static final int MESSAGE_UPDATE_LOADING = 0x01;
    private static final int MESSAGE_UPDATE_COMPLETE = 0x02;
    public static final int MESSAGE_UPDATE_SHOW_RESULT = 0x03;

    private ScrollTabsFragment scrollTabsFragment;
    private MenuItem menuRefersh;
    private FetchQuestionTask fetchQuestionsTask;
    private Boolean focusRefresh = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollTabsFragment = new ScrollTabsFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, scrollTabsFragment)
                .commit();

        fetchQuestionsTask = new FetchQuestionTask(context, new FetchQuestionTask.Callback() {
            @Override
            public void onFinished() {
                UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_COMPLETE);
                if (focusRefresh) {
                    UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_SHOW_RESULT);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        UIChangedChangedHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Util.isNetworkConnected(context)) {
                    fetchQuestionsFromServer(false);
                }
            }
        }, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuRefersh = menu.findItem(R.id.menu_refresh);
        return true;
    }


    /**
     * 刷新 UI 线程集中的地方
     */
    private final android.os.Handler UIChangedChangedHandler = new android.os.Handler() {
        /**
         * 判断是否第一次启动
         *
         * @return Boolean
         */
        private boolean isFirstRun() {
            Boolean status = sharedPreferences.getBoolean(getString(R.string.app_name), true);
            if (status) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.app_name), false);
                editor.commit();
            }
            return status;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_LOADING:
                    if (isFirstRun()) {
                        progressDialog = ProgressDialog.show(
                                Main.this,
                                getString(R.string.app_name), getString(R.string.loading), false, false);
                    }

                    Animation rotation = AnimationUtils.loadAnimation(context, R.anim.refresh_rotate);
                    RelativeLayout layout = new RelativeLayout(context);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(context);
                    imageView.setLayoutParams(params);

                    layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.TOP);
                    layout.addView(imageView);

                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_refersh));
                    imageView.startAnimation(rotation);

                    if (menuRefersh != null) {
                        menuRefersh.setActionView(layout);
                    }
                    break;

                case MESSAGE_UPDATE_COMPLETE:
                    try {
                        if (fetchQuestionsTask.getAffectedRows() > 0) {
                            BaseListFragment fragment = (scrollTabsFragment.getListAdapter())
                                    .getBaseListFragment(ListPagerAdapter.FIRST_TAB);

                            if (fragment instanceof QuestionsListFragment) {
                                ((QuestionsListFragment) fragment).updateQuestionsFromDatabase();
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } finally {
                        scrollTabsFragment.notifyDatasetChanged();
                    }

                    if (menuRefersh != null) {
                        View v = menuRefersh.getActionView();
                        if (v != null) {
                            v.clearAnimation();
                        }
                        menuRefersh.setActionView(null);
                    }

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    if (fetchQuestionsTask.hasError()) {
                        Util.showShortToast(context, fetchQuestionsTask.getErrorMessage());
                    }
                    break;

                case MESSAGE_UPDATE_SHOW_RESULT:
                    if (fetchQuestionsTask != null) {
                        String message = getString(R.string.no_newer_questions);
                        if (fetchQuestionsTask.getAffectedRows() > 0) {
                            message = String.format(getString(R.string.affectRows), fetchQuestionsTask.getAffectedRows());
                        }

                        Util.showShortToast(context, message);
                    }
                    break;
            }
        }
    };


    private ProgressDialog progressDialog;

    /**
     * 从服务器获取条目
     *
     * @param focus 强制刷新
     */
    void fetchQuestionsFromServer(final Boolean focus) {
        UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_LOADING);

        Boolean isNeedCacheThumbnails = sharedPreferences.getBoolean(context.getString(R.string.key_enable_cache), true);
        fetchQuestionsTask.setIsNeedCacheThumbnails(isNeedCacheThumbnails);

        this.focusRefresh = focus;
        // Start fetch from new thread.
        fetchQuestionsTask.start(focus);
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
