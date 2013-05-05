package com.gracecode.iZhihu.Fragments;


import android.os.Bundle;
import com.gracecode.iZhihu.Dao.Question;

public class StaredListFragment extends BaseListFragment {
    public StaredListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions.addAll(getStaredQuestions());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedPosition != SELECT_NONE) {
            try {
                Question question = questions.get(selectedPosition);
                if (!question.isStared()) {
                    questions.remove(selectedPosition);
                }
                getListView().setSelection(selectedPosition);

                selectedPosition = SELECT_NONE;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } finally {
                questionsAdapter.notifyDataSetChanged();
            }
        }
    }
}
