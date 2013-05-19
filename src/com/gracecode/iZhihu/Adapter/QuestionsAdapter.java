package com.gracecode.iZhihu.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.gracecode.iZhihu.Dao.Question;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-5-5
 */
public final class QuestionsAdapter extends BaseAdapter {
    private static final int MAX_DESPCRIPTION_LENGTH = 100;
    private static final int FLAG_DRAWABLE_STARED = android.R.color.holo_red_light;
    private static final int FLAG_DRAWABLE_NORMAL = android.R.color.holo_blue_light;

    private final List<Question> questions;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private static Hashtable<Integer, String> convertedDescriptions;

    public QuestionsAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
        this.layoutInflater = LayoutInflater.from(context);
        this.convertedDescriptions = new Hashtable<Integer, String>();
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
            ImageView imageViewFlag = (ImageView) convertView.findViewById(R.id.unread_flag);

            holder = new ViewHolder();
            holder.description = textViewDescription;
            holder.title = textViewTitle;
            holder.flag = imageViewFlag;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.description.setText(getConvertDescription(question));
        holder.title.setText(Util.replaceSymbol(question.title));

        ImageView flag = holder.flag;
        if (question.stared) {
            flag.setBackgroundResource(R.drawable.ic_action_star_selected);
        } else {
            flag.setBackgroundResource(R.drawable.ic_action_unread);
        }

        if (question.unread || question.stared) {
            flag.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private String getConvertDescription(Question question) {
        if (convertedDescriptions.containsKey(question.id)) {
            return convertedDescriptions.get(question.id);
        }

        String content = Html.fromHtml(question.content).toString();
        int maxLength = (content.length() > MAX_DESPCRIPTION_LENGTH) ? MAX_DESPCRIPTION_LENGTH : content.length();

        content = content.substring(0, maxLength).trim();
        content = ((question.userName.length() > 1) ?
                question.userName.trim() + context.getString(R.string.colon) + " " : "") + content;

        convertedDescriptions.put(question.id, content);
        return content;
    }


    private static class ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView flag;
    }
}
