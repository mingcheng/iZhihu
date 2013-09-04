package com.gracecode.iZhihu.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import com.gracecode.iZhihu.BuildConfig;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.db.QuestionsDatabase;
import com.gracecode.iZhihu.util.Helper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class QuestionsListFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final String TAG = QuestionsListFragment.class.getName();

    private static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private int mCurrentPage = QuestionsDatabase.FIRST_PAGE;

    private final Handler mUpdateDataSetChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what > 0) {
                    mQuestionsAdapter.notifyDataSetChanged();
                } else {
                    Helper.showShortToast(mContext, getString(R.string.nomore_questions));
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mPull2RefreshView.onRefreshComplete();
                if (msg.what <= 0) {
                    mPull2RefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    public void updateQuestionsFromDatabase() {
        mQuestions.clear();
        for (int i = QuestionsDatabase.FIRST_PAGE; i <= mCurrentPage; i++) {
            mQuestions.addAll(mQuestionsDatabase.getRecentQuestions(i));
        }
    }

    public void addNewQuestionsAtHead(ArrayList<Question> questions) {
        this.mQuestions.addAll(0, questions);
    }

    /**
     * Get more times from db.
     */
    synchronized private void getMoreQuestionsFromDatabase() {
        new Thread(new Runnable() {
            private static final long DELAY_TIME = 1000;

            @Override
            public void run() {
                int size = 0;
                try {
                    Thread.sleep(DELAY_TIME);
                    if (mCurrentPage <= mQuestionsDatabase.getTotalPages()) {
                        ArrayList<Question> newDatas = mQuestionsDatabase.getRecentQuestions(++mCurrentPage);
                        mQuestions.addAll(newDatas);
                        size = newDatas.size();
                    }
                } catch (InterruptedException e) {
                    if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
                } finally {
                    mUpdateDataSetChangedHandler.sendEmptyMessage(size);
                }
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();

        mPull2RefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPull2RefreshView.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mQuestions.size() > 0) {
                        for (Question question : mQuestions) {
                            question.setStared(mQuestionsDatabase.isStared(question.getId()));
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
                } catch (NullPointerException e) {
                    if (BuildConfig.DEBUG) Log.e(TAG, e.getMessage());
                } finally {
                    mUpdateDataSetChangedHandler.sendEmptyMessage(mQuestions.size());
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
