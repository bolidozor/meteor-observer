package cz.expaobserver.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import cz.expaobserver.util.ActivityUtils;
import cz.expaobserver.util.Util;

public class RecordListActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, RecordListFragment.newInstance()).commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Util.Material.tintMenu(menu, getSupportActionBar().getThemedContext());
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtils.navigateUp(this, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
