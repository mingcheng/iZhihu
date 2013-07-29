package com.gracecode.iZhihu.activity;

import android.os.Bundle;
import com.gracecode.iZhihu.fragment.PreferencesFragment;
import com.gracecode.iZhihu.R;

public class Preference extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.preference));

        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new PreferencesFragment())
            .commit();
    }
}
