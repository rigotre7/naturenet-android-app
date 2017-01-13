package org.naturenet.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.naturenet.util.CroppedCircleTransformation;
import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.ObserverInfo;

import java.util.List;

public class ObservationAdapter extends ArrayAdapter<Observation> {

    private static Transformation mAvatarTransform = new CroppedCircleTransformation();

    private List<ObserverInfo> observers;

    public ObservationAdapter(Context context, List<Observation> observations, List<ObserverInfo> observers) {
        super(context, R.layout.observation_list_item, observations);
        this.observers = observers;
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(R.layout.observation_list_item, parent, false);
        }

        final TextView observer_user_name, observer_affiliation;
        final ImageView observer_avatar, observation_icon;
        observation_icon = (ImageView) view.findViewById(R.id.observation_icon);
        observer_avatar = (ImageView) view.findViewById(R.id.observer_avatar);
        observer_user_name = (TextView) view.findViewById(R.id.observer_user_name);
        observer_affiliation = (TextView) view.findViewById(R.id.observer_affiliation);
        final Observation observation = getItem(position);

        if (observation != null) {
            view.setTag(observation);
            Picasso.with(getContext()).load(Strings.emptyToNull(observation.data.image))
                    .error(R.drawable.no_image).fit().centerCrop().into(observation_icon);

            for (ObserverInfo observer : observers) {
                if (observer.getObserverId().equals(observation.userId)) {

                    Picasso.with(getContext()).load(Strings.emptyToNull(observer.getObserverAvatar())).transform(mAvatarTransform)
                            .placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).fit().into(observer_avatar);

                    if (observer.getObserverName() != null) {
                        observer_user_name.setText(observer.getObserverName());
                    } else {
                        observer_user_name.setText(R.string.unknown_user);
                    }

                    if (observer.getObserverAffiliation() != null) {
                        observer_affiliation.setText(observer.getObserverAffiliation());
                    } else {
                        observer_affiliation.setText(null);
                    }

                    break;
                }
            }
        }
        return view;
    }
}