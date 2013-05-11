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
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.R;

import java.util.ArrayList;

public abstract class BaseListFragment extends ListFragment {
    public static final String KEY_SELECTED_POSITION = "KEY_SELECTED_POSITION";
    public static final int SELECT_NONE = -1;
    protected QuestionsAdapter questionsAdapter;
    protected Activity activity;
    protected Context context;
    protected static QuestionsDatabase questionsDatabase;
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
        this.sharedPref = context.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        // @todo 初始化数据库
        this.questionsDatabase = new QuestionsDatabase(context);
        this.questions = getInitialData();
        this.questionsAdapter = new QuestionsAdapter(context, questions);
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

    @Override
    public void onStart() {
        super.onStart();

        this.questionsDatabase = new QuestionsDatabase(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedPosition = sharedPref.getInt(KEY_SELECTED_POSITION, SELECT_NONE);
        if (selectedPosition != SELECT_NONE) {
            getListView().setSelection(selectedPosition);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setListAdapter(questionsAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        if (selectedPosition == SELECT_NONE) {
            savePref(KEY_SELECTED_POSITION, getListView().getFirstVisiblePosition());
        }
        super.onStop();
    }

    public ArrayList<Question> getInitialData() {
        return new ArrayList<Question>();
    }

    public boolean savePref(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        Question question = questions.get(position);
        selectedPosition = position;
        if (selectedPosition != SELECT_NONE) {
            savePref(KEY_SELECTED_POSITION, position);
        }

        Intent intent = new Intent(activity, Detail.class);
        intent.putExtra(QuestionsDatabase.COLUM_ID, question.id);
        startActivity(intent);
    }

    public ArrayList<Question> getRecentQuestion() {
        return questionsDatabase.getRecentQuestions();
    }

    public ArrayList<Question> getStaredQuestions() {
        return questionsDatabase.getStaredQuestions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
