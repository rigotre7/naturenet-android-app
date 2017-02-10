package org.naturenet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.naturenet.R;
import org.naturenet.data.model.Project;

public class ProjectActivity extends AppCompatActivity {

    static String FRAGMENT_TAG_PROJECT_DETAIL = "project_detail_fragment";
    static String PROJECT = "project";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Project project = getIntent().getParcelableExtra(PROJECT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(project.name);
        }

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ProjectDetailFragment.newInstance(project), FRAGMENT_TAG_PROJECT_DETAIL)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_down);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_down);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}