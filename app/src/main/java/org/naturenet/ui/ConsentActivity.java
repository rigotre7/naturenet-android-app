package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Consent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsentActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_CONSENT = "consent_fragment";
    static String FRAGMENT_TAG_SIGNUP = "signup_fragment";
    static String KEY_CONSENT = "key_consent";
    static String LAUNCH = "launch";
    static String GUEST = "guest";
    static String SITES = "sites";
    static String ID = "id";
    static String NAME = "name";
    String[] affiliation_ids, affiliation_names;
    List<String> ids = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
    boolean[] consent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton ib = (ImageButton) findViewById(R.id.consent_ib);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToLaunchFragment();
            }
        });
        consent = new boolean[4];
        goToConsentFragment();
    }
    public boolean[] getConsent() {
        return consent;
    }
    public void setConsent(boolean[] consent) {
        this.consent = consent;
    }
    public void goToConsentFragment() {
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ConsentFragment(), FRAGMENT_TAG_CONSENT).
                addToBackStack(null).
                commit();
    }
    public void add(String id, String name) {
        ids.add(id);
        names.add(name);
    }
    public void goToSignUpFragment(boolean[] consent) {
        this.consent = consent;
        Firebase.setAndroidContext(this);
        Firebase fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
        fbRef.child(SITES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, String> map = postSnapshot.getValue(Map.class);
                    add(map.get(ID), map.get(NAME));
                }
                if (ids.size() != 0 && names.size() != 0) {
                    affiliation_ids = ids.toArray(new String[ids.size()]);
                    affiliation_names = names.toArray(new String[names.size()]);
                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.fragment_container, new SignUpFragment(), FRAGMENT_TAG_SIGNUP).
                            addToBackStack(null).
                            commit();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println(getResources().getString(R.string.sign_up_error_message_firebase_read) + firebaseError.getMessage());
            }
        });
    }
    public void goBackToLaunchFragment() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(KEY_CONSENT, LAUNCH);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void continueAsGuest() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(KEY_CONSENT, GUEST);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
}