package com.gracecode.iZhihu.Adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import com.gracecode.iZhihu.Fragments.BaseListFragment;
import com.gracecode.iZhihu.Fragments.QuestionsListFragment;
import com.gracecode.iZhihu.Fragments.StaredListFragment;

import java.util.ArrayList;

public class ListPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
    public static final int FIRST_TAB = 0;
    public static final int SECOND_TAB = 1;

    private final ActionBar actionBar;
    private final ArrayList<BaseListFragment> fragments = new ArrayList<>();

    public ListPagerAdapter(Activity activity) {
        super(activity.getFragmentManager());
        actionBar = activity.getActionBar();

        fragments.add(FIRST_TAB, new QuestionsListFragment());
        fragments.add(SECOND_TAB, new StaredListFragment());
    }

    public BaseListFragment getBaseListFragment(int index) {
        return fragments.get(index);
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
        return fragments.size();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        // ...
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
        // ...
    }
}
