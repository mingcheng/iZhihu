package com.gracecode.iZhihu.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;
import taobe.tec.jcc.JChineseConvertor;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-5
 */
public final class QuestionsAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_DESPCRIPTION_LENGTH = 100;
    private final List<Question> questions;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private static JChineseConvertor chineseConvertor = null;
    private final SharedPreferences sharedPreferences;
    private Typeface xinGothicTypeFace = null;
    private boolean isNeedConvertTraditionalChinese = false;

    public QuestionsAdapter(Activity activity, List<Question> questions) {
        this.context = activity;
        this.questions = questions;
        this.layoutInflater = LayoutInflater.from(activity);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        this.isNeedConvertTraditionalChinese =
                sharedPreferences.getBoolean(activity.getString(R.string.key_traditional_chinese), false);

        try {
            chineseConvertor = JChineseConvertor.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        xinGothicTypeFace = Typeface.createFromAsset(activity.getAssets(), "XinGothic.otf");
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Question getItem(int i) {
        return questions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Question question = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_question_item, null);

            TextView textViewTitle = (TextView) convertView.findViewById(R.id.title);
            TextView textViewDescription = (TextView) convertView.findViewById(R.id.description);

            if (xinGothicTypeFace != null) {
                textViewTitle.setTypeface(xinGothicTypeFace);
                textViewDescription.setTypeface(xinGothicTypeFace);
            }

            ImageView imageViewFlag = (ImageView) convertView.findViewById(R.id.unread_flag);

            holder = new ViewHolder();
            holder.description = textViewDescription;
            holder.title = textViewTitle;
            holder.flag = imageViewFlag;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String title = isNeedConvertTraditionalChinese ?
                chineseConvertor.s2t(question.getTitle()) : chineseConvertor.t2s(question.getTitle());
        holder.title.setText(Util.replaceSymbol(title));
        holder.description.setText(getConvertDescription(question));

        ImageView flag = holder.flag;
        if (question.isStared()) {
            flag.setBackgroundResource(R.drawable.ic_action_star_selected);
        } else {
            flag.setBackgroundResource(R.drawable.ic_action_unread);
        }

        if (question.isUnread() || question.isStared()) {
            flag.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    // @todo zhenghe
    private String getConvertDescription(Question question) {
        String content = Html.fromHtml(question.getContent()).toString();
        int maxLength = (content.length() > MAX_DESPCRIPTION_LENGTH) ? MAX_DESPCRIPTION_LENGTH : content.length();

        content = content.substring(0, maxLength).trim();
        content = ((question.getUserName().length() > 1) ?
                question.getUserName().trim() + context.getString(R.string.colon) + " " : "") + content;

        // 转换简繁体
        return isNeedConvertTraditionalChinese ? chineseConvertor.s2t(content) : chineseConvertor.t2s(content);
    }

    @Override
    public Filter getFilter() {
        return new KNoFilter();
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView flag;
    }
}
