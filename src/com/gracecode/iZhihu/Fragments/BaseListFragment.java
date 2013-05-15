package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.util.ArrayList;

public abstract class BaseListFragment extends ListFragment {
    public static final String KEY_SELECTED_POSITION = "KEY_SELECTED_POSITION";
    public static final int SELECT_NONE = -1;
    public static final int REQUEST_FOR_POSITION = 0;
    protected QuestionsAdapter questionsAdapter;
    protected Activity activity;
    protected Context context;
    protected QuestionsDatabase questionsDatabase;
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
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedPosition == SELECT_NONE) {
            selectedPosition = sharedPref.getInt(KEY_SELECTED_POSITION, SELECT_NONE);
        }

        if (selectedPosition != SELECT_NONE) {
            getListView().setSelection(selectedPosition);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(questionsAdapter);
    }


    @Override
    public void onStop() {
        if (selectedPosition == SELECT_NONE) {
            Util.savePref(sharedPref, KEY_SELECTED_POSITION, getListView().getFirstVisiblePosition());
        }
        super.onStop();
    }


    public ArrayList<Question> getInitialData() {
        return new ArrayList<Question>();
    }


    private ArrayList<Integer> getQuestionsIdArrays() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (int i = 0, size = questions.size(); i < size; i++) {
            ids.add(questions.get(i).id);
        }

        return ids;
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        Question question = questions.get(position);
        selectedPosition = position;
        if (selectedPosition != SELECT_NONE) {
            Util.savePref(sharedPref, KEY_SELECTED_POSITION, position);
        }

        Intent intent = new Intent(activity, Detail.class);
        intent.putExtra(Detail.INTENT_EXTRA_COLUM_ID, question.id);
        intent.putExtra(Detail.INTENT_EXTRA_MUTI_IDS, getQuestionsIdArrays());
        intent.putExtra(KEY_SELECTED_POSITION, selectedPosition);
        startActivityForResult(intent, REQUEST_FOR_POSITION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        selectedPosition = sharedPref.getInt(KEY_SELECTED_POSITION, SELECT_NONE);
        super.onActivityResult(requestCode, resultCode, intent);
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
        if (questionsDatabase != null) {
            questionsDatabase.close();
            questionsDatabase = null;
        }
    }
}
