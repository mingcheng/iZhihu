package com.gracecode.iZhihu.Activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import com.gracecode.iZhihu.Fragments.FavoritesList;
import com.gracecode.iZhihu.Fragments.QuestionsList;
import com.gracecode.iZhihu.Listener.MainTabListener;
import com.gracecode.iZhihu.R;

public class Main extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab mainTab = actionBar.newTab()
            .setText(getString(R.string.tab_index))
            .setTabListener(new MainTabListener(context, QuestionsList.class.getName()));

        ActionBar.Tab favoritesTab = actionBar.newTab()
            .setText(getString(R.string.tab_favorite))
            .setTabListener(new MainTabListener(context, FavoritesList.class.getName()));

        actionBar.addTab(mainTab);
        actionBar.addTab(favoritesTab);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
