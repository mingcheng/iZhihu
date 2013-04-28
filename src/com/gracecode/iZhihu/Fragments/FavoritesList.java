package com.gracecode.iZhihu.Fragments;


import android.os.Bundle;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;

public class FavoritesList extends BaseQuestionsList {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuestionsAdapter questionsAdapter = new QuestionsAdapter(context, getFavoritesQuestion());
        setListAdapter(questionsAdapter);
    }
}
