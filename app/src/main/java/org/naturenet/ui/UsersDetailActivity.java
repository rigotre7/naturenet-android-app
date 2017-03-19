package org.naturenet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.naturenet.R;
import org.naturenet.data.model.Users;

public class UsersDetailActivity extends AppCompatActivity {

    Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_detail);
        user = getIntent().getParcelableExtra(CommunitiesFragment.USER_EXTRA);
        goToUsersFragment();
    }

    public void goToUsersFragment(){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.user_details_container, new UsersDetailFragment(), UsersDetailFragment.FRAGMENT_TAG)
                .commit();
    }
}
