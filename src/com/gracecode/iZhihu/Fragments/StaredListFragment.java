package com.gracecode.iZhihu.Fragments;

import com.gracecode.iZhihu.Dao.Question;

import java.util.ArrayList;

public class StaredListFragment extends BaseListFragment {
    public StaredListFragment() {
        super();
    }

    @Override
    public ArrayList<Question> getInitialData() {
        return getStaredQuestions();
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    questions = getStaredQuestions();
                } finally {
                    questionsAdapter.notifyDataSetChanged();
                }
            }
        }).start();
    }
}
