package com.gracecode.iZhihu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.dao.Question;
import com.gracecode.iZhihu.util.Helper;
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
    private final List<Question> mQuestions;

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    private ViewHolder mViewHolder;
    private static JChineseConvertor mChineseConvertor;
    private final SharedPreferences mSharedPreferences;

    private boolean isNeedConvertTraditionalChinese = false;
    private boolean isHideDescription = false;

    public QuestionsAdapter(Activity activity, List<Question> questions) {
        this.mContext = activity;
        this.mQuestions = questions;
        this.mLayoutInflater = LayoutInflater.from(activity);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        this.isNeedConvertTraditionalChinese =
                mSharedPreferences.getBoolean(activity.getString(R.string.key_traditional_chinese), false);

        try {
            mChineseConvertor = JChineseConvertor.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mQuestions.size();
    }

    @Override
    public Question getItem(int i) {
        return mQuestions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Question question = getItem(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.listview_question_item, null);

            TextView textViewTitle = (TextView) convertView.findViewById(R.id.title);
            TextView textViewDescription = (TextView) convertView.findViewById(R.id.description);

            // @see https://twitter.com/wuketidai/status/362225046728609792
            textViewTitle.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            ImageView imageViewFlag = (ImageView) convertView.findViewById(R.id.unread_flag);

            mViewHolder = new ViewHolder();
            mViewHolder.description = textViewDescription;
            mViewHolder.title = textViewTitle;
            mViewHolder.flag = imageViewFlag;

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String title = isNeedConvertTraditionalChinese ?
                mChineseConvertor.s2t(question.getTitle()) : mChineseConvertor.t2s(question.getTitle());
        mViewHolder.title.setText(Helper.replaceSymbol(title));
        mViewHolder.description.setText(getConvertDescription(question));

        ImageView flag = mViewHolder.flag;
        if (question.isUnread()) {
            flag.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.INVISIBLE);
        }

        if (question.isStared()) {
            flag.setBackgroundResource(R.drawable.ic_action_star_selected);
            flag.setVisibility(View.VISIBLE);
        } else {
            flag.setBackgroundResource(R.drawable.ic_action_unread);
        }

        if (isHideDescription) {
            mViewHolder.description.setVisibility(View.GONE);
        } else {
            mViewHolder.description.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void setHideDescription(boolean flag) {
        isHideDescription = flag;
    }

    // @todo zhenghe
    private String getConvertDescription(Question question) {
        String content = Html.fromHtml(question.getContent()).toString();
        int maxLength = (content.length() > MAX_DESPCRIPTION_LENGTH) ? MAX_DESPCRIPTION_LENGTH : content.length();

        content = content.substring(0, maxLength).trim();
        content = ((question.getUserName().length() > 1) ?
                question.getUserName().trim() + mContext.getString(R.string.colon) + " " : "") + content;

        // 转换简繁体
        return isNeedConvertTraditionalChinese ? mChineseConvertor.s2t(content) : mChineseConvertor.t2s(content);
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
