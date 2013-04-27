package com.gracecode.iZhihu.Activity;

import android.os.Bundle;
import com.gracecode.iZhihu.Fragments.PreferencesList;
import com.gracecode.iZhihu.R;

public class Preference extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.preference));

        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new PreferencesList())
            .commit();
    }
}
