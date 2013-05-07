package com.gracecode.iZhihu.Listener;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.view.ViewPager;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public final class MainTabListener implements ActionBar.TabListener {
    private final Context context;
    private final ViewPager viewPager;

    public MainTabListener(Context context, ViewPager viewPager) {
        this.context = context;
        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        if (alreadyAdded) {
//            fragmentTransaction.show(fragment);
//            return;
//        } else {
//            fragmentTransaction.add(android.R.id.content, fragment);
//            alreadyAdded = true;
//        }

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        fragmentTransaction.hide(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
