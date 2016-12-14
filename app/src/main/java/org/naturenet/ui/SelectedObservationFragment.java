package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.util.CroppedCircleTransformation;
import org.naturenet.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class SelectedObservationFragment extends Fragment {
    ProjectActivity o;
    ImageView observer_avatar, observation_image, like;
    TextView observer_name, observer_affiliation, observeration_timestamp, observeration_text, send;
    EditText comment;
    ListView lv_comments;
    private Transformation mAvatarTransform = new CroppedCircleTransformation();
    public SelectedObservationFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selected_observation, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        o = (ProjectActivity) getActivity();
        observer_avatar = (ImageView) o.findViewById(R.id.selected_observer_avatar);
        observation_image = (ImageView) o.findViewById(R.id.selected_observation_icon);
        like = (ImageView) o.findViewById(R.id.iv_like);
        observer_name = (TextView) o.findViewById(R.id.selected_observer_user_name);
        observer_affiliation = (TextView) o.findViewById(R.id.selected_observer_affiliation);
        observeration_timestamp = (TextView) o.findViewById(R.id.selected_observeration_timestamp);
        observeration_text = (TextView) o.findViewById(R.id.selected_observation_text);
        send = (TextView) o.findViewById(R.id.tv_send);
        comment = (EditText) o.findViewById(R.id.et_comment);
        lv_comments = (ListView) o.findViewById(R.id.lv_comments);
        if (o.like != null) {
            if (o.like) {
                like.setImageDrawable(o.getResources().getDrawable(R.drawable.like));
            } else {
                like.setImageDrawable(o.getResources().getDrawable(R.drawable.unlike));
            }
        } else {
            like.setImageDrawable(o.getResources().getDrawable(R.drawable.unlike));
        }
        if (o.comments != null) {
            Timber.d("Comments are available");
            for (int i=0; i<o.comments.size(); i++) {
                Timber.d("Comment" + i+1 + " : "+ o.comments.get(i).toString());
            }
        } else {
            Timber.d("Comments are not available");
        }
        Picasso.with(o).load(Strings.emptyToNull(o.selectedObservation.data.image))
                .placeholder(R.drawable.no_image).error(R.drawable.no_image).fit().centerInside().into(observation_image);
        if (o.selectedObservation.data.text != null)
            observeration_text.setText(o.selectedObservation.data.text);
        else
            observeration_text.setText("No Description");
        if (o.selectedObservation.getUpdatedAtMillis() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date date = new Date((Long) o.selectedObservation.getUpdatedAtMillis());
            observeration_timestamp.setText(sfd.format(date).toString());
        } else {
            observeration_timestamp.setText("No Timestamp");
        }
        Picasso.with(o).load(Strings.emptyToNull(o.selectedObserverInfo.getObserverAvatar()))
                .placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).fit().transform(mAvatarTransform).into(observer_avatar);
        if (o.selectedObserverInfo.getObserverName() != null) {
            observer_name.setText(o.selectedObserverInfo.getObserverName());
        } else {
            observer_name.setText("No Observer Name");
        }
        if (o.selectedObserverInfo.getObserverAffiliation() != null) {
            observer_affiliation.setText(o.selectedObserverInfo.getObserverAffiliation());
        } else {
            observer_affiliation.setText("No Observer Affiliation");
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (o.signed_user != null) {
                    Optional<Boolean> value;
                    if (o.like) {
                        o.like = false;
                        like.setImageDrawable(o.getResources().getDrawable(R.drawable.unlike));
                        value = Optional.absent();
                    } else {
                        o.like = true;
                        like.setImageDrawable(o.getResources().getDrawable(R.drawable.like));
                        value = Optional.of(true);
                    }
                    DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
                    fbRef.child("observations").child(o.selectedObservation.id).child("likes").child(o.signed_user.id).setValue(value.orNull());
                } else
                    Toast.makeText(o, "Please login to like an observation.", Toast.LENGTH_SHORT).show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = comment.getText().toString();
                if (!commentText.isEmpty()) {
                    if (o.signed_user != null) {
                        send.setVisibility(View.GONE);
                        send.setEnabled(false);
                        comment.setEnabled(false);
                        final DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child(Comment.NODE_NAME).push();
                        Comment newComment = new Comment(commentRef.getKey(), commentText, o.signed_user.id, o.selectedObservation.id, Observation.NODE_NAME);
                        commentRef.setValue(newComment, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                send.setEnabled(true);
                                comment.setEnabled(true);
                                if (databaseError != null) {
                                    send.setVisibility(View.VISIBLE);
                                    Timber.w("Could not write comment for %s: %s", o.selectedObservation.id, databaseError.getDetails());
                                    Toast.makeText(o, "Your comment could not be submitted.", Toast.LENGTH_LONG).show();
                                } else {
                                    // Update /observations/<observation-id>/comments/ with new comment
                                    FirebaseDatabase.getInstance().getReference().child(Observation.NODE_NAME)
                                            .child(o.selectedObservation.id).child("comments").child(commentRef.getKey()).setValue(true);
                                    comment.getText().clear();
                                    send.setVisibility(View.VISIBLE);
                                    Toast.makeText(o, "Your comment has been submitted.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(o, "Please login to comment.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}