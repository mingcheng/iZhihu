package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import com.gracecode.iZhihu.Activity.Detail;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

abstract class BaseListFragment extends PullToRefreshListFragment implements AdapterView.OnItemClickListener {
    QuestionsAdapter questionsAdapter;
    private Activity activity;
    Context context;
    QuestionsDatabase questionsDatabase;
    ArrayList<Question> questions;
    int selectedPosition;
    SharedPreferences sharedPref;
    protected PullToRefreshListView pull2RefreshView;

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
    public void onStart() {
        super.onStart();

        // 默认关闭下拉
        pull2RefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(questionsAdapter);

        // 下拉 ListView 控件
        this.pull2RefreshView = getPullToRefreshListView();

        getListView().setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // Android-PullToRefresh 似乎增加了个不可见条目，所以要 -1
        Question question = questions.get((pull2RefreshView != null) ? position - 1 : position);

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
