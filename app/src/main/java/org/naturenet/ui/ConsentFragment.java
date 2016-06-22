package org.naturenet.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.naturenet.R;

public class ConsentFragment extends Fragment {
    boolean[] consent;
    CheckBox upload, share, recording, survey;
    public ConsentFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ConsentActivity con = ((ConsentActivity) this.getActivity());
        upload = (CheckBox) con.findViewById(R.id.consent_cb_upload);
        share = (CheckBox) con.findViewById(R.id.consent_cb_share);
        recording = (CheckBox) con.findViewById(R.id.consent_cb_recording);
        survey = (CheckBox) con.findViewById(R.id.consent_cb_survey);
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
                    con.goToSignUpFragment(consent);
                } else {
                    AlertDialog.Builder required = new AlertDialog.Builder(getActivity());
                    String title = getResources().getString(R.string.alert_title);
                    String message = getResources().getString(R.string.alert_tv_message);
                    String guest = getResources().getString(R.string.alert_b_guest);
                    String dismiss = getResources().getString(R.string.alert_b_dismiss);
                    required.setTitle(title);
                    required.setIcon(R.drawable.logo_1_xxxhdpi);
                    required.setMessage(message);
                    required.setCancelable(false);
                    required.setPositiveButton(dismiss, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    required.setNegativeButton(guest, new DialogInterface.OnClickListener() {
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