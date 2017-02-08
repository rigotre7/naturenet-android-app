package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
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

import timber.log.Timber;

public class ObservationGalleryFragment extends Fragment {

    ObservationActivity o;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        o = (ObservationActivity) getActivity();
        gridView = (GridView) o.findViewById(R.id.observation_gallery);

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
                o.selectedObservation = (Observation) view.getTag();

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