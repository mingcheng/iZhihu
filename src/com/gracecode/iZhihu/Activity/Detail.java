package com.gracecode.iZhihu.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gracecode.iZhihu.Dao.Database;
import com.gracecode.iZhihu.R;
import com.gracecode.iZhihu.Tasks.ToggleStarTask;
import com.gracecode.iZhihu.Util;


public class Detail extends BaseActivity {
    private int id;
    private com.gracecode.iZhihu.Fragments.Detail fragQuestionDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setDisplayHomeAsUpEnabled(true);

        this.id = getIntent().getIntExtra(Database.COLUM_ID, com.gracecode.iZhihu.Fragments.Detail.ID_NOT_FOUND);
        this.fragQuestionDetail = new com.gracecode.iZhihu.Fragments.Detail(id, this);

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
