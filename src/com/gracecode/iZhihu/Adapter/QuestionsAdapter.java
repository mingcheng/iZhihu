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

    public QuestionsAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.listview_question_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex("question_title"));
        String description = cursor.getString(cursor.getColumnIndex("question_description"));

        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtDescription = (TextView) view.findViewById(R.id.description);
        View viewUnreadFlag = view.findViewById(R.id.unread_flag);

        if (cursor.getInt(cursor.getColumnIndex("unread")) != 0) {
            viewUnreadFlag.setVisibility(View.INVISIBLE);
        }

        txtTitle.setText(title);
        if (description.length() > 0) {
            description = Html.fromHtml(description).toString().trim();
            txtDescription.setText(description);
        } else {
            txtDescription.setVisibility(View.GONE);
        }
    }
}
