package org.naturenet.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.util.NatureNetUtils;

public class ObservationGalleryFragment extends Fragment {

    public static final String FRAGMENT_TAG = "gallery_fragment";

    MainActivity main;

    GridView gridView;
    FirebaseListAdapter mAdapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = ((MainActivity) getActivity());
        TextView toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.gallery_title);
        return inflater.inflate(R.layout.fragment_observation_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = (GridView) view.findViewById(R.id.observation_gallery);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query query = FirebaseDatabase.getInstance().getReference(Observation.NODE_NAME)
                .orderByChild("updated_at").limitToLast(20);
        mAdapter = new ObservationAdapter(getActivity(), query);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent observationIntent = new Intent(getActivity(), ObservationActivity.class);
                observationIntent.putExtra(ObservationActivity.EXTRA_OBSERVATION_ID, ((Observation)view.getTag()).id);
                startActivity(observationIntent);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                    Picasso.with(getActivity()).pauseTag(NatureNetUtils.PICASSO_TAGS.PICASSO_TAG_OBSERVATION_LIST);
                } else {
                    Picasso.with(getActivity()).resumeTag(NatureNetUtils.PICASSO_TAGS.PICASSO_TAG_OBSERVATION_LIST);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        });
    }
}