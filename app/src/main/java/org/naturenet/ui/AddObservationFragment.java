package org.naturenet.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.naturenet.R;

public class AddObservationFragment extends Fragment {
    TextView back, send, project;
    EditText description;
    Button choose;
    ListView mProjectsListView;
    LinearLayout add_observation_ll;
    public AddObservationFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_observation, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AddObservationActivity add = ((AddObservationActivity) getActivity());
        back = (TextView) add.findViewById(R.id.toolbar_back);
        send = (TextView) add.findViewById(R.id.toolbar_send);
        project = (TextView) add.findViewById(R.id.add_observation_tv_project);
        ImageView imgView = (ImageView) add.findViewById(R.id.add_observation_iv);
        description = (EditText) add.findViewById(R.id.add_observation_et_description);
        choose = (Button) add.findViewById(R.id.add_observation_b_project);
        mProjectsListView = (ListView) add.findViewById(R.id.projects_list);
        add_observation_ll = (LinearLayout) add.findViewById(R.id.add_observation_ll);
        mProjectsListView.setAdapter(new ProjectAdapter(add, add.mProjects));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add.onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                project.setText(add.mProjects.get(position).getName());
                add_observation_ll.setVisibility(View.VISIBLE);
                mProjectsListView.setVisibility(View.GONE);
            }
        });
        if (add.imgDecodableString != null)
            imgView.setImageBitmap(BitmapFactory.decodeFile(add.imgDecodableString));
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_observation_ll.setVisibility(View.GONE);
                mProjectsListView.setVisibility(View.VISIBLE);
            }
        });
        add_observation_ll.setVisibility(View.VISIBLE);
        mProjectsListView.setVisibility(View.GONE);
    }
}