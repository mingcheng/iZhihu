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

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-4-27
 */
public class QuestionsAdapter extends CursorAdapter {
    private static final int MAX_DESPCRIPTION_LENGTH = 100;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final Cursor cursor;
    private int idxTitle;
    private HashMap<Integer, CacheItem> cacheItemHashMap;
    private int idxContent;
    private int idxUserName;
    private int idxStared;
    private int idxUnread;

    private static class CacheItem {
        public String title;
        public String description;
        public Boolean isUnread;
        public Boolean isStared;
    }

    public QuestionsAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        this.context = context;
        this.cursor = cursor;
        this.cacheItemHashMap = new HashMap<Integer, CacheItem>();
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
    public void changeCursor(Cursor cursor) {
        clear();
        cursor.moveToFirst();

        this.idxTitle = cursor.getColumnIndex(Database.COLUM_QUESTION_TITLE);
        this.idxContent = cursor.getColumnIndex(Database.COLUM_CONTENT);
        this.idxUserName = cursor.getColumnIndex(Database.COLUM_USER_NAME);
        this.idxStared = cursor.getColumnIndex(Database.COLUM_STARED);
        this.idxUnread = cursor.getColumnIndex(Database.COLUM_UNREAD);

        while (cursor.moveToNext()) {
            getFromCaches(cursor);
        }
        super.changeCursor(cursor);
    }

    private CacheItem getFromCaches(Cursor cursor) {
        int position = cursor.getPosition();

        if (cacheItemHashMap.containsKey(position)) {
            return cacheItemHashMap.get(position);
        } else {
            String title = cursor.getString(idxTitle);
            String content = cursor.getString(idxContent);
            String userName = cursor.getString(idxUserName);
            int maxLength = content.length() > MAX_DESPCRIPTION_LENGTH ? MAX_DESPCRIPTION_LENGTH : content.length();

            content = Html.fromHtml(content).toString().trim();
            content = (userName.length() > 1 ? userName.trim() + "：" : "") + content;

            boolean isStared = (cursor.getInt(idxStared) == Database.VALUE_STARED) ? true : false;
            boolean isUnreaded = (cursor.getInt(idxUnread) == Database.VALUE_READED) ? false : true;

            CacheItem item = new CacheItem();
            item.description = content.substring(0, maxLength);
            item.title = title;
            item.isStared = isStared;
            item.isUnread = isUnreaded;

            cacheItemHashMap.put(position, item);
            return item;
        }
    }

    public void clear() {
        cacheItemHashMap.clear();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txtTitle = (TextView) view.getTag(R.id.title);
        TextView txtDescription = (TextView) view.getTag(R.id.description);
        ImageView viewUnreadFlag = (ImageView) view.getTag(R.id.unread_flag);

        CacheItem item = getFromCaches(cursor);
        if (item.isStared) {
            viewUnreadFlag.setBackgroundResource(android.R.color.holo_red_light);
        } else {
            viewUnreadFlag.setBackgroundResource(android.R.color.holo_blue_light);
        }

        if (item.isStared || item.isUnread) {
            viewUnreadFlag.setVisibility(View.VISIBLE);
        } else {
            viewUnreadFlag.setVisibility(View.INVISIBLE);
        }

        txtTitle.setText(item.title);
        txtDescription.setText(item.description);
    }
}
