package com.gracecode.iZhihu.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.gracecode.iZhihu.Fragments.DetailFragment;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-15
 */
public class DetailListsAdapter extends FragmentStatePagerAdapter {
    private final List<Integer> questionsIds;
    private final Activity activity;
    private HashMap<Integer, Fragment> questionFragments;

    public DetailListsAdapter(Activity activity, List<Integer> questionsIds) {
        super(activity.getFragmentManager());

        this.activity = activity;
        this.questionsIds = questionsIds;
        this.questionFragments = new HashMap<Integer, Fragment>();
    }

    @Override
    public Fragment getItem(int i) {
        int id = questionsIds.get(i);
        if (questionFragments.containsKey(id)) {
            return questionFragments.get(id);
        }

        Fragment fragment = new DetailFragment(id, activity);
        questionFragments.put(id, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return questionsIds.size();
    }
}
