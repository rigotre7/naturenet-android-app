package org.naturenet.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.collect.Lists;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ObservationGalleryFragment extends Fragment {

    private Logger mLogger = LoggerFactory.getLogger(ObservationGalleryFragment.class);
    private GridView mGridView;
    private List<Observation> mObservations = Lists.newArrayList();
    private Firebase mFirebase = new Firebase(BuildConfig.FIREBASE_ROOT_URL);

    public ObservationGalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ObservationGalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ObservationGalleryFragment newInstance() {
        ObservationGalleryFragment fragment = new ObservationGalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_observation_gallery, container, false);

        mGridView = (GridView) root.findViewById(R.id.observation_gallery);
        readObservations();

        return root;
    }

    private void readObservations() {
        mLogger.info("Getting observations");
        mFirebase.child(Observation.NODE_NAME).orderByChild("updated_at").limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    mObservations.add(child.getValue(Observation.class));
                }
                mGridView.setAdapter(new TiledObservationAdapter(getContext(), mObservations));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mLogger.error("Failed to read Observations: {}", firebaseError);
                Toast.makeText(ObservationGalleryFragment.this.getContext(), "Could not get observations", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
