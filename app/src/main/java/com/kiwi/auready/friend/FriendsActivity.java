package com.kiwi.auready.friend;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kiwi.auready.Injection;
import com.kiwi.auready.R;
import com.kiwi.auready.util.ActivityUtils;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    public static final int REQ_FRIENDS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setTitle(getString(R.string.friend_title));
        }

        FriendsFragment friendsFragment =
                (FriendsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (friendsFragment == null) {
            friendsFragment = FriendsFragment.newInstance();

            Bundle bundle = new Bundle();
            if(getIntent().hasExtra(FriendsFragment.EXTRA_KEY_MEMBERS)) {
                ArrayList<String> userIdOfMembers = getIntent().getStringArrayListExtra(FriendsFragment.EXTRA_KEY_MEMBERS);
                bundle.putStringArrayList(FriendsFragment.EXTRA_KEY_MEMBERS, userIdOfMembers);
            }
            friendsFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), friendsFragment, R.id.content_frame, FriendsFragment.TAG_FRIENDFRAG);
        }

        // Create Presenter
        FriendsPresenter presenter = new FriendsPresenter(
                Injection.provideUseCaseHandler(),
                friendsFragment,
                Injection.provideGetFriends(getApplicationContext()),
                Injection.provideDeleteFriend(getApplicationContext()));

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
