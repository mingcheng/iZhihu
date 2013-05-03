package com.gracecode.iZhihu.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
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
        View view = layoutInflater.inflate(R.layout.listview_question_item, null);

        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtDescription = (TextView) view.findViewById(R.id.description);
        ImageView unReadFlag = (ImageView) view.findViewById(R.id.unread_flag);

        view.setTag(R.id.title, txtTitle);
        view.setTag(R.id.description, txtDescription);
        view.setTag(R.id.unread_flag, unReadFlag);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(Database.COLUM_CONTENT));
        String userName = cursor.getString(cursor.getColumnIndex(Database.COLUM_USER_NAME));

        boolean isStared = (cursor.getInt(cursor.getColumnIndex(Database.COLUM_STARED)) == Database.VALUE_STARED) ? true : false;
        boolean isUnreaded = (cursor.getInt(cursor.getColumnIndex(Database.COLUM_UNREAD)) == Database.VALUE_READED) ? false : true;

        content = Html.fromHtml(content).toString().trim();
        content = (userName.length() > 1 ? userName.trim() + "：" : "") + content;

        TextView txtTitle = (TextView) view.findViewById(R.id.title);
        TextView txtDescription = (TextView) view.findViewById(R.id.description);
        View viewUnreadFlag = view.findViewById(R.id.unread_flag);

        if (isStared) {
            viewUnreadFlag.setBackgroundResource(android.R.color.holo_red_light);
        } else {
            viewUnreadFlag.setBackgroundResource(android.R.color.holo_blue_light);
        }

        if (isStared || isUnreaded) {
            viewUnreadFlag.setVisibility(View.VISIBLE);
        } else {
            viewUnreadFlag.setVisibility(View.INVISIBLE);
        }

        txtTitle.setText(title);
        txtDescription.setText(content);
    }
}
