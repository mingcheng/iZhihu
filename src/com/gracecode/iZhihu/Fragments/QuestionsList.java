package com.gracecode.iZhihu.Fragments;

import android.os.Bundle;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class QuestionsList extends BaseQuestionsList {
    private QuestionsAdapter questionsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionsAdapter = new QuestionsAdapter(context, null);
        setListAdapter(questionsAdapter);
    }

    @Override
    public void onStart() {
        questionsAdapter.changeCursor(getRecentQuestion());
        super.onStart();
    }
}
