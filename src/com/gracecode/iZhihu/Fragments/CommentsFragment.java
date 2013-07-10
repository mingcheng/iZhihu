package com.gracecode.iZhihu.Fragments;

import android.app.ListFragment;
import android.os.Bundle;
import com.gracecode.iZhihu.Adapter.CommentAdapter;
import com.gracecode.iZhihu.Dao.CommentItem;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-7-10
 */
public class CommentsFragment extends ListFragment {
    private final ArrayList<CommentItem> comments;

    public CommentsFragment(ArrayList<CommentItem> commentItems) {
        this.comments = commentItems;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new CommentAdapter(getActivity(), comments));
    }
}
