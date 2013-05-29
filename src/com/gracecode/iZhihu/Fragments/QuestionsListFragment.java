package com.gracecode.iZhihu.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

public class QuestionsListFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private int currentPage = QuestionsDatabase.FIRST_PAGE;
    private boolean isRunning = false;

    private final Handler updateDataSetChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what > 0) {
                    questionsAdapter.notifyDataSetChanged();
                } else {
                    Util.showShortToast(context, getString(R.string.not_more_questions));
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                pull2RefreshView.onRefreshComplete();
            }
        }
    };

    private void getMoreLocalQuestions() {
        if (isRunning) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;

                isRunning = true;
                try {
                    if (currentPage <= questionsDatabase.getTotalPages()) {
                        ArrayList<Question> newDatas = questionsDatabase.getRecentQuestions(++currentPage);
                        questions.addAll(newDatas);
                        size = newDatas.size();
                    }
                } finally {
                    updateDataSetChangedHandler.sendEmptyMessage(size);
                    isRunning = false;
                }
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            Question question = questions.get(selectedPosition);
            questions.remove(selectedPosition);
            questions.add(selectedPosition, questionsDatabase.getSingleQuestion(question.id));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            questionsAdapter.notifyDataSetChanged();
        }

        pull2RefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pull2RefreshView.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStop() {
        Util.savePref(sharedPref, KEY_CURRENT_PAGE, currentPage);
        super.onStop();
    }


    @Override
    public ArrayList<Question> getInitialData() {
        ArrayList<Question> q = new ArrayList<>();

        // @todo 载入的页面逻辑，需要优化
        this.currentPage = sharedPref.getInt(KEY_CURRENT_PAGE, QuestionsDatabase.FIRST_PAGE);
        for (int i = QuestionsDatabase.FIRST_PAGE; i <= currentPage; i++) {
            q.addAll(questionsDatabase.getRecentQuestions(i));
        }

        return q;
    }

    /**
     * 远程刷新获取条目
     *
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//        Util.showLongToast(context, "down");
    }

    /**
     * 载入本地更多的条目
     *
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getMoreLocalQuestions();
    }
}
