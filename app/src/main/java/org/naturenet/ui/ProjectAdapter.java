package org.naturenet.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Project;


import java.util.ArrayList;

public class ProjectAdapter extends ArrayAdapter<Project> {

    Activity mContext;
    int resource;
    ArrayList<Project> projectsList;

    public ProjectAdapter(Activity context, int resource, ArrayList<Project> list) {
        super(context, R.layout.project_list_item, list);

        this.mContext = context;
        this.resource = resource;
        this.projectsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        //if this isn't a recycled view
        if(convertView==null){
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            holder.projectName = (TextView) convertView.findViewById(R.id.project_name);
            holder.projectIcon = (ImageView) convertView.findViewById(R.id.project_thumbnail);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        Project project = projectsList.get(position);

        holder.projectName.setText(project.name);
        Picasso.with(mContext).load(project.iconUrl).into(holder.projectIcon);


        return convertView;
    }


    private static class ViewHolder{
        ImageView projectIcon;
        TextView projectName;
    }
}