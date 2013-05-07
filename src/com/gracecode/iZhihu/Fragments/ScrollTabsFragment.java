package com.gracecode.iZhihu.Fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gracecode.iZhihu.Adapter.ListPagerAdapter;
import com.gracecode.iZhihu.Listener.MainTabListener;
import com.gracecode.iZhihu.R;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-7
 */
public class ScrollTabsFragment extends Fragment {
    private static final String KEY_CURRENT_TAB = "KEY_CURRENT_TAB";
    private Activity activity;
    private Context context;
    private SharedPreferences sharedPref;
    private ActionBar actionBar;
    private static ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pages, container, false);
        this.viewPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }


    private void rebuildTables() throws RuntimeException {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.removeAllTabs();

        for (String tabTitle : new String[]{getString(R.string.tab_index), getString(R.string.tab_stared)}) {
            ActionBar.Tab tab = actionBar.newTab()
                .setText(tabTitle)
                .setTabListener(new MainTabListener(context, viewPager));

            actionBar.addTab(tab);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new setAdapterTask().execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        savePref(KEY_CURRENT_TAB, actionBar.getSelectedNavigationIndex());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.activity = getActivity();
        this.context = activity.getApplicationContext();
        this.actionBar = activity.getActionBar();
        this.sharedPref = context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        rebuildTables();
    }

    public void notifyDatasetChanged() {
        new setAdapterTask().execute();
    }

    public boolean savePref(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListPagerAdapter adapter = new ListPagerAdapter(activity);
            viewPager.setAdapter(adapter);
            viewPager.setOnPageChangeListener(adapter);

            int selectIndex = sharedPref.getInt(KEY_CURRENT_TAB, ListPagerAdapter.FIRST_TAB);
            actionBar.setSelectedNavigationItem(selectIndex);
            viewPager.setCurrentItem(selectIndex);
        }
    }
}
