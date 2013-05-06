package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.R;

import java.util.ArrayList;

public abstract class BaseListFragment extends ListFragment {
    public static final String KEY_SELECTED_POSITION = "SELECTED_POSITION";
    public static final int SELECT_NONE = -1;
    protected QuestionsAdapter questionsAdapter;
    protected Activity activity;
    protected Context context;
    protected Database database;
    protected ArrayList<Question> questions;
    protected int selectedPosition;
    protected SharedPreferences sharedPref;

    public BaseListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = getActivity();
        this.context = activity.getApplicationContext();
        this.database = new Database(context);
        this.questions = new ArrayList<Question>();
        this.questionsAdapter = new QuestionsAdapter(context, questions);
        this.sharedPref = context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        selectedPosition = sharedPref.getInt(KEY_SELECTED_POSITION, SELECT_NONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        setListAdapter(questionsAdapter);
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_questions, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean savePref(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        Question question = questions.get(position);

        selectedPosition = position;
        savePref(KEY_SELECTED_POSITION, position);

        Intent intent = new Intent(activity, Detail.class);
        intent.putExtra(Database.COLUM_ID, question.id);
        startActivity(intent);
    }


    public ArrayList<Question> getRecentQuestion() {
        return database.getRecentQuestions();
    }

    public ArrayList<Question> getStaredQuestions() {
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
