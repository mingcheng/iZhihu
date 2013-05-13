package com.gracecode.iZhihu.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Fragments.DetailFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.io.File;

public class Detail extends BaseActivity {
    public int id;
    private DetailFragment fragQuestionDetail;
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


    /**
     * 更新 ActionBar 的收藏图标，并返回状态
     */
    private boolean updateStartIcon() {
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

        this.id = getIntent().getIntExtra(QuestionsDatabase.COLUM_ID, DetailFragment.ID_NOT_FOUND);
        if (this.id == DetailFragment.ID_NOT_FOUND) {
            finish();
        } else {
            this.fragQuestionDetail = new DetailFragment(id, this);
        }

        PowerManager powerManager = ((PowerManager) getSystemService(POWER_SERVICE));
        this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, Detail.class.getName());

        actionBar.setDisplayHomeAsUpEnabled(true);
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, fragQuestionDetail)
            .commit();
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (isNeedScreenWakeLock()) {
            wakeLock.acquire();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isNeedScreenWakeLock()) {
            wakeLock.release();
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
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File screenShotFile = new File(pictureDirectory, fragQuestionDetail.getQuestionId() + ".png");
                File tempScreenShotsFile = fragQuestionDetail.getTempScreenShotsFile();

                try {
                    if (fragQuestionDetail.isTempScreenShotsFileCached()) {
                        Util.copyFile(tempScreenShotsFile, screenShotFile);
                        Util.openShareIntentWithImage(this, fragQuestionDetail.getShareString(), Uri.fromFile(screenShotFile));
                    } else {
                        Util.openShareIntentWithPlainText(this, fragQuestionDetail.getShareString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
