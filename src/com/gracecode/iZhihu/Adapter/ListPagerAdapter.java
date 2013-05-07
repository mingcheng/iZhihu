package com.gracecode.iZhihu.Adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.gracecode.iZhihu.Fragments.QuestionsListFragment;
import com.gracecode.iZhihu.Fragments.StaredListFragment;

public class ListPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
    public static final String[] TAB_CLASSES =
        {QuestionsListFragment.class.getName(), StaredListFragment.class.getName()};

    public static final int FIRST_TAB = 0;
    public static final int SECOND_TAB = 1;
    private final Activity activity;
    private final ActionBar actionBar;

    public ListPagerAdapter(Activity activity) {
        super(activity.getFragmentManager());

        this.activity = activity;
        this.actionBar = activity.getActionBar();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case SECOND_TAB:
                return Fragment.instantiate(activity, TAB_CLASSES[SECOND_TAB]);
        }

        return Fragment.instantiate(activity, TAB_CLASSES[FIRST_TAB]);
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
    public void onPageScrollStateChanged(int i) {
    }
}
