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
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-15
 */
public class ScrollDetailFragment extends Fragment {
    private final Detail detailActivity;
    private final ArrayList<Question> questions;
    private final Integer position;
    private ViewPager viewPager;
    private Activity activity;
    private DetailListsAdapter adapter;

    public ScrollDetailFragment(Detail detailActivity, ArrayList<Question> questions, Integer position) {
        this.detailActivity = detailActivity;
        this.questions = questions;
        this.position = position;
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
        this.adapter = new DetailListsAdapter(activity, questions);

        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(detailActivity);
        viewPager.setCurrentItem(position);
    }
}
