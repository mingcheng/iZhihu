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
    }

    @Override
    public void onStart() {
        questions.clear();
        questions.addAll(getStaredQuestions());
        super.onStart();
    }

    @Override
    public void onStop() {
        int position = getListView().getSelectedItemPosition();
        savePref(KEY_SELECTED_POSITION, position);
        super.onStop();
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
        } else {

        }
    }
}
