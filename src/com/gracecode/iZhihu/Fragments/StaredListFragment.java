package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.gracecode.iZhihu.Dao.Question;

import java.util.ArrayList;

public class StaredListFragment extends BaseListFragment {

    private final Handler updateDataSetChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            questionsAdapter.notifyDataSetChanged();
        }
    };

    public StaredListFragment() {
        super();
    }

    @Override
    public ArrayList<Question> getInitialData() {
        return getStaredQuestions();
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    questions = getStaredQuestions();
                } finally {
                    updateDataSetChangedHandler.sendEmptyMessage(Activity.RESULT_OK);
                }
            }
        }).start();
    }
}
