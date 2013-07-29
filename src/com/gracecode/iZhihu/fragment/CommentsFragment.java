package com.gracecode.iZhihu.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import com.gracecode.iZhihu.adapter.CommentAdapter;
import com.gracecode.iZhihu.dao.Comment;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-7-10
 */
public class CommentsFragment extends ListFragment {
    private final ArrayList<Comment> comments;

    public CommentsFragment(ArrayList<Comment> commentItems) {
        this.comments = commentItems;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new CommentAdapter(getActivity(), comments));
    }
}
