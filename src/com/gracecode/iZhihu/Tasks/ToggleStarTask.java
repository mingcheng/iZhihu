package com.gracecode.iZhihu.Tasks;

import android.content.Context;

public class ToggleStarTask extends BaseTasks<ToggleStarTask.Item, Void, Void> {
    public static class Item {
        public int id;
        public boolean stared;

        public Item(int id, boolean stared) {
            this.id = id;
            this.stared = stared;
        }
    }

    public ToggleStarTask(Context context, Callback callback) {
        super(context, callback);

    }

    @Override
    protected Void doInBackground(Item... items) {
        for (Item item : items) {
            synchronized (database) {
                database.markQuestionAsFavourated(item.id, item.stared);
            }
        }

        return null;
    }
}
