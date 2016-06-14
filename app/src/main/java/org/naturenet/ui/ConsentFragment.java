package org.naturenet.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.naturenet.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsentFragment extends Fragment {
    boolean[] consent;
    CheckBox upload, share, recording, survey;
    List<String> ids = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
    String[] affiliation_ids, affiliation_names;
    public ConsentFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ConsentActivity con = ((ConsentActivity) this.getActivity());
        upload = (CheckBox) con.findViewById(R.id.consent_cB_upload);
        share = (CheckBox) con.findViewById(R.id.consent_cB_share);
        recording = (CheckBox) con.findViewById(R.id.consent_cB_recording);
        survey = (CheckBox) con.findViewById(R.id.consent_cB_survey);
        consent = con.getConsent();
        if(consent!=null) {
            if(consent[0]) upload.setChecked(true);
            if(consent[1]) share.setChecked(true);
            if(consent[2]) recording.setChecked(true);
            if(consent[3]) survey.setChecked(true);
        }
        else consent = new boolean[4];
        con.findViewById(R.id.consent_b_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consent[0] = upload.isChecked();
                consent[1] = share.isChecked();
                consent[2] = recording.isChecked();
                consent[3] = survey.isChecked();
                if(consent[0] && consent[1]) {
                    Firebase.setAndroidContext(con);
                    Firebase fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
                    fbRef.child("sites").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Map<String, String> map = postSnapshot.getValue(Map.class);
                                ids.add(map.get("id").toString());
                                names.add(map.get("name").toString());
                            }
                            affiliation_ids = ids.toArray(new String[ids.size()]);
                            affiliation_names = names.toArray(new String[names.size()]);
                            Log.d("demo", "1 ids: " + affiliation_ids.toString());
                            Log.d("demo", "1 names: " + affiliation_names.toString());
                            con.goToSignUpFragment(consent, affiliation_ids, affiliation_names);
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                } else {
                    AlertDialog.Builder required = new AlertDialog.Builder(getActivity());
                    required.setTitle("Consent Form");
                    required.setIcon(R.drawable.logo_1_xxxhdpi);
                    required.setMessage("Please check the required fields or Continue as a guest");
                    required.setCancelable(false);
                    required.setPositiveButton(
                            "Dismiss",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    required.setNegativeButton(
                            "Continue As Guest",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    con.continueAsGuest();
                                }
                            });
                    AlertDialog alert = required.create();
                    alert.show();
                }
            }
        });
    }
}