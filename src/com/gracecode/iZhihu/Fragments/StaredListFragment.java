package com.gracecode.iZhihu.Fragments;


import android.os.Bundle;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;

public class StaredListFragment extends BaseListFragment {
    private QuestionsAdapter questionsAdapter;

    public StaredListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questions = getStaredQuestions();
        questionsAdapter = new QuestionsAdapter(context, questions);
        setListAdapter(questionsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
