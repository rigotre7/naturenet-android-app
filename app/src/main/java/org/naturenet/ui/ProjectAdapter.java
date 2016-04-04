package org.naturenet.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProjectAdapter extends ArrayAdapter<Project> implements View.OnClickListener {

    Logger mLogger = LoggerFactory.getLogger(ProjectAdapter.class);

    public ProjectAdapter(Context context, List<Project> objects) {
        super(context, R.layout.project_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.project_list_item, parent, false);

        Project project = getItem(position);
        view.setTag(project);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.project_thumbnail);
        Picasso.with(getContext()).load(project.getIconUrl()).fit().into(thumbnail);
        TextView name = (TextView) view.findViewById(R.id.project_name);
        name.setText(project.getName());

        return view;
    }

    @Override
    public void onClick(View v) {
        mLogger.debug("Clicked on project {}", v.getTag().toString());
        //TODO
    }
}
