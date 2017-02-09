package org.naturenet.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.common.base.Strings;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.util.NatureNetUtils;

public class ObservationGalleryFragment extends Fragment {

    GridView gridView;
    FirebaseListAdapter mAdapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mAdapter = new FirebaseListAdapter<Observation>(getActivity(), Observation.class, R.layout.observation_list_item, query) {
            @Override
            protected void populateView(final View v, final Observation model, int position) {
                v.setTag(model);
                ViewGroup badge = (ViewGroup) v.findViewById(R.id.observation_user_badge);
                //TODO: instead of recreating a new layout, make a Badge class and clear contents individually
                badge.removeAllViews();
                NatureNetUtils.makeUserBadge(getActivity(), badge, model.userId);
                Picasso.with(getActivity()).load(Strings.emptyToNull(model.data.image)).error(R.drawable.no_image)
                        .fit().centerCrop().into((ImageView) v.findViewById(R.id.observation_icon));
            }
            @Override
            public Observation getItem(int pos) {
                return super.getItem(getCount() - 1 - pos);
            }
        };
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent observationIntent = new Intent(getActivity(), ObservationActivity.class);
                observationIntent.putExtra(ObservationActivity.OBSERVATION, (Observation)view.getTag());
                startActivity(observationIntent);
            }
        });
    }
}