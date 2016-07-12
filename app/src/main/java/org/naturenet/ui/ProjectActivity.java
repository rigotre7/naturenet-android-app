package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

public class ProjectActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_PROJECT_DETAIL = "project_detail_fragment";
    static String FRAGMENT_TAG_SELECTED_OBSERVATION = "selected_observation_fragment";
    static String SIGNED_USER = "signed_user";
    static String EMPTY = "";
    Project project;
    Toolbar toolbar;
    Observation selectedObservation;
    Users signed_user;
    TextView project_back, toolbar_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        project = (Project) getIntent().getSerializableExtra("project");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        project_back = (TextView) findViewById(R.id.project_back);
        toolbar_title = (TextView) findViewById(R.id.app_bar_project_tv);
        signed_user = (Users) getIntent().getSerializableExtra(SIGNED_USER);
        setSupportActionBar(toolbar);
        toolbar.setTitle(EMPTY);
        toolbar_title.setVisibility(View.VISIBLE);
        project_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToProjectsFragment();
            }
        });
        selectedObservation = null;
        goToProjectDetailFragment();
    }
    public void goBackToProjectsFragment() {
        project = null;
        selectedObservation = null;
        Intent resultIntent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void goToProjectDetailFragment() {
        if (project.getName() != null)
            toolbar_title.setText(project.getName());
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
                addToBackStack(null).
                commit();
    }
    public void goToSelectedObservationFragment() {
        toolbar_title.setVisibility(View.GONE);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new SelectedObservationFragment(), FRAGMENT_TAG_SELECTED_OBSERVATION).
                addToBackStack(null).
                commit();
    }
    public void goBackToProjectDetailFragment() {
        selectedObservation = null;
        if (project.getName() != null)
            toolbar_title.setText(project.getName());
        toolbar_title.setVisibility(View.VISIBLE);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProjectDetailFragment(), FRAGMENT_TAG_PROJECT_DETAIL).
                addToBackStack(null).
                commit();
    }
}