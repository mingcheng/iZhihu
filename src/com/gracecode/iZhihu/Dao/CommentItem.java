package com.gracecode.iZhihu.Dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-7-9
 */
public class CommentItem implements Parcelable {
    private String content = "";
    private String author = "";
    private long timeStamp = 0;
    private long id = 0;

    public CommentItem() {
        // ...
    }

    public CommentItem(Parcel parcel) {
        this.id = parcel.readLong();
        this.timeStamp = parcel.readLong();
        this.author = parcel.readString();
        this.content = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(timeStamp);
        parcel.writeString(author);
        parcel.writeString(content);
    }

    static public final Parcelable.Creator<CommentItem> CREATOR = new Parcelable.Creator<CommentItem>() {
        @Override
        public CommentItem createFromParcel(Parcel parcel) {
            return new CommentItem(parcel);
        }

        @Override
        public CommentItem[] newArray(int i) {
            return new CommentItem[i];
        }
    };

    public String getAuthor() {
        return this.author;
    }

    public String getContent() {
        return this.content;
    }

    public long getId() {
        return this.id;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
