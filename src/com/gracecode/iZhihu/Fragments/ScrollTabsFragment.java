package com.gracecode.iZhihu.Fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gracecode.iZhihu.Adapter.ListPagerAdapter;
import com.gracecode.iZhihu.Listener.MainTabListener;
import com.gracecode.iZhihu.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-7
 */
public class ScrollTabsFragment extends Fragment {
    private Activity activity;
    private Context context;
    private SharedPreferences sharedPref;
    private ActionBar actionBar;
    private ActionBar.Tab mainTab;
    private ActionBar.Tab favoritesTab;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pages, container, false);
        this.viewPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }

    private void rebuildTables() throws RuntimeException {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.removeAllTabs();
        actionBar.addTab(mainTab);
        actionBar.addTab(favoritesTab);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.activity = getActivity();
        this.context = activity.getApplicationContext();
        this.actionBar = activity.getActionBar();
        this.sharedPref = context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        Fragment questionsList = new QuestionsListFragment();
        Fragment staredList = new StaredListFragment();

        this.mainTab = actionBar.newTab()
            .setText(getString(R.string.tab_index))
            .setTabListener(new MainTabListener(context, viewPager));

        this.favoritesTab = actionBar.newTab()
            .setText(getString(R.string.tab_favorite))
            .setTabListener(new MainTabListener(context, viewPager));

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(questionsList);
        fragments.add(staredList);

        rebuildTables();

        ListPagerAdapter listPagerAdapter = new ListPagerAdapter(activity, fragments);
        viewPager.setAdapter(listPagerAdapter);
        viewPager.setOnPageChangeListener(listPagerAdapter);
    }
}
