package org.naturenet.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TiledObservationAdapter extends ArrayAdapter<Observation> implements View.OnClickListener {
    private Logger mLogger = LoggerFactory.getLogger(TiledObservationAdapter.class);
    public TiledObservationAdapter(Context context, List<Observation> objects) {
        super(context, R.layout.observation_tile_view, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View root = inflater.inflate(R.layout.observation_tile_view, parent, false);
        Observation obs = getItem(position);
        root.setTag(obs);
        ImageView thumbnail = (ImageView) root.findViewById(R.id.obs_preview_thumbnail);
//        Picasso.with(getContext()).load(obs.getData().getImageUrl()).fit().into(thumbnail);
        return root;
    }
    @Override
    public void onClick(View v) {
        mLogger.debug("Clicked on observation {}", v.getTag().toString());
    }
}