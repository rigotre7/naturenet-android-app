package org.naturenet.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;

import java.util.List;

public class ProjectDetailFragment extends Fragment {
    static String COMPLETED = "Completed";
    TextView name, status, description, no_recent, recent;
    ImageView icon, iv_status;
    ProjectActivity p;
    GridView gridView;
    ProgressDialog pd;
    List<Observation> observations;
    static String DISPLAYING_OBSERVATIONS = "Displaying Observations...";
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
        name.setText(p.project.getName());
        if (p.project.getStatus() != null) {
            status.setText(p.project.getStatus());
            if (p.project.getStatus().equals(COMPLETED))
                iv_status.setVisibility(View.VISIBLE);
            else
                iv_status.setVisibility(View.GONE);
        } else
            iv_status.setVisibility(View.GONE);
        if (p.project.getDescription() != null)
            description.setText(p.project.getDescription());
        if (p.project.getIcon_url() != null)
            Picasso.with(p).load(p.project.getIcon_url()).fit().into(icon);
        if (p.observations != null && p.observations.size() != 0) {
            recent.setVisibility(View.VISIBLE);
            no_recent.setVisibility(View.GONE);
        } else {
            no_recent.setVisibility(View.VISIBLE);
            recent.setVisibility(View.GONE);
        }
        gridView = (GridView) p.findViewById(R.id.observation_gallery);
        pd = new ProgressDialog(p);
        pd.setMessage(DISPLAYING_OBSERVATIONS);
        pd.setCancelable(false);
        pd.show();
        observations = Lists.newArrayList();
        for (int i=p.observations.size()-1; i>=0; i--)
            observations.add(p.observations.get(i));
        ObservationAdapter adapter = new ObservationAdapter(p, observations, p.observers);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        while(!gridView.getAdapter().areAllItemsEnabled()) {}
        pd.dismiss();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p.selectedObservation = observations.get(position);
                for (int i=0; i<p.observers.size(); i++) {
                    if (p.observers.get(i).getObserverId().equals(p.selectedObservation.getObserver())) {
                        p.selectedObserverInfo = p.observers.get(i);
                        break;
                    }
                }
                p.goToSelectedObservationFragment();
            }
        });
    }
}