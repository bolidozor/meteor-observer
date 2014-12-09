package cz.expaobserver.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import cz.expaobserver.util.ActivityUtils;

public class RecordListActivity extends ActionBarActivity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setHomeButtonEnabled(true);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction().add(android.R.id.content, RecordListFragment.newInstance()).commit();
    }
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
