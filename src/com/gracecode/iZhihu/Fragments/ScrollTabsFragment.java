package com.gracecode.iZhihu.Fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
    private ListPagerAdapter listAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pages, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }


    private void rebuildTables() throws RuntimeException {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.removeAllTabs();

        for (int icon : new int[]{R.drawable.ic_tab_list, R.drawable.ic_tab_star}) {
            ActionBar.Tab tab = actionBar.newTab()
                    //.setText(tabTitle)
                    .setIcon(icon)
                    .setTabListener(new MainTabListener(context, viewPager));

            actionBar.addTab(tab);
        }
    }

    @Override
    public void onResume() {
        int selectIndex = sharedPref.getInt(KEY_CURRENT_TAB, ListPagerAdapter.FIRST_TAB);
        actionBar.setSelectedNavigationItem(selectIndex);
        viewPager.setCurrentItem(selectIndex);

        notifyDatasetChanged();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        savePref(KEY_CURRENT_TAB, actionBar.getSelectedNavigationIndex());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.activity = getActivity();
        this.context = activity.getApplicationContext();
        this.actionBar = activity.getActionBar();
        this.sharedPref = context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        this.listAdapter = new ListPagerAdapter(activity);

        viewPager.setAdapter(listAdapter);
        viewPager.setOnPageChangeListener(listAdapter);

        rebuildTables();
    }

    public void notifyDatasetChanged() {
        // @see http://stackoverflow.com/questions/7700226/display-fragment-viewpager-within-a-fragment
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    void savePref(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
