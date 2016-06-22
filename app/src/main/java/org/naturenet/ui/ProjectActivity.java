package org.naturenet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Project;

public class ProjectActivity extends AppCompatActivity {
    TextView name, status, description, no_recent, recent;
    ImageView icon, iv_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Project project = (Project) getIntent().getSerializableExtra("project");
        name = (TextView) findViewById(R.id.project_tv_name);
        status = (TextView) findViewById(R.id.project_tv_status);
        description = (TextView) findViewById(R.id.project_tv_description);
        no_recent = (TextView) findViewById(R.id.project_tv_no_recent_contributions);
        recent = (TextView) findViewById(R.id.project_tv_recent_contributions);
        icon = (ImageView) findViewById(R.id.project_iv_icon);
        iv_status = (ImageView) findViewById(R.id.project_iv_status);
        name.setText(project.getName());
        status.setText(project.getStatus());
        description.setText(project.getDescription());
        Picasso.with(this).load(project.getIconUrl()).fit().into(icon);
        if (project.getStatus().equals("Completed")) iv_status.setVisibility(View.VISIBLE);
        else iv_status.setVisibility(View.GONE);
    }
}