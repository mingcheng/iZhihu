package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.R;

public abstract class BaseQuestionsList extends ListFragment {
    protected Activity activity;
    protected Context context;
    protected Database database;
    protected Cursor questions;

    public BaseQuestionsList() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = getActivity();
        this.context = activity.getApplicationContext();
        this.database = new Database(context);
    }

    @Override
    public void onStart() {
        QuestionsAdapter adapter = (QuestionsAdapter) getListAdapter();
        adapter.notifyDataSetChanged();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_questions, container, false);
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        if (questions != null && questions.moveToPosition(position)) {
            Intent intent = new Intent(activity, Detail.class);

            int questionsId = questions.getInt(questions.getColumnIndex(Database.COLUM_ID));
            intent.putExtra(Database.COLUM_ID, questionsId);

            startActivity(intent);
        }
    }

    public Cursor getRecentQuestion() {
        questions = database.getRecentQuestions();
        return questions;
    }

    public Cursor getFavoritesQuestion() {
        questions = database.getFavoritesQuestion();
        return questions;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
            database = null;
        }
    }
}
