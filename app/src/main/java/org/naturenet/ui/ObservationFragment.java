package org.naturenet.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Comment;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Site;
import org.naturenet.data.model.Users;
import org.naturenet.util.NatureNetUtils;

import timber.log.Timber;

public class ObservationFragment extends Fragment {

    public static final String FRAGMENT_TAG = "observation_fragment";
    private static final String ARG_OBSERVATION = "ARG_OBSERVATION";

    boolean isImageFitToScreen;

    ObservationActivity o;
    Observation observation;
    ImageView observer_avatar, observation_image, like;
    TextView observer_name, observer_affiliation, observation_timestamp, observation_text, send;
    RelativeLayout commentLayout;
    EditText comment;
    ListView lv_comments;
    LinearLayout comment_view;
    private String mObservationId;
    ViewGroup.LayoutParams params;
    private DatabaseReference mRef;
    private final ValueEventListener mObservationListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            observation = dataSnapshot.getValue(Observation.class);

            if(observation == null) {
                Timber.e("Observation %s does not exist!", mObservationId);
                return;
            }

            Picasso.with(getActivity())
                    .load(Strings.emptyToNull(observation.data.image))
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.no_image)
                    .fit()
                    .centerInside()
                    .into(observation_image);

            if (observation.data.text != null) {
                observation_text.setText(observation.data.text);
            } else {
                observation_text.setText(R.string.no_description);
            }

            observation_timestamp.setText(NatureNetUtils.toDateString(observation));

            FirebaseDatabase.getInstance().getReference(Project.NODE_NAME).child(observation.projectId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Project project = dataSnapshot.getValue(Project.class);
                            if(project != null) {
                                o.getSupportActionBar().setTitle(project.name);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.w(databaseError.toException(), "Unable to read data for project %s", observation.projectId);
                        }
                    });

            FirebaseDatabase.getInstance().getReference(Users.NODE_NAME).child(observation.userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Users user = dataSnapshot.getValue(Users.class);

                            if(user == null) {
                                Timber.e("User %s does not exist!", observation.userId);
                                return;
                            }

                            NatureNetUtils.showUserAvatar(getActivity(), observer_avatar, user.avatar);
                            if (user.displayName != null) {
                                observer_name.setText(user.displayName);
                            } else {
                                observer_name.setText(R.string.unknown_user);
                            }

                            FirebaseDatabase.getInstance().getReference(Site.NODE_NAME).child(user.affiliation)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Site site = dataSnapshot.getValue(Site.class);
                                            if (site.name != null) {
                                                observer_affiliation.setText(site.name);
                                            } else {
                                                observer_affiliation.setText(null);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Timber.w(databaseError.toException(), "Unable to read data for site %s", user.affiliation);
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.w(databaseError.toException(), "Unable to read data for user %s", observation.userId);
                        }
                    });

            if (o.signed_user != null && observation.likes != null
                    && observation.likes.containsKey(o.signed_user.id) && observation.likes.get(o.signed_user.id)) {
                like.setImageDrawable(ContextCompat.getDrawable(ObservationFragment.this.getActivity(), R.drawable.like));
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.w(databaseError.toException(), "Unable to read data for observation %s", mObservationId);
        }
    };

    public static ObservationFragment newInstance(String observationId) {
        ObservationFragment frag = new ObservationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OBSERVATION, observationId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_observation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observer_avatar = (ImageView) view.findViewById(R.id.selected_observer_avatar);
        observation_image = (ImageView) view.findViewById(R.id.selected_observation_icon);
        params = observation_image.getLayoutParams();
        like = (ImageView) view.findViewById(R.id.iv_like);
        observer_name = (TextView) view.findViewById(R.id.selected_observer_user_name);
        observer_affiliation = (TextView) view.findViewById(R.id.selected_observer_affiliation);
        observation_timestamp = (TextView) view.findViewById(R.id.selected_observeration_timestamp);
        observation_text = (TextView) view.findViewById(R.id.selected_observation_text);
        send = (TextView) view.findViewById(R.id.tv_send);
        comment = (EditText) view.findViewById(R.id.et_comment);
        lv_comments = (ListView) view.findViewById(R.id.lv_comments);
        commentLayout = (RelativeLayout) view.findViewById(R.id.rl_comment);
        comment_view = (LinearLayout) view.findViewById(R.id.scroll_view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() == null || getArguments().getString(ARG_OBSERVATION) == null) {
            Timber.e(new IllegalArgumentException(), "Tried to load ObservationFragment without an Observation argument");
            Toast.makeText(getActivity(), "No observation to display", Toast.LENGTH_SHORT).show();
            return;
        }

        o = (ObservationActivity) getActivity();
        mObservationId = getArguments().getString(ARG_OBSERVATION);

        getComments(mObservationId);

        mRef = FirebaseDatabase.getInstance().getReference(Observation.NODE_NAME).child(mObservationId);
        mRef.addValueEventListener(mObservationListener);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (o.signed_user != null) {
                    Optional<Boolean> value;

                    if (observation.likes != null && observation.likes.containsKey(o.signed_user.id) && observation.likes.get(o.signed_user.id)) {
                        like.setImageDrawable(ContextCompat.getDrawable(ObservationFragment.this.getActivity(), R.drawable.unlike));
                        value = Optional.absent();
                    } else {
                        like.setImageDrawable(ContextCompat.getDrawable(ObservationFragment.this.getActivity(), R.drawable.like));
                        value = Optional.of(true);
                    }

                    DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
                    fbRef.child("observations").child(observation.id).child("likes").child(o.signed_user.id).setValue(value.orNull());
                } else {
                    Toast.makeText(getActivity(), "Please login to like an observation.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        observation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    observation_image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    observation_image.setAdjustViewBounds(true);
                    observation_image.setLayoutParams(params);
                    o.getSupportActionBar().show();
                    commentLayout.setVisibility(View.VISIBLE);
                    comment_view.setVisibility(View.VISIBLE);
                }else{
                    isImageFitToScreen=true;
                    o.getSupportActionBar().hide();
                    commentLayout.setVisibility(View.GONE);
                    comment_view.setVisibility(View.GONE);
                    observation_image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    observation_image.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = comment.getText().toString();

                if (!commentText.isEmpty()) {
                    if (o.signed_user != null) {
                        send.setEnabled(false);
                        comment.setEnabled(false);
                        final DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child(Comment.NODE_NAME).push();
                        Comment newComment = Comment.createNew(commentRef.getKey(), commentText, o.signed_user.id, observation.id, Observation.NODE_NAME);

                        commentRef.setValue(newComment, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                send.setEnabled(true);
                                comment.setEnabled(true);

                                if (databaseError != null) {
                                    Timber.w("Could not write comment for %s: %s", observation.id, databaseError.getDetails());
                                    Toast.makeText(getActivity(), "Your comment could not be submitted.", Toast.LENGTH_LONG).show();
                                } else {
                                    // Update /observations/<observation-id>/comments/ with new comment
                                    FirebaseDatabase.getInstance().getReference().child(Observation.NODE_NAME)
                                            .child(observation.id).child("comments").child(commentRef.getKey()).setValue(true);
                                    comment.getText().clear();
                                    Toast.makeText(getActivity(), "Your comment has been submitted.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Please login to comment.", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(loginIntent, 2);
                    }
                }
            }
        });
    }

    public void getComments(String observation){

        Query query = FirebaseDatabase.getInstance().getReference().child(Comment.NODE_NAME).orderByChild("parent").equalTo(observation);
        CommentsAdapter adapter = new CommentsAdapter(getActivity(), query);

        lv_comments.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        mRef.removeEventListener(mObservationListener);
        super.onDestroyView();
    }
}