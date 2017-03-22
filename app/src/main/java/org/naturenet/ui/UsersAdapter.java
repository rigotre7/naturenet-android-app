package org.naturenet.ui;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.common.base.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;


/**
 * Created by rigot on 2/23/2017.
 */

public class UsersAdapter extends FirebaseListAdapter<Users> {

    DatabaseReference databaseReference;
    Site site;
    String location;

    /**
     * @param activity    The activity containing the ListView
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location,
     *                    using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public UsersAdapter(Activity activity, Query ref) {
        super(activity, Users.class, R.layout.communities_row_layout, ref);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    protected void populateView(View v, Users model, int position) {

        v.setTag(model);
        ImageView profilePic = (ImageView) v.findViewById(R.id.user_profile_pic_communities);
        Picasso.with(mActivity).load(Strings.emptyToNull(model.avatar)).fit().into(profilePic);
        TextView username = (TextView) v.findViewById(R.id.username_communities);
        TextView location = (TextView) v.findViewById(R.id.location_communities);

        username.setText(model.displayName);
        location.setText(getSiteName(model.affiliation));
    }

    public String getSiteName(String s){
        databaseReference.child(Site.NODE_NAME).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                site = dataSnapshot.getValue(Site.class);   //get the site information
                location = site.name;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mActivity, R.string.login_error_message_firebase_read, Toast.LENGTH_SHORT).show();
            }
        });

        return location;
    }

    @Override
    public Users getItem(int pos) {
        return super.getItem(getCount() - 1 - pos);
    }

}
