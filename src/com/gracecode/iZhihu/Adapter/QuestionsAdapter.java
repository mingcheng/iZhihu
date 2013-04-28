package com.gracecode.iZhihu.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.gracecode.iZhihu.R;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class QuestionsAdapter extends CursorAdapter {
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final Cursor cursor;

    public QuestionsAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        this.context = context;
        this.cursor = cursor;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.listview_question_item, null);

        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtDescription = (TextView) view.findViewById(R.id.description);

        view.setTag(R.id.title, txtTitle);
        view.setTag(R.id.description, txtDescription);

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Couldn't move cursor to position " + position);
        }

        if (convertView == null) {
            v = newView(context, cursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex("question_title"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        String userName = cursor.getString(cursor.getColumnIndex("user_name"));

        content = Html.fromHtml(content).toString().trim();


        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtDescription = (TextView) view.findViewById(R.id.description);
        View viewUnreadFlag = view.findViewById(R.id.unread_flag);

        if (cursor.getInt(cursor.getColumnIndex("unread")) != 0) {
            viewUnreadFlag.setVisibility(View.INVISIBLE);
        }

        txtTitle.setText(title);
        txtDescription.setText((userName.length() > 1 ? userName.trim() + "：" : "") + content);
    }
}
