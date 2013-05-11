package com.gracecode.iZhihu.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.R;

import java.util.ArrayList;

public class QuestionsListFragment extends BaseListFragment implements AbsListView.OnScrollListener {
    private static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private static final int LOAD_DELAY_TIME = 2000;
    private int currentPage = QuestionsDatabase.FIRST_PAGE;
    private boolean isRunning = false;

    private Handler updateDataSetChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what > 0) {
                Toast.makeText(context, getString(R.string.loaded_more_data), Toast.LENGTH_SHORT).show();
            }
            questionsAdapter.notifyDataSetChanged();
            isRunning = false;
        }
    };

    private void getMoreLocalQuestions() {
        if (isRunning) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;

                if (currentPage > questionsDatabase.getTotalPages()) {
                    return;
                }

                try {
                    ArrayList<Question> newDatas = questionsDatabase.getRecentQuestions(++currentPage);
                    questions.addAll(newDatas);

                    Thread.sleep(LOAD_DELAY_TIME);
                    updateDataSetChangedHandler.sendEmptyMessage(newDatas.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (selectedPosition != SELECT_NONE) {
            try {
                Question question = questions.get(selectedPosition);
                questions.remove(selectedPosition);
                questions.add(selectedPosition, questionsDatabase.getSingleQuestion(question.id));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } finally {
                questionsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onStop() {
        savePref(KEY_CURRENT_PAGE, currentPage);
        super.onStop();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (i + i2 == i3) {
            getMoreLocalQuestions();
        }
    }

    @Override
    public ArrayList<Question> getInitialData() {
        ArrayList<Question> q = new ArrayList<Question>();

        this.currentPage = sharedPref.getInt(KEY_CURRENT_PAGE, QuestionsDatabase.FIRST_PAGE);
        for (int i = QuestionsDatabase.FIRST_PAGE; i <= currentPage; i++) {
            q.addAll(questionsDatabase.getRecentQuestions(i));
        }
        return q;
    }
}
