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

        try {
            Question question = questions.get(selectedPosition);
            if (!question.isStared()) {
                questions.remove(selectedPosition);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            questionsAdapter.notifyDataSetChanged();
        }
    }
}
