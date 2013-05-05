package com.gracecode.iZhihu.Activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.gracecode.iZhihu.Fragments.QuestionsListFragment;
import com.gracecode.iZhihu.Fragments.StaredListFragment;
import com.gracecode.iZhihu.Listener.MainTabListener;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Tasks.FetchQuestion;

public class Main extends BaseActivity {

    private boolean isFirstRun() {
        Boolean isFirstrun = sharedPreferences.getBoolean(getString(R.string.app_name), true);
        if (isFirstrun) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.app_name), false);
            editor.commit();
        }
        return isFirstrun;
    }

    private void rebuildTables() throws RuntimeException {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.removeAllTabs();

        ActionBar.Tab mainTab = actionBar.newTab()
            .setText(getString(R.string.tab_index))
            .setTabListener(new MainTabListener(context, QuestionsListFragment.class.getName()));

        ActionBar.Tab favoritesTab = actionBar.newTab()
            .setText(getString(R.string.tab_favorite))
            .setTabListener(new MainTabListener(context, StaredListFragment.class.getName()));

        actionBar.addTab(mainTab);
        actionBar.addTab(favoritesTab);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchQuestionsFromServer(isFirstRun() ? true : false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void fetchQuestionsFromServer(final Boolean focus) {
        FetchQuestion task = new FetchQuestion(context, new FetchQuestion.Callback() {
            private ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                if (focus) {
                    progressDialog = ProgressDialog.show(Main.this,
                        getString(R.string.app_name), getString(R.string.loading), false, false);
                }
            }

            @Override
            public void onPostExecute(Object o) {
                try {
                    rebuildTables();
                } catch (RuntimeException e) {

                } finally {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
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
