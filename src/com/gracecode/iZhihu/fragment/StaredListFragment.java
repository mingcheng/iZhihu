package com.gracecode.iZhihu.fragment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.gracecode.iZhihu.dao.Question;

import java.util.ArrayList;

public class StaredListFragment extends BaseListFragment {

    private final Handler updateDataSetChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mQuestionsAdapter.notifyDataSetChanged();
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
                    mQuestions = getStaredQuestions();
                } finally {
                    updateDataSetChangedHandler.sendEmptyMessage(Activity.RESULT_OK);
                }
            }
        }).start();
    }
}
