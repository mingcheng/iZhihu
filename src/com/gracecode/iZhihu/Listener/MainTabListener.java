package com.gracecode.iZhihu.Listener;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public final class MainTabListener implements ActionBar.TabListener {
    private final Context context;
    private final String className;
    public Fragment fragment;

    public MainTabListener(Context context, String className) {
        this.context = context;
        this.className = className;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragment = Fragment.instantiate(context, className);
        fragmentTransaction.add(android.R.id.content, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
            fragment = null;
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
