package com.gracecode.iZhihu.Dao;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.gracecode.iZhihu.R;


public final class Question implements Parcelable {
    private int id = 0;
    private int questionId = 0;
    private int answerId = 0;

    private String title = "";
    private String content = "";
    private String description = "";
    private String userName = "";
    private String updateAt = "";

    private boolean stared = false;
    private boolean unread = true;

    public Question() {

    }

    public Question(Parcel parcel) {
        this.id = parcel.readInt();
        this.questionId = parcel.readInt();
        this.answerId = parcel.readInt();

        this.title = parcel.readString();
        this.content = parcel.readString();
        this.description = parcel.readString();
        this.userName = parcel.readString();
        this.updateAt = parcel.readString();

        this.stared = (parcel.readInt() == 1) ? true : false;
        this.unread = (parcel.readInt() == 1) ? true : false;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(questionId);
        parcel.writeInt(answerId);

        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(description);
        parcel.writeString(userName);
        parcel.writeString(updateAt);

        parcel.writeInt(stared ? 1 : 0);
        parcel.writeInt(unread ? 1 : 0);
    }


    static public final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel parcel) {
            return new Question(parcel);
        }

        @Override
        public Question[] newArray(int i) {
            return new Question[i];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUpdateAt() {
        return this.updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean flag) {
        this.unread = flag;
    }

    public boolean isStared() {
        return stared;
    }

    public void setStared(boolean flag) {
        this.stared = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getShareString(Context context) {
        return String.format(context.getString(R.string.share_template),
                getTitle(),
                getOnlineShortUrl(getAnswerId()),
                context.getString(R.string.app_name)
        );
    }

    /**
     * 读知乎在线连接
     *
     * @param number
     * @return
     */
    private static String getOnlineShortUrl(int number) {
        String s = "", KEY = "6BCMx(0gEwTj3FbUGPe7rtKfqosmZOX2S)5IvH.zu9DdQRL41AnV8ckylhp!YNWJi";
        int l = KEY.length();

        while (number > 0) {
            int x = number % l;
            s = KEY.substring(x, x + 1) + s;
            number = (int) Math.floor(number / l);
        }

        return "http://z.ihu.im/u/" + s;
    }
}
