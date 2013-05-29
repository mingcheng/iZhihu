package com.gracecode.iZhihu.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Fragments.DetailFragment;
import com.gracecode.iZhihu.Fragments.ScrollDetailFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.io.File;
import java.util.ArrayList;

public class Detail extends BaseActivity implements ViewPager.OnPageChangeListener {
    public static final String INTENT_EXTRA_COLUM_ID = QuestionsDatabase.COLUM_ID;
    public static final String INTENT_EXTRA_MUTI_IDS = "mutiIds";
    private static final String TAG = Detail.class.getName();

    private DetailFragment fragQuestionDetail = null;
    private Menu menuItem;
    private PowerManager.WakeLock wakeLock;
    private static final int MESSAGE_UPDATE_START_SUCCESS = 0;
    private static final int MESSAGE_UPDATE_START_FAILD = -1;

    /**
     * 标记当前条目（未）收藏
     */
    private final Runnable MarkAsStared = new Runnable() {
        @Override
        public void run() {
            if (fragQuestionDetail.markStared(!fragQuestionDetail.isStared())) {
                UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_START_SUCCESS);
            } else {
                UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_START_FAILD);
            }
        }
    };


    private final android.os.Handler UIChangedChangedHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_START_SUCCESS:
                    Util.showShortToast(context,
                            getString(isStared() ? R.string.mark_as_starred : R.string.cancel_mark_as_stared));

                    updateMenu();
                    break;
                case MESSAGE_UPDATE_START_FAILD:
                    Util.showLongToast(context, getString(R.string.database_faild));
                    break;
            }
        }
    };
    private boolean isShareByTextOnly = false;
    private boolean isShareAndSave = true;
    private ScrollDetailFragment fragListQuestions = null;
    private boolean isSetScrolltoRead = true;
    private final ArrayList<Integer> readedQuestionsPositions = new ArrayList<>();


    private boolean isStared() {
        return (fragQuestionDetail != null) && fragQuestionDetail.isStared();
    }

    /**
     * 更新 ActionBar 的收藏图标，并返回状态
     */
    private void updateMenu() {
        if (menuItem != null) {
            menuItem.findItem(R.id.menu_favorite).setIcon(isStared() ?
                    R.drawable.ic_action_star_selected : R.drawable.ic_action_star);

            if (!Util.isExternalStorageExists() && !isShareByTextOnly) {
                menuItem.findItem(R.id.menu_share).setEnabled(false);
            }
        }
    }


    /**
     * 判断是否需要常亮屏幕
     *
     * @return boolean
     */
    private boolean isNeedScreenWakeLock() {
        return sharedPreferences.getBoolean(getString(R.string.key_wake_lock), true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 电源管理
        PowerManager powerManager = ((PowerManager) getSystemService(POWER_SERVICE));

        // 获取当权选定的条目
        int id = getIntent().getIntExtra(INTENT_EXTRA_COLUM_ID, DetailFragment.ID_NOT_FOUND);
        if (id == DetailFragment.ID_NOT_FOUND) {
            finish();
        }

        // 屏幕常亮控制
        this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, Detail.class.getName());

        // 配置项
        this.isShareByTextOnly = sharedPreferences.getBoolean(getString(R.string.key_share_text_only), false);
        this.isShareAndSave = sharedPreferences.getBoolean(getString(R.string.key_share_and_save), true);
        this.isSetScrolltoRead = sharedPreferences.getBoolean(getString(R.string.key_scroll_read), true);

        ArrayList<Integer> questionsIds = getIntent().getIntegerArrayListExtra(INTENT_EXTRA_MUTI_IDS);

        // 是否是滚动阅读
        if (isSetScrolltoRead) {
            if (questionsIds.size() > 0) {
                this.fragListQuestions = new ScrollDetailFragment(this, questionsIds, id);
            }
        } else {
            this.fragQuestionDetail = new DetailFragment(id, this);
        }

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, (isSetScrolltoRead) ? fragListQuestions : fragQuestionDetail)
                .commit();

        // ActionBar 的样式
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private File getScreenShotFile() {
        if (fragQuestionDetail == null) {
            return null;
        }

        int id = fragQuestionDetail.getQuestionId();
        if (id != DetailFragment.ID_NOT_FOUND) {
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return new File(pictureDirectory, id + ".png");
        } else {
            return null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // 弱化 Navigation Bar
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        if (isNeedScreenWakeLock()) {
            wakeLock.acquire();
        }

        // 是否保留分享时用的图片
        File screenshots = getScreenShotFile();
        if (!isShareByTextOnly && !isShareAndSave && screenshots != null && screenshots.exists()) {
            try {
                screenshots.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isNeedScreenWakeLock()) {
            wakeLock.release();
        }

        if (isSetScrolltoRead) {
            for (Integer i : readedQuestionsPositions) {
                fragListQuestions.getDetailFragment(i).markReaded();
            }
        }

        if (fragQuestionDetail != null) {
            fragQuestionDetail.markReaded();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        menuItem = menu;
        updateMenu();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Toggle star
            case R.id.menu_favorite:
                new Thread(MarkAsStared).start();
                return true;

            // View question via zhihu.com
            case R.id.menu_view_at_zhihu:
                String url =
                        String.format(
                                getString(R.string.url_zhihu_questioin_pre),
                                fragQuestionDetail.getQuestionId(), fragQuestionDetail.getAnswerId());

                Util.openWithBrowser(this, url);
                return true;

            // Share question by intent
            case R.id.menu_share:
                File screenShotFile = getScreenShotFile();
                String shareString = fragQuestionDetail.getShareString();

                try {
                    // @todo 优化这段代码的逻辑
                    if (!isShareByTextOnly) {
                        if (!screenShotFile.exists() && fragQuestionDetail.isTempScreenShotsFileCached()) {
                            Util.copyFile(fragQuestionDetail.getTempScreenShotsFile(), screenShotFile);
                        }

                        if (screenShotFile.exists()) {
                            Util.openShareIntentWithImage(this, shareString, Uri.fromFile(screenShotFile));
                        } else {
                            Util.openShareIntentWithPlainText(this, shareString);
                        }
                    } else {
                        Util.openShareIntentWithPlainText(this, shareString);
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateCurrentQuestion(DetailFragment fragment) {
        this.fragQuestionDetail = fragment;
        updateMenu();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        DetailFragment fragment = fragListQuestions.getCurrentDetailFragment();
        if (fragment != null) {
            readedQuestionsPositions.add(i);
            updateCurrentQuestion(fragment);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
