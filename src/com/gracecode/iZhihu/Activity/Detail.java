package com.gracecode.iZhihu.Activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Fragments.DetailFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Util;

import java.io.File;
import java.io.FileOutputStream;

public class Detail extends BaseActivity {
    public int id;
    private DetailFragment fragQuestionDetail;
    private MenuItem starMenuItem;
    private PowerManager.WakeLock wakeLock;

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

        if (starMenuItem != null) {
            starMenuItem.setIcon(fragQuestionDetail.isStared() ?
                R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
        }
    }


    private boolean isNeedScreenWakeLock() {
        return sharedPreferences.getBoolean(getString(R.string.key_wake_lock), true);
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
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                if (fragQuestionDetail.markStar(!fragQuestionDetail.isStared())) {
                    boolean isStared = fragQuestionDetail.isStared();
                    item.setIcon(isStared ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
                    String showMessage = getString(isStared ? R.string.mark_as_stared : R.string.cancel_mark_as_stared);
                    Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_view_at_zhihu:
                String url = getString(R.string.url_zhihu_questioin_pre) + fragQuestionDetail.getQuestionId();
                Util.openWithBrowser(this, url);
                return true;

            case R.id.menu_share:
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File imageFile = new File(path, fragQuestionDetail.getQuestion().answerId + ".png");


                // @todo 优化此处的代码
                try {
                    if (!imageFile.exists()) {
                        Bitmap bitmap = fragQuestionDetail.getCaptureBitmap();
                        if (bitmap != null) {
                            FileOutputStream fileOutPutStream = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutPutStream);
                            fileOutPutStream.flush();
                            fileOutPutStream.close();
                            bitmap.recycle();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Util.openShareIntent(this, fragQuestionDetail.getShareString(), Uri.fromFile(imageFile));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
