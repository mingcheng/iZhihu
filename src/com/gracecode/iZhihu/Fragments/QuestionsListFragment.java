package com.gracecode.iZhihu.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AbsListView;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.Dao.Question;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class QuestionsListFragment extends BaseListFragment implements AbsListView.OnScrollListener {
    private static final String KEY_CURRENT_PAGE = "currentPage";
    private int currentPage = Database.FIRST_PAGE;
    private boolean isRunning = false;
    private int arrayListSize = 0;

    private boolean needLoadMorequestion() {
        if (questions.isEmpty() || arrayListSize != questions.size()) {
            return true;
        }
        return false;
    }

    private void getMoreLocalQuestions() {
        if (isRunning || !needLoadMorequestion()) return;
        questions.addAll(database.getRecentQuestions(++currentPage));

        arrayListSize = questions.size();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        super.onStop();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_CURRENT_PAGE, currentPage);
        editor.commit();
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
