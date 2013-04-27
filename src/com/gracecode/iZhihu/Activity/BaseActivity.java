package com.gracecode.iZhihu.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import com.gracecode.iZhihu.R;


public abstract class BaseActivity extends Activity {
    protected static ActionBar actionBar;
    protected static Context context;
    protected static SharedPreferences sharedPreferences;

    public BaseActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        context = getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preference:
                Intent intent = new Intent(this, Preference.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_about:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
