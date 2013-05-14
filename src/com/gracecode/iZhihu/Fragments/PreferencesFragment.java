package com.gracecode.iZhihu.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import com.gracecode.iZhihu.Activity.About;
import com.gracecode.iZhihu.Dao.ThumbnailsDatabase;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

public class PreferencesFragment extends PreferenceFragment {
    private Activity activity;
    private PackageInfo packageInfo;
    private static ThumbnailsDatabase thumbnailsDatabase;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        this.activity = getActivity();
        this.thumbnailsDatabase = new ThumbnailsDatabase(activity);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        try {
            this.packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        markVersionNumber();
        markCacheCountsAndSize();

        setCacheEnabled(sharedPreferences.getBoolean(getString(R.string.key_enable_cache), true));
        setSaveThumbnailsEnabled(sharedPreferences.getBoolean(getString(R.string.key_share_text_only), false));
    }


    private void markVersionNumber() {
        Preference aboutPref = findPreference(getString(R.string.key_about));
        aboutPref.setSummary(
            getString(R.string.version_tail) + " " + packageInfo.versionName + "(" + packageInfo.versionCode + ")");
    }


    private void markCacheCountsAndSize() {
        Preference cachePref = findPreference(getString(R.string.key_clear_caches));

        String template = getString(R.string.clear_caches_summary);
        int cacheCount = thumbnailsDatabase.getTotalCachedCount();
        float cacheSize = thumbnailsDatabase.getTotalCachedSize() / (1024 * 1024);

        cachePref.setSummary(String.format(template, cacheCount, cacheSize));
    }

    private void setCacheEnabled(boolean status) {
        for (String key : new String[]{
            getString(R.string.key_only_wifi_cache),
            getString(R.string.key_clear_caches)
        }) {
            findPreference(key).setEnabled(status);
        }
    }

    private void setSaveThumbnailsEnabled(boolean status) {
        findPreference(getString(R.string.key_share_and_save)).setEnabled(!status);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        String key = pref.getKey();
        boolean isEnableCache = sharedPreferences.getBoolean(getString(R.string.key_enable_cache), true);
        boolean isShareTextOnly = sharedPreferences.getBoolean(getString(R.string.key_share_text_only), false);

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
        } else if (getString(R.string.key_enable_cache).equals(key)) {
            setCacheEnabled(isEnableCache);
            return true;
        } else if (getString(R.string.key_share_text_only).equals(key)) {
            setSaveThumbnailsEnabled(isShareTextOnly);
            return true;
        } else if (getString(R.string.key_clear_caches).equals(key)) {
            if (isEnableCache) {
                new AlertDialog.Builder(activity)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.really_clear_cache)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            thumbnailsDatabase.clearAll();
                            markCacheCountsAndSize();
                        }
                    })
                    .show();
            }
            return true;
        }

        return super.onPreferenceTreeClick(prefScreen, pref);
    }


    @Override
    public void onDestroy() {
        thumbnailsDatabase.close();
        super.onDestroy();
    }
}
