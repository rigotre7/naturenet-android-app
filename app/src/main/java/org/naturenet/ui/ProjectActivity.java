package org.naturenet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

public class ProjectActivity extends AppCompatActivity {

    static String FRAGMENT_TAG_PROJECT_DETAIL = "project_detail_fragment";
    static String SIGNED_USER = "signed_user";
    static String PROJECT = "project";
    static String EMPTY = "";

    Project project;
    Toolbar toolbar;
    Users signed_user;
    TextView project_back, toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project);
        project = getIntent().getParcelableExtra(PROJECT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        project_back = (TextView) findViewById(R.id.project_back);
        toolbar_title = (TextView) findViewById(R.id.app_bar_project_tv);
        signed_user = getIntent().getParcelableExtra(SIGNED_USER);

        setSupportActionBar(toolbar);
        toolbar.setTitle(EMPTY);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(project.name);

        project_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ProjectDetailFragment.newInstance(project), FRAGMENT_TAG_PROJECT_DETAIL)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}