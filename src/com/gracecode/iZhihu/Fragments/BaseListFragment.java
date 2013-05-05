package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.R;

import java.util.ArrayList;

public abstract class BaseListFragment extends ListFragment {
    protected QuestionsAdapter questionsAdapter;
    protected Activity activity;
    protected Context context;
    protected Database database;
    protected ArrayList<Database.Question> questions;

    public BaseListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = getActivity();
        this.context = activity.getApplicationContext();
        this.database = new Database(context);
        this.questions = new ArrayList<Database.Question>();
    }

    @Override
    public void onStart() {
        if (questionsAdapter != null) {
//            questionsAdapter.notifyDataSetChanged();
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_questions, container, false);
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        Database.Question question = questions.get(position);

        Intent intent = new Intent(activity, Detail.class);
        intent.putExtra(Database.COLUM_ID, question.id);
        startActivity(intent);
    }

    public ArrayList<Database.Question> getRecentQuestion() {
        return database.getRecentQuestions();
    }

    public ArrayList<Database.Question> getStaredQuestions() {
        return database.getStaredQuestions();
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
