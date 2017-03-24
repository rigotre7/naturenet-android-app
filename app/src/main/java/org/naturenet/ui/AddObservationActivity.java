package org.naturenet.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.common.collect.Lists;

import org.naturenet.R;
import org.naturenet.UploadService;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Users;

public class AddObservationActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_PATH = "observation_path";
    public static final String EXTRA_USER = "signed_user";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";

    Uri observationPath;
    Observation newObservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.add_observation_title);
        }

        newObservation = new Observation();
        double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0);
        double lon = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0);
        newObservation.location = Lists.newArrayList(lat, lon);
        Users user = getIntent().getParcelableExtra(EXTRA_USER);
        if (user != null) {
            newObservation.userId = user.id;
            newObservation.siteId = user.affiliation;
        }
        observationPath = getIntent().getParcelableExtra(EXTRA_IMAGE_PATH);

        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new AddObservationFragment(), AddObservationFragment.FRAGMENT_TAG).
                commit();
    }

    public void submitObservation() {
        findViewById(R.id.toolbar_send).setVisibility(View.GONE);
        findViewById(R.id.toolbar_busy).setVisibility(View.VISIBLE);

        Intent uploadIntent = new Intent(this, UploadService.class);
        uploadIntent.putExtra(UploadService.EXTRA_OBSERVATION, newObservation);
        uploadIntent.putExtra(UploadService.EXTRA_URI_PATH, observationPath);
        startService(uploadIntent);

        finish();
    }
}