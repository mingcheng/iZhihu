package com.gracecode.iZhihu.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.fragment.DetailFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-15
 */
public class DetailListsAdapter extends FragmentStatePagerAdapter {
    private final Activity activity;
    private final HashMap<Integer, Fragment> questionFragments;
    private final ArrayList<Question> questions;

    public DetailListsAdapter(Activity activity, ArrayList<Question> questions) {
        super(activity.getFragmentManager());

        this.activity = activity;
        this.questions = questions;
        this.questionFragments = new HashMap<>();
    }

    @Override
    public Fragment getItem(int i) {
        Question question = questions.get(i);
        if (questionFragments.containsKey(i)) {
            return questionFragments.get(i);
        }

        Fragment fragment = new DetailFragment(activity, question);
        questionFragments.put(i, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return questions.size();
    }
}
