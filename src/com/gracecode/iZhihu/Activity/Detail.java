package com.gracecode.iZhihu.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Fragments.BaseListFragment;
import com.gracecode.iZhihu.Fragments.DetailFragment;
import com.gracecode.iZhihu.Fragments.ScrollDetailFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.io.File;
import java.util.ArrayList;

public class Detail extends BaseActivity implements ViewPager.OnPageChangeListener {
    public static final String INTENT_EXTRA_COLUM_ID = QuestionsDatabase.COLUM_ID;
    public static final String INTENT_EXTRA_MUTI_IDS = "mutiIds";

    public int id;
    private DetailFragment fragQuestionDetail = null;
    private MenuItem starMenuItem;
    private PowerManager.WakeLock wakeLock;
    public static final int MESSAGE_UPDATE_START_SUCCESS = 0;
    public static final int MESSAGE_UPDATE_START_FAILD = -1;


    /**
     * 标记当前条目（未）收藏
     */
    Runnable MarkAsStared = new Runnable() {
        @Override
        public void run() {
            if (fragQuestionDetail.markStar(!fragQuestionDetail.isStared())) {
                UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_START_SUCCESS);
            } else {
                UIChangedChangedHandler.sendEmptyMessage(MESSAGE_UPDATE_START_FAILD);
            }
        }
    };


    private android.os.Handler UIChangedChangedHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_START_SUCCESS:
                    boolean isStared = updateStartIcon();
                    Util.showShortToast(context,
                        getString(isStared ? R.string.mark_as_stared : R.string.cancel_mark_as_stared));
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
    private ArrayList<Integer> questionsIds = new ArrayList<Integer>();
    private int currnetPosition = BaseListFragment.SELECT_NONE;
    private boolean isSetScrolltoRead = true;

    /**
     * 更新 ActionBar 的收藏图标，并返回状态
     */
    private boolean updateStartIcon() {
        if (fragQuestionDetail == null) {
            return false;
        }

        boolean isStared = fragQuestionDetail.isStared();
        if (starMenuItem != null) {
            starMenuItem.setIcon(isStared ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
        }

        return isStared;
    }


    /**
     * 判断是否需要常亮屏幕
     *
     * @return
     */
    private boolean isNeedScreenWakeLock() {
        return sharedPreferences.getBoolean(getString(R.string.key_wake_lock), true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager powerManager = ((PowerManager) getSystemService(POWER_SERVICE));

        // 获取当权选定的条目
        this.id = getIntent().getIntExtra(INTENT_EXTRA_COLUM_ID, DetailFragment.ID_NOT_FOUND);
        if (this.id == DetailFragment.ID_NOT_FOUND) {
            finish();
        }

        // 屏幕常亮控制
        this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, Detail.class.getName());

        // ActionBar 的样式
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 配置项
        this.isShareByTextOnly = sharedPreferences.getBoolean(getString(R.string.key_share_text_only), false);
        this.isShareAndSave = sharedPreferences.getBoolean(getString(R.string.key_share_and_save), true);
        this.isSetScrolltoRead = sharedPreferences.getBoolean(getString(R.string.key_scroll_read), true);

        if (isSetScrolltoRead) {
            this.questionsIds = getIntent().getIntegerArrayListExtra(INTENT_EXTRA_MUTI_IDS);
            if (questionsIds.size() > 0) {
                this.fragListQuestions = new ScrollDetailFragment(this, questionsIds, id);
            }

            this.currnetPosition = getIntent().getIntExtra(BaseListFragment.KEY_SELECTED_POSITION,
                BaseListFragment.SELECT_NONE);
        } else {
            this.fragQuestionDetail = new DetailFragment(id, this);
        }

        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, (isSetScrolltoRead) ? fragListQuestions : fragQuestionDetail)
            .commit();
    }


    @Override
    public void onStart() {
        super.onStart();

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
            Util.savePref(sharedPreferences, BaseListFragment.KEY_SELECTED_POSITION, currnetPosition);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        starMenuItem = menu.findItem(R.id.menu_favorite);
        updateStartIcon();
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
                String url = getString(R.string.url_zhihu_questioin_pre) + fragQuestionDetail.getQuestionId();
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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateCurrentQuestion(DetailFragment fragment) {
        this.fragQuestionDetail = fragment;
        updateStartIcon();
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
        currnetPosition = i;
        DetailFragment fragment = fragListQuestions.getCurrentDetailFragment();
        if (fragment != null) {
            updateCurrentQuestion(fragment);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
