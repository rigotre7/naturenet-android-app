package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;

public class ProjectDetailFragment extends Fragment {
    static String COMPLETED = "Completed";
    TextView name, status, description, no_recent, recent;
    ImageView icon, iv_status;
    ProjectActivity p;
    public ProjectDetailFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        p = (ProjectActivity) getActivity();
        name = (TextView) p.findViewById(R.id.project_tv_name);
        status = (TextView) p.findViewById(R.id.project_tv_status);
        description = (TextView) p.findViewById(R.id.project_tv_description);
        no_recent = (TextView) p.findViewById(R.id.project_tv_no_recent_contributions);
        recent = (TextView) p.findViewById(R.id.project_tv_recent_contributions);
        icon = (ImageView) p.findViewById(R.id.project_iv_icon);
        iv_status = (ImageView) p.findViewById(R.id.project_iv_status);
        if (p.project.getName() != null)
            name.setText(p.project.getName());
        if (p.project.getStatus() != null)
            status.setText(p.project.getStatus());
        if (p.project.getDescription() != null)
            description.setText(p.project.getDescription());
        if (p.project.getIcon_url() != null)
            Picasso.with(p).load(p.project.getIcon_url()).fit().into(icon);
        if (p.project.getStatus().equals(COMPLETED))
            iv_status.setVisibility(View.VISIBLE);
        else
            iv_status.setVisibility(View.GONE);
    }
}