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
    private boolean alreadyAdded = false;
    public Fragment fragment;

    public MainTabListener(Context context, String className) {
        this.context = context;
        this.className = className;
        this.fragment = Fragment.instantiate(context, className);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (alreadyAdded) {
            fragmentTransaction.show(fragment);
            return;
        } else {
            fragmentTransaction.add(android.R.id.content, fragment);
            alreadyAdded = true;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.hide(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
