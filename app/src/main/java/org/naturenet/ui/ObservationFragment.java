package org.naturenet.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.naturenet.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class ObservationFragment extends Fragment {
    ObservationActivity o;
    ImageView observer_avatar, observation_image, like;
    TextView observer_name, observer_affiliation, observeration_timestamp, observeration_text, send;
    EditText comment;
    ListView lv_comments;
    public ObservationFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_observation, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        o = (ObservationActivity) getActivity();
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
        if (o.selectedObservation.getData().getImage() != null) {
            Picasso.with(o).load(Strings.emptyToNull(o.selectedObservation.getData().getImage())).fit().into(observation_image, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onError() {
                    observation_image.setImageDrawable(o.getResources().getDrawable(R.drawable.no_image));
                }
            });
        } else {
            observation_image.setImageDrawable(o.getResources().getDrawable(R.drawable.no_image));
        }
        if (o.selectedObservation.getData().getText() != null)
            observeration_text.setText(o.selectedObservation.getData().getText());
        else
            observeration_text.setText("No Description");
        if (o.selectedObservation.getUpdated_at() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date date = new Date((Long) o.selectedObservation.getUpdated_at());
            observeration_timestamp.setText(sfd.format(date).toString());
        } else {
            observeration_timestamp.setText("No Timestamp");
        }
        if (o.selectedObserverInfo.getObserverAvatar() != null) {
            Picasso.with(o).load(Strings.emptyToNull(o.selectedObserverInfo.getObserverAvatar()))
                    .placeholder(R.drawable.default_avatar).fit().into(observer_avatar, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    observer_avatar.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) observer_avatar.getDrawable()).getBitmap()));
                }
                @Override
                public void onError() {
                    observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(o.getResources(), R.drawable.default_avatar)));
                }
            });
        } else {
            observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(o.getResources(), R.drawable.default_avatar)));
        }
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
                    fbRef.child("observations").child(o.selectedObservation.getId()).child("likes").child(o.signed_user.getId()).setValue(value.orNull());
                } else
                    Toast.makeText(o, "Please login to like an observation.", Toast.LENGTH_SHORT).show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (o.signed_user != null) {

                } else {
                    Toast.makeText(o, "Please login to comment an observation.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Path path = new Path();
        path.addCircle((float) (width/2), (float) (height/2), (float) Math.min(width, (height/2)), Path.Direction.CCW);
        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
}