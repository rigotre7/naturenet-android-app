package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.naturenet.R;

public class ConsentActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_CONSENT = "consent_fragment";
    static String FRAGMENT_TAG_SIGNUP = "signup_fragment";
    boolean[] consent;
    String[] affiliation_ids, affiliation_names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.consent_tB)).setText(R.string.consent_title);
        ImageButton ib = (ImageButton) findViewById(R.id.consent_iB_back_appBar);
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
    public void goToSignUpFragment(boolean[] consent, String[] ids, String[] names) {
        this.consent = consent;
        this.affiliation_ids = ids;
        this.affiliation_names = names;
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new SignUpFragment(), FRAGMENT_TAG_SIGNUP).
                addToBackStack(null).
                commit();
    }
    public void goBackToLaunchFragment() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("key_consent", "launch");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void continueAsGuest() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("key_consent", "guest");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
}