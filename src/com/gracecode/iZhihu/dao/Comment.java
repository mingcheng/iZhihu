package com.gracecode.iZhihu.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-7-9
 */
public class Comment implements Parcelable {
    private String content = "";
    private String author = "";
    private long timeStamp = 0;
    private long id = 0;

    public Comment() {
        // ...
    }

    public Comment(Parcel parcel) {
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

    static public final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel parcel) {
            return new Comment(parcel);
        }

        @Override
        public Comment[] newArray(int i) {
            return new Comment[i];
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
