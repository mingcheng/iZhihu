package com.gracecode.iZhihu.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.gracecode.iZhihu.Dao.Database;
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
        return layoutInflater.inflate(R.layout.listview_question_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(Database.COLUM_CONTENT));
        String userName = cursor.getString(cursor.getColumnIndex(Database.COLUM_USER_NAME));

        content = Html.fromHtml(content).toString().trim();
        content = (userName.length() > 1 ? userName.trim() + "：" : "") + content;

        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtDescription = (TextView) view.findViewById(R.id.description);
        View viewUnreadFlag = view.findViewById(R.id.unread_flag);

        if (cursor.getInt(cursor.getColumnIndex(Database.COLUM_UNREAD)) != 0) {
            viewUnreadFlag.setVisibility(View.INVISIBLE);
        } else {
            viewUnreadFlag.setVisibility(View.VISIBLE);
        }

        txtTitle.setText(title);
        txtDescription.setText(content);
        if (content.length() > 1) {
            txtDescription.setVisibility(View.VISIBLE);
        } else {
            txtDescription.setVisibility(View.GONE);
        }
    }
}
