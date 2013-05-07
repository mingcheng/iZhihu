package com.gracecode.iZhihu.Adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-7
 */
public class ListPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
    private final Activity activity;
    private final ActionBar actionBar;
    List<Fragment> fragments;

    public ListPagerAdapter(Activity activity, List<Fragment> fragments) {
        super(activity.getFragmentManager());

        this.activity = activity;
        this.actionBar = activity.getActionBar();
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        actionBar.setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
