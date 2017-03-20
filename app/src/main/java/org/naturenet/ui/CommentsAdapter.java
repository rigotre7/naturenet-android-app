package org.naturenet.ui;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Users;

/**
 * Created by rigot on 3/15/2017.
 */

public class CommentsAdapter extends FirebaseListAdapter<Comment> {

    TextView commenter, comment;
    Query query;

    public CommentsAdapter(Activity activity, Query ref) {
        super(activity, Comment.class, R.layout.comments_row_layout, ref);
        query = ref;
    }

    @Override
    protected void populateView(View v, Comment c, int position) {
        v.setTag(c);

        commenter = (TextView) v.findViewById(R.id.commenter_name);
        comment = (TextView) v.findViewById(R.id.comment);

        //set the comments
        setCommenter(c.commenter, commenter);
        comment.setText(c.comment);
    }


    /*
    This method sets the commenter for each specific comment.
     */
    private void setCommenter(String commenterID, final TextView textView){
        FirebaseDatabase.getInstance().getReference().child(Users.NODE_NAME).child(commenterID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users u = dataSnapshot.getValue(Users.class);
                textView.setText(u.displayName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
