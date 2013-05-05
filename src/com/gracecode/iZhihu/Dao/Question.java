package com.gracecode.iZhihu.Dao;

import android.os.Parcel;
import android.os.Parcelable;


public final class Question implements Parcelable {
    private final Database database;
    public int id;
    public int questionId;

    public String title;
    public String content;
    public String description;
    public String userName;
    public String updateAt;
    public boolean stared;
    public boolean unread;

    public Question(Database database) {
        this.database = database;
    }

    public boolean markAsRead() {
        int result = database.markSingleQuestionAsReaded(id);
        return (result > 1) ? true : false;
    }

    public boolean toggleStar(boolean flag) {
        int result = database.markQuestionAsStared(id, flag);
        return (result > 1) ? true : false;
    }

    public boolean isStared() {
        return database.isStared(id);
    }

    public boolean markAsStared() {
        return toggleStar(true);
    }

    public boolean markAsUnStared() {
        return toggleStar(false);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}

