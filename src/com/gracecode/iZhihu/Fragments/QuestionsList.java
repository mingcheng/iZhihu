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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuestionsAdapter questionsAdapter = new QuestionsAdapter(context, getRecentQuestion());
        setListAdapter(questionsAdapter);
    }
}
