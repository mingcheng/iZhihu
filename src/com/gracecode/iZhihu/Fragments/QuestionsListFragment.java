package com.gracecode.iZhihu.Fragments;

import android.os.Bundle;
import android.widget.AbsListView;
import com.gracecode.iZhihu.Adapter.QuestionsAdapter;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class QuestionsListFragment extends BaseListFragment implements AbsListView.OnScrollListener {
    private int currentPage = 1;
    private boolean isRunning = false;
    private int lastCursorCount = 0;

//    private boolean needLoadMorequestion() {
//        if (questions == null || lastCursorCount != questions.getCount()) {
//            return true;
//        }
//        return false;
//    }
//
//    private void getMoreLocalQuestions() {
//        if (isRunning || !needLoadMorequestion()) return;
//        new GetMoreLocalQuestionsTask(context, new GetMoreLocalQuestionsTask.Callback() {
//
//            @Override
//            public void onPostExecute(Object result) {
//                Cursor c = (Cursor) result;
//                lastCursorCount = c.getCount();
//                changeCursor(c);
//                isRunning = false;
//            }
//
//            @Override
//            public void onPreExecute() {
//                isRunning = true;
//            }
//        }).execute(++currentPage);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions.addAll(getRecentQuestion());
        questionsAdapter = new QuestionsAdapter(context, questions);
        setListAdapter(questionsAdapter);
    }

    @Override
    public void onStart() {
        //questionsAdapter.notifyDataSetChanged();
        getListView().setOnScrollListener(this);
        super.onStart();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (i + i2 == i3) {

        }
    }
}
