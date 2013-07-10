package com.gracecode.iZhihu.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gracecode.iZhihu.Dao.CommentItem;
import com.gracecode.iZhihu.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-7-10
 */
public class CommentAdapter extends BaseAdapter {
    private final List<CommentItem> comments;
    private final Activity activity;
    private final LayoutInflater layoutInflater;

    public CommentAdapter(Activity activity, List<CommentItem> comments) {
        this.comments = comments;
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public CommentItem getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        CommentItem comment = comments.get(i);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.listview_comment_item, null);

            TextView textViewAuthor = (TextView) view.findViewById(R.id.author);
            TextView textViewContent = (TextView) view.findViewById(R.id.content);
            TextView textViewTime = (TextView) view.findViewById(R.id.time);

            holder = new ViewHolder();
            holder.author = textViewAuthor;
            holder.content = textViewContent;
            holder.time = textViewTime;

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.author.setText(comment.getAuthor());
        holder.content.setText(comment.getContent());
        holder.time.setText(formatTime(comment.getTimeStamp()));

        return view;
    }

    private String formatTime(long timeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(timeStamp * 1000);
    }

    private static class ViewHolder {
        public TextView author;
        public TextView content;
        public TextView time;
    }
}
