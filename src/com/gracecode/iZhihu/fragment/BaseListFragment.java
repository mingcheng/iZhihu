package com.gracecode.iZhihu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import com.gracecode.iZhihu.activity.Detail;
import com.gracecode.iZhihu.adapter.QuestionsAdapter;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.db.QuestionsDatabase;
import com.gracecode.iZhihu.util.Helper;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

public abstract class BaseListFragment extends PullToRefreshListFragment implements AdapterView.OnItemClickListener {
    public static final String SAVED_QUESTIONS = "savedQuestions";

    Context mContext;
    QuestionsAdapter mQuestionsAdapter;
    private Activity mActivity;
    QuestionsDatabase mQuestionsDatabase;
    ArrayList<Question> mQuestions;

    SharedPreferences mSharedPreferences;
    protected PullToRefreshListView mPull2RefreshView;

    BaseListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mActivity = getActivity();
        this.mContext = mActivity.getApplicationContext();
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // @todo 初始化数据库
        this.mQuestionsDatabase = new QuestionsDatabase(mContext);

        if (savedInstanceState != null) {
            this.mQuestions = savedInstanceState.getParcelableArrayList(SAVED_QUESTIONS);
        }

        if (this.mQuestions == null || this.mQuestions.size() <= 0) {
            this.mQuestions = getInitialData();
        }

        this.mQuestionsAdapter = new QuestionsAdapter(getActivity(), mQuestions);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mQuestions != null && mQuestions.size() > 0) {
            outState.putParcelableArrayList(SAVED_QUESTIONS, mQuestions);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 默认关闭下拉
        mPull2RefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setListAdapter(mQuestionsAdapter);

        // 下拉 ListView 控件
        this.mPull2RefreshView = getPullToRefreshListView();
        getListView().setOnItemClickListener(this);

        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Initial Data
     *
     * @return An Empty ArrayList By Default.
     */
    ArrayList<Question> getInitialData() {
        return new ArrayList<Question>();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // Android-PullToRefresh 似乎增加了个不可见条目，所以要 -1
        int selectedPosition = (mPull2RefreshView != null) ? position - 1 : position;
        Helper.startDetailActivity(getActivity(), mQuestions, selectedPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Intent.FILL_IN_PACKAGE) {
            mQuestions = data.getParcelableArrayListExtra(Detail.INTENT_MODIFIED_LISTS);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * notifyDataSetChanged by manually
     */
    public void notifyDataSetChanged() {
        if (mQuestionsAdapter != null) {
            mQuestionsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set Questions
     *
     * @param mQuestions
     */
    public void setQuestions(ArrayList<Question> mQuestions) {
        this.mQuestions = mQuestions;
    }

    /**
     * Get Recent Questions By Update Time.
     *
     * @return mQuestions
     */
    public ArrayList<Question> getRecentQuestion() {
        return mQuestionsDatabase.getRecentQuestions();
    }


    /**
     * Get Stared Questions.
     *
     * @return mQuestions
     */
    public ArrayList<Question> getStaredQuestions() {
        return mQuestionsDatabase.getStaredQuestions();
    }

    @Override
    public void onDestroy() {
        mQuestionsDatabase.close();
        super.onDestroy();
    }
}
