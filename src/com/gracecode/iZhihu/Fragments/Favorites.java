package com.gracecode.iZhihu.Fragments;


import android.os.Bundle;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;

public class Favorites extends BaseList {
    private QuestionsAdapter questionsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionsAdapter = new QuestionsAdapter(context, null);
        setListAdapter(questionsAdapter);
    }

    @Override
    public void onStart() {
        questionsAdapter.changeCursor(getFavoritesQuestion());
        super.onStart();
    }
}
