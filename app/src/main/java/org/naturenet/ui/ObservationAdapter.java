package org.naturenet.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.ObserverInfo;

import java.util.List;

public class ObservationAdapter extends ArrayAdapter<Observation> {
    static String NO_DISPLAY_NAME = "No Display Name";
    static String NO_AFFILIATION = "No Affiliation";
    List<Observation> observations;
    List<ObserverInfo> observers;
    public ObservationAdapter(Context context, List<Observation> observations, List<ObserverInfo> observers) {
        super(context, R.layout.observation_list_item, observations);
        this.observations = observations;
        this.observers = observers;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.observation_list_item, parent, false);
        final TextView observer_user_name, observer_affiliation;
        final ImageView observer_avatar, observation_icon;
        observation_icon = (ImageView) view.findViewById(R.id.observation_icon);
        observer_avatar = (ImageView) view.findViewById(R.id.observer_avatar);
        observer_user_name = (TextView) view.findViewById(R.id.observer_user_name);
        observer_affiliation = (TextView) view.findViewById(R.id.observer_affiliation);
        final Observation observation = getItem(position);
        view.setTag(observation);
        if (observation.getData().getImage() != null)
            Picasso.with(getContext()).load(observation.getData().getImage()).fit().into(observation_icon, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    for (int i=0; i<observers.size(); i++) {
                        if (observers.get(i).getObserverId().equals(observation.getObserver())) {
                            ObserverInfo observer = observers.get(i);
                            if (observer.getObserverAvatar() != null) {
                                Picasso.with(getContext()).load(Strings.emptyToNull(observer.getObserverAvatar()))
                                        .placeholder(R.drawable.default_avatar).fit().into(observer_avatar, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        observer_avatar.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) observer_avatar.getDrawable()).getBitmap()));
                                    }
                                    @Override
                                    public void onError() {
                                        observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_avatar)));
                                    }
                                });
                            } else {
                                observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_avatar)));
                            }
                            if (observer.getObserverName() != null)
                                observer_user_name.setText(observer.getObserverName());
                            else
                                observer_user_name.setText(NO_DISPLAY_NAME);
                            if (observer.getObserverAffiliation() != null)
                                observer_affiliation.setText(observer.getObserverAffiliation());
                            else
                                observer_affiliation.setText(NO_AFFILIATION);
                            break;
                        }
                    }
                }
                @Override
                public void onError() {
                    observation_icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.no_image));
                    for (int i=0; i<observers.size(); i++) {
                        if (observers.get(i).getObserverId().equals(observation.getObserver())) {
                            ObserverInfo observer = observers.get(i);
                            if (observer.getObserverAvatar() != null) {
                                Picasso.with(getContext()).load(observer.getObserverAvatar()).fit().into(observer_avatar, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        observer_avatar.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) observer_avatar.getDrawable()).getBitmap()));
                                    }
                                    @Override
                                    public void onError() {
                                        observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_avatar)));
                                    }
                                });
                            } else {
                                observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_avatar)));
                            }
                            if (observer.getObserverName() != null)
                                observer_user_name.setText(observer.getObserverName());
                            else
                                observer_user_name.setText(NO_DISPLAY_NAME);
                            if (observer.getObserverAffiliation() != null)
                                observer_affiliation.setText(observer.getObserverAffiliation());
                            else
                                observer_affiliation.setText(NO_AFFILIATION);
                            break;
                        }
                    }
                }
            });
        else {
            observation_icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.no_image));
            for (int i=0; i<observers.size(); i++) {
                if (observers.get(i).getObserverId().equals(observation.getObserver())) {
                    ObserverInfo observer = observers.get(i);
                    if (observer.getObserverAvatar() != null) {
                        Picasso.with(getContext()).load(observer.getObserverAvatar()).fit().into(observer_avatar, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                observer_avatar.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) observer_avatar.getDrawable()).getBitmap()));
                            }
                            @Override
                            public void onError() {
                                observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_avatar)));
                            }
                        });
                    } else {
                        observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_avatar)));
                    }
                    if (observer.getObserverName() != null)
                        observer_user_name.setText(observer.getObserverName());
                    else
                        observer_user_name.setText(NO_DISPLAY_NAME);
                    if (observer.getObserverAffiliation() != null)
                        observer_affiliation.setText(observer.getObserverAffiliation());
                    else
                        observer_affiliation.setText(NO_AFFILIATION);
                    break;
                }
            }
        }
        return view;
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