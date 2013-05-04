package com.gracecode.iZhihu.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.AbsListView;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;
import com.gracecode.iZhihu.Tasks.GetMoreLocalQuestionsTask;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class QuestionsList extends BaseQuestionsList implements AbsListView.OnScrollListener {
    private QuestionsAdapter questionsAdapter;
    private int currentPage = 1;
    private boolean isRunning = false;
    private int lastCursorCount = 0;

    private boolean needLoadMorequestion() {
        if (questions == null || lastCursorCount != questions.getCount()) {
            return true;
        }
        return false;
    }

    private void getMoreLocalQuestions() {
        if (isRunning || !needLoadMorequestion()) return;
        new GetMoreLocalQuestionsTask(context, new GetMoreLocalQuestionsTask.Callback() {

            @Override
            public void onPostExecute(Object result) {
                Cursor c = (Cursor) result;
                lastCursorCount = c.getCount();
                changeCursor(c);
                isRunning = false;
            }

            @Override
            public void onPreExecute() {
                isRunning = true;
            }
        }).execute(++currentPage);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionsAdapter = new QuestionsAdapter(context, null);
        setListAdapter(questionsAdapter);
    }

    private void changeCursor(Cursor cursor) {
        this.questions = cursor;
        questionsAdapter.changeCursor(cursor);
    }

    @Override
    public void onStart() {
        changeCursor(getRecentQuestion());
        getListView().setOnScrollListener(this);
        super.onStart();
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
}
