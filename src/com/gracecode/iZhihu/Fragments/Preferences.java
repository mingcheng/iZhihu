package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import com.gracecode.iZhihu.Activity.About;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

public class Preferences extends PreferenceFragment {
    private Activity activity;
    private PackageInfo packageInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        this.activity = getActivity();
        try {
            this.packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        markVersionNumber();
    }

    private void markVersionNumber() {
        Preference aboutPref = findPreference(getString(R.string.key_about));
        aboutPref.setSummary(
            getString(R.string.version_tail) + " " + packageInfo.versionName);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        String key = pref.getKey();

        if (getString(R.string.key_donate).equals(key)) {
            Util.openWithBrowser(activity, getString(R.string.url_donate));
            return true;
        } else if (getString(R.string.feedback).equals(key)) {
            String subject =
                String.format(getString(R.string.feedback_title), getString(R.string.app_name), packageInfo.versionName);
            Util.sendMail(activity, new String[]{getString(R.string.author_email)}, subject, "");
            return true;
        } else if (getString(R.string.key_about).equals(key)) {
            Intent intent = new Intent(activity, About.class);
            startActivity(intent);
            return true;
        }

        return super.onPreferenceTreeClick(prefScreen, pref);
    }
}
