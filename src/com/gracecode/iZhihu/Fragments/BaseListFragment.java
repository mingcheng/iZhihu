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

import java.util.ArrayList;

abstract class BaseListFragment extends ListFragment {
    QuestionsAdapter questionsAdapter;
    private Activity activity;
    Context context;
    QuestionsDatabase questionsDatabase;
    ArrayList<Question> questions;
    int selectedPosition;
    SharedPreferences sharedPref;

    BaseListFragment() {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(questionsAdapter);
    }

    ArrayList<Question> getInitialData() {
        return new ArrayList<>();
    }

    private ArrayList<Integer> getQuestionsIdArrays() {
        ArrayList<Integer> ids = new ArrayList<>();

        for (Question question : questions) {
            ids.add(question.id);
        }
        return ids;
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        Question question = questions.get(position);

        Intent intent = new Intent(activity, Detail.class);
        intent.putExtra(Detail.INTENT_EXTRA_COLUM_ID, question.id);
        intent.putExtra(Detail.INTENT_EXTRA_MUTI_IDS, getQuestionsIdArrays());
        startActivity(intent);
    }

    public ArrayList<Question> getRecentQuestion() {
        return questionsDatabase.getRecentQuestions();
    }

    ArrayList<Question> getStaredQuestions() {
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
