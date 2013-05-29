package com.gracecode.iZhihu.Adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import com.gracecode.iZhihu.Fragments.QuestionsListFragment;
import com.gracecode.iZhihu.Fragments.StaredListFragment;

import java.util.ArrayList;

public class ListPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
    private static final String[] TAB_CLASSES =
            {QuestionsListFragment.class.getName(), StaredListFragment.class.getName()};

    public static final int FIRST_TAB = 0;
    private static final int SECOND_TAB = 1;
    private final ActionBar actionBar;
    private final ArrayList<Fragment> fragments;

    public ListPagerAdapter(Activity activity) {
        super(activity.getFragmentManager());

        this.actionBar = activity.getActionBar();
        this.fragments = new ArrayList<>();

        fragments.add(FIRST_TAB, Fragment.instantiate(activity, TAB_CLASSES[FIRST_TAB]));
        fragments.add(SECOND_TAB, Fragment.instantiate(activity, TAB_CLASSES[SECOND_TAB]));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, 0, object);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return TAB_CLASSES.length;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        actionBar.setSelectedNavigationItem(i);
    }

    @Override
    public void notifyDataSetChanged() {
        try {
            super.notifyDataSetChanged();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
