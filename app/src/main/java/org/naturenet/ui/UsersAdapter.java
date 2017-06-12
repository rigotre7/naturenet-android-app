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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;

import java.util.ArrayList;
import java.util.HashMap;


public class UsersAdapter extends ArrayAdapter<Users> {

    private DatabaseReference databaseReference;
    private HashMap<String, String> locations;
    private Activity mContext;
    private int resource;
    private ArrayList<Users> userList;


    public UsersAdapter(Activity activity, int res, ArrayList<Users> list) {
        super(activity, res, list);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mContext = activity;
        resource = res;
        userList = list;
        locations = new HashMap<>();
        getSiteNames();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        //if this isn't a recycled view
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.username_communities);
            holder.profilePic = (ImageView) convertView.findViewById(R.id.user_profile_pic_communities);
            holder.affiliation = (TextView) convertView.findViewById(R.id.location_communities);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        Users user = userList.get(position);

        holder.affiliation.setText(locations.get(user.affiliation));
        holder.userName.setText(user.displayName);
        if(user.avatar != null && !user.avatar.isEmpty())
            Picasso.with(mContext).load(user.avatar).placeholder(R.drawable.default_avatar).into(holder.profilePic);

        return convertView;
    }

    private void getSiteNames(){
        databaseReference.child(Site.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot site: dataSnapshot.getChildren()){
                    Site s = site.getValue(Site.class);
                    locations.put(site.getKey(), s.name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private static class ViewHolder{
        ImageView profilePic;
        TextView userName, affiliation;
    }
}

