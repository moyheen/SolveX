package com.moyheen.user.solvex.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.moyheen.user.solvex.R;
import com.moyheen.user.solvex.common.logger.Log;
import com.moyheen.user.solvex.fragments.LeaderboardFragment;
import com.moyheen.user.solvex.fragments.PlayFragment;

import static com.moyheen.user.solvex.R.id;
import static com.moyheen.user.solvex.R.layout;
import static com.moyheen.user.solvex.R.string;


public class MainActivity extends ActionBarActivity {

    private String[] titles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mCharSequence;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id.content_frame, new PlayFragment());
        transaction.commit();

        Toolbar mToolbar = (Toolbar) findViewById(id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(id.drawer_layout);
        mDrawerList = (ListView) findViewById(id.left_drawer);

        // To set the SupportActionBar;
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        titles = new String[]{"Solve X!", "Leaderboard"};

        //Set the adapter for the list_view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                layout.drawer_list_item, titles));

        // The list click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                string.drawer_open,
                string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
                syncState();
            }

        };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        mActionBarDrawerToggle.syncState();

        // To set the default fragment
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i(TAG, "I'm in Main Activity");
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new PlayFragment();
                break;
            case 1:
                fragment = new LeaderboardFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(id.content_frame, fragment).commit();
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            getSupportActionBar().setTitle(titles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in loading navigation bar fragment");
        }


    }


}
