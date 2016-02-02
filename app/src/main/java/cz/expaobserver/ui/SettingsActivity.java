package cz.expaobserver.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import cz.expaobserver.util.ActivityUtils;
import cz.expaobserver.util.Util;

public class SettingsActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
//    ab.setIcon(((ObserverApplication)getApplication()).getTintedIcon());
//    ab.setIcon(new ColorDrawable(0x00000000));
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(android.R.id.content, SettingsFragment.newInstance()).commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Util.Material.tintMenu(menu, getSupportActionBar().getThemedContext());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            ActivityUtils.navigateUp(this, true);
            return true;
        }

        return false;
    }

}
