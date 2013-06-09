package com.gracecode.iZhihu.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

public class QuestionsListFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private int currentPage = QuestionsDatabase.FIRST_PAGE;

    private final Handler updateDataSetChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what > 0) {
                    questionsAdapter.notifyDataSetChanged();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                pull2RefreshView.onRefreshComplete();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.currentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_PAGE, currentPage);
        super.onSaveInstanceState(outState);
    }

    public void updateQuestionsFromDatabase() {
        questions.clear();
        for (int i = QuestionsDatabase.FIRST_PAGE; i <= currentPage; i++) {
            questions.addAll(questionsDatabase.getRecentQuestions(i));
        }
    }

    public void addNewQuestionsAtHead(ArrayList<Question> questions) {
        this.questions.addAll(0, questions);
    }

    /**
     * Get more times from database.
     */
    synchronized private void getMoreQuestionsFromDatabase() {
        new Thread(new Runnable() {
            private static final long DELAY_TIME = 1000;

            @Override
            public void run() {
                int size = 0;
                try {
                    Thread.sleep(DELAY_TIME);

                    if (currentPage <= questionsDatabase.getTotalPages()) {
                        ArrayList<Question> newDatas = questionsDatabase.getRecentQuestions(++currentPage);
                        questions.addAll(newDatas);
                        size = newDatas.size();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    updateDataSetChangedHandler.sendEmptyMessage(size);
                }
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();

        pull2RefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pull2RefreshView.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Question question : questions) {
                        question.setStared(questionsDatabase.isStared(question.getId()));
                    }
                } finally {
                    updateDataSetChangedHandler.sendEmptyMessage(questions.size());
                }
            }
        }).start();
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public ArrayList<Question> getInitialData() {
        return getRecentQuestion();
    }


    /**
     * 远程刷新获取条目
     *
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }


    /**
     * 载入本地更多的条目
     *
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getMoreQuestionsFromDatabase();
    }

}
