package com.gracecode.iZhihu.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Dao.Question;

import java.util.ArrayList;

public class QuestionsListFragment extends BaseListFragment implements AbsListView.OnScrollListener {
    private static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private int currentPage = Database.FIRST_PAGE;
    private boolean isRunning = false;

    private class GetMoreLocalQuestionsTask extends AsyncTask<Void, Void, Void> {

        private static final long LOAD_DELAY_TIME = 1000;

        @Override
        protected Void doInBackground(Void... voids) {
            if (currentPage > database.getTotalPages()) {
                return null;
            }

            try {
                Thread.sleep(LOAD_DELAY_TIME);
                ArrayList<Question> newDatas = database.getRecentQuestions(++currentPage);
                questions.addAll(newDatas);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            isRunning = true;
        }

        @Override
        protected void onPostExecute(Void result) {
            questionsAdapter.notifyDataSetChanged();
            isRunning = false;
        }

    }

    private void getMoreLocalQuestions() {
        if (isRunning) {
            return;
        }
        new GetMoreLocalQuestionsTask().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentPage = sharedPref.getInt(KEY_CURRENT_PAGE, Database.FIRST_PAGE);
        if (selectedPosition != SELECT_NONE) {
            try {
                Question question = questions.get(selectedPosition);
                questions.remove(selectedPosition);
                questions.add(selectedPosition, database.getSingleQuestion(question.id));
                selectedPosition = SELECT_NONE;
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
        for (int i = Database.FIRST_PAGE; i <= currentPage; i++) {
            q.addAll(database.getRecentQuestions(i));
        }
        return q;
    }
}
