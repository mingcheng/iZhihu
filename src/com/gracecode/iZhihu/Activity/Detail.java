package com.gracecode.iZhihu.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.QuestionsDatabase;
import com.gracecode.iZhihu.Fragments.DetailFragment;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Tasks.ToggleStarTask;
import com.gracecode.iZhihu.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Detail extends BaseActivity {
    public int id;
    private DetailFragment fragQuestionDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setDisplayHomeAsUpEnabled(true);

        this.id = getIntent().getIntExtra(QuestionsDatabase.COLUM_ID, DetailFragment.ID_NOT_FOUND);
        this.fragQuestionDetail = new DetailFragment(id, this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.menu_favorite);
        item.setIcon(fragQuestionDetail.isStared() ?
            R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                ToggleStarTask toggleStarTask = new ToggleStarTask(context, new ToggleStarTask.Callback() {
                    @Override
                    public void onPostExecute(Object o) {
                        boolean isStared = fragQuestionDetail.isStared();
                        item.setIcon(isStared ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
                        String showMessage = getString(isStared ? R.string.mark_as_stared : R.string.cancel_mark_as_stared);
                        Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPreExecute() {
                    }
                });

                toggleStarTask.execute(new ToggleStarTask.Item(id, !fragQuestionDetail.isStared()));
                return true;

            case R.id.menu_view_at_zhihu:
                String url = getString(R.string.url_zhihu_questioin_pre) + fragQuestionDetail.getQuestionId();
                Util.openWithBrowser(this, url);
                return true;

            case R.id.menu_share:
                Bitmap bitmap = fragQuestionDetail.getCapture();
                try {
                    FileOutputStream fileOutPutStream = new FileOutputStream("/storage/sdcard0/izhihu.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutPutStream);

                    fileOutPutStream.flush();
                    fileOutPutStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
