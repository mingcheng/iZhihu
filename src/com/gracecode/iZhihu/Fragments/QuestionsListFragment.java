package com.gracecode.iZhihu.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Dao.Question;

import java.util.ArrayList;

public class QuestionsListFragment extends BaseListFragment implements AbsListView.OnScrollListener {
    private static final String KEY_CURRENT_PAGE = "currentPage";
    private int currentPage = Database.FIRST_PAGE;
    private boolean isRunning = false;

    private class GetMoreLocalQuestionsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (currentPage > database.getTotalPages()) {
                return null;
            }

            try {
                Thread.sleep(3000);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions.addAll(getRecentQuestion());
    }

    @Override
    public void onStart() {
        super.onStart();

        currentPage = sharedPref.getInt(KEY_CURRENT_PAGE, Database.FIRST_PAGE);
        getAllRecentQuestion();
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onStop() {
        int position = getListView().getSelectedItemPosition();
        savePref(KEY_SELECTED_POSITION, position);
        savePref(KEY_CURRENT_PAGE, currentPage);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (selectedPosition != SELECT_NONE) {
            try {
                Question question = questions.get(selectedPosition);
                questions.remove(selectedPosition);
                questions.add(selectedPosition, database.getSingleQuestion(question.id));

                getListView().setSelection(selectedPosition);

                selectedPosition = SELECT_NONE;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } finally {
                questionsAdapter.notifyDataSetChanged();
            }
        }

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

    public ArrayList<Question> getAllRecentQuestion() {
        questions.clear();
        for (int i = Database.FIRST_PAGE; i <= currentPage; i++) {
            questions.addAll(database.getRecentQuestions(i));
        }
        return questions;
    }
}
