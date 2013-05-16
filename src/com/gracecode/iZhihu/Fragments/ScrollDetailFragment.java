package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Adapter.DetailListsAdapter;
import com.gracecode.iZhihu.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-15
 */
public class ScrollDetailFragment extends Fragment {
    private final List<Integer> questionIds;
    private final Integer selectedId;
    private final Detail detailActivity;
    private ViewPager viewPager;
    private Activity activity;
    private DetailListsAdapter adapter;

    public ScrollDetailFragment(Detail detailActivity, List<Integer> questionIds, Integer selectedId) {
        this.detailActivity = detailActivity;
        this.questionIds = questionIds;
        this.selectedId = selectedId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pages, container, false);
        this.viewPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.activity = getActivity();
        this.adapter = new DetailListsAdapter(activity, questionIds);

        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(detailActivity);
        viewPager.setCurrentItem(questionIds.indexOf(selectedId));
    }

    @Override
    public void onResume() {
        super.onResume();
        detailActivity.updateCurrentQuestion(getCurrentDetailFragment());
    }

    public DetailFragment getDetailFragment(int pos) {
        Fragment fragment = adapter.getItem(pos);
        if (fragment instanceof DetailFragment) {
            return (DetailFragment) fragment;
        } else {
            return null;
        }
    }

    public DetailFragment getCurrentDetailFragment() {
        int currentItem = viewPager.getCurrentItem();
        Fragment fragment = adapter.getItem(currentItem);
        if (fragment instanceof DetailFragment) {
            return (DetailFragment) fragment;
        } else {
            return null;
        }
    }
}
