package org.naturenet.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.common.collect.Lists;

import org.naturenet.R;
import org.naturenet.data.model.Observation;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class ObservationGalleryFragment extends Fragment {

    ObservationActivity o;
    GridView gridView;
    ProgressDialog pd;
    List<Observation> observations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_observation_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        o = (ObservationActivity) getActivity();
        gridView = (GridView) o.findViewById(R.id.observation_gallery);
        pd = new ProgressDialog(o);
        pd.setMessage(getString(R.string.gallery_loading_observations));
        pd.setCancelable(false);
        pd.show();

        observations = Lists.newArrayList(o.observations);
        Collections.reverse(observations);
        ObservationAdapter adapter = new ObservationAdapter(o, observations, o.observers);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);

        while(!gridView.getAdapter().areAllItemsEnabled()) {}
        pd.dismiss();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                o.selectedObservation = observations.get(position);

                for (int i = 0; i < o.observers.size(); i++) {
                    if (o.observers.get(i).getObserverId().equals(o.selectedObservation.userId)) {
                        o.selectedObserverInfo = o.observers.get(i);
                        break;
                    }
                }

                Timber.d("Observation: " + o.selectedObservation.toString());
                Timber.d("Observer Id: " + o.selectedObserverInfo.getObserverId());
                Timber.d("Observer Avatar: " + o.selectedObserverInfo.getObserverAvatar());
                Timber.d("Observer Name: " + o.selectedObserverInfo.getObserverName());
                Timber.d("Observer Affiliation: " + o.selectedObserverInfo.getObserverAffiliation());

                o.goToSelectedObservationFragment();
            }
        });
    }
}