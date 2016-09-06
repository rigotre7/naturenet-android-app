package org.naturenet.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.common.collect.Lists;

import org.naturenet.R;
import org.naturenet.data.model.Observation;

import java.util.List;

import timber.log.Timber;

public class ObservationGalleryFragment extends Fragment {
    ObservationActivity o;
    GridView gridView;
    ProgressDialog pd;
    List<Observation> observations;
    static String DISPLAYING_OBSERVATIONS = "Displaying Observations...";
    public ObservationGalleryFragment() {}
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
        pd.setMessage(DISPLAYING_OBSERVATIONS);
        pd.setCancelable(false);
        pd.show();
        observations = Lists.newArrayList();
        for (int i=o.observations.size()-1; i>=0; i--)
            observations.add(o.observations.get(i));
        ObservationAdapter adapter = new ObservationAdapter(o, observations, o.observers);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        while(!gridView.getAdapter().areAllItemsEnabled()) {}
        pd.dismiss();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                o.selectedObservation = observations.get(position);
                for (int i=0; i<o.observers.size(); i++) {
                    if (o.observers.get(i).getObserverId().equals(o.selectedObservation.getObserver())) {
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