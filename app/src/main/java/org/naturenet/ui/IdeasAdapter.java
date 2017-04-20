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
import org.naturenet.data.model.Idea;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;

import java.util.Collection;


/**
 * Created by rigot on 4/13/2017.
 */

public class IdeasAdapter extends FirebaseListAdapter<Idea>{

    private DatabaseReference databaseReference;
    private Users user;
    private Site site;
    private String location;
    private Collection list;
    private int likeCount = 0;
    private int dislikeCount =0;

    public IdeasAdapter(Activity activity, Query query){
        super(activity, Idea.class, R.layout.design_ideas_row_layout, query);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void populateView(View v, Idea model, int position) {
        v.setTag(model);

        likeCount=0;
        dislikeCount=0;

        ImageView profile_pic = (ImageView) v.findViewById(R.id.design_ideas_user_profile_image);
        TextView username = (TextView) v.findViewById(R.id.design_ideas_user_name);
        TextView affiliation = (TextView) v.findViewById(R.id.design_ideas_affiliation);
        TextView ideas_text = (TextView) v.findViewById(R.id.design_idea_text_row);
        TextView likes =(TextView) v.findViewById(R.id.design_idea_likes_number);
        TextView dislikes = (TextView) v.findViewById(R.id.design_ideas_dislike_number);
        TextView comments = (TextView) v.findViewById(R.id.design_ideas_comment_number);

        if(model.likes!=null){
            list =  model.likes.values();

            for(Object like : list){
                if(like.equals(true)){
                    likeCount++;
                }else{
                    dislikeCount++;
                }
            }
        }

        if(model.comments != null){
            comments.setText(String.valueOf(model.comments.size()));
        }
        likes.setText(String.valueOf(likeCount));
        dislikes.setText(String.valueOf(dislikeCount));
        ideas_text.setText(model.content);
        setUserInformation(model.submitter, profile_pic, username, affiliation);

    }

    private void setUserInformation(String id, final ImageView profile, final TextView username, final TextView aff){
        databaseReference.child(Users.NODE_NAME).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Users.class);
                Picasso.with(mActivity).load(Strings.emptyToNull(user.avatar)).placeholder(R.drawable.default_avatar).into(profile);
                username.setText(user.displayName);
                aff.setText(getSiteName(user.affiliation));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getSiteName(String s){
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
    public Idea getItem(int position) {
        return super.getItem(getCount() - 1 - position);
    }
}
