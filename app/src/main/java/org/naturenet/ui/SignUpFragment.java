package org.naturenet.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import org.naturenet.R;
import org.naturenet.data.model.Consent;
import org.naturenet.data.model.Users;
import org.naturenet.data.model.UsersPrivate;

import java.util.Map;

public class SignUpFragment extends Fragment {
    String user_name, password, name, email, affiliation;
    String error;
    public SignUpFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ConsentActivity con = ((ConsentActivity) this.getActivity());
        final String[] affiliation_ids = con.affiliation_ids;
        final String[] affiliation_names = con.affiliation_names;
        Spinner sp_affiliation = (Spinner) con.findViewById(R.id.sign_up_s_affiliation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(con, android.R.layout.simple_spinner_dropdown_item, affiliation_names);
        sp_affiliation.setAdapter(adapter);
        sp_affiliation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                affiliation = affiliation_ids[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        con.findViewById(R.id.sign_up_tV_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.goToConsentFragment();
            }
        });
        con.findViewById(R.id.sign_up_b_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = ((EditText) con.findViewById(R.id.sign_up_eT_userName)).getText().toString();
                password = ((EditText) con.findViewById(R.id.sign_up_eT_password)).getText().toString();
                name = ((EditText) con.findViewById(R.id.sign_up_eT_name)).getText().toString();
                email = ((EditText) con.findViewById(R.id.sign_up_eT_email)).getText().toString();
                if(is_any_field_empty()) {
                    Toast.makeText(con, error, Toast.LENGTH_SHORT).show();
                } else if(!is_email_not_valid().equals("false")) {
                    Toast.makeText(con, is_email_not_valid(), Toast.LENGTH_SHORT).show();
                } else if(!is_password_not_valid().equals("false")) {
                    Toast.makeText(con, is_password_not_valid(), Toast.LENGTH_SHORT).show();
                } else {
                    Firebase.setAndroidContext(con);
                    final Firebase fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
                    fbRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            String uid = result.get("uid").toString();
                            final Users user = new Users(email, password, uid, name, user_name, affiliation, ServerValue.TIMESTAMP);
                            final UsersPrivate userPrivate = new UsersPrivate(uid, ServerValue.TIMESTAMP);
                            final Consent Consent = new Consent(con.consent);
                            fbRef.child("users").child(uid).setValue(user);
                            fbRef.child("users-private").child(uid).setValue(userPrivate);
                            fbRef.child("users-private").child(uid).child("consent").setValue(Consent);
                            Toast.makeText(con, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(con, "Failed to create: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private boolean is_any_field_empty() {
        if(user_name.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || affiliation.isEmpty()) {
            error = "Enter all the details";
            return true;
        }
        return false;
    }
    private String is_email_not_valid() {
        if(email.length()<3) return "Email Address should be atleast 3 characters long";
        if(email.length()>20) return "Email Address should be atmost 20 characters long";
        if(!email.contains("@")) return "Email Address should contain '@'";
        if(email.endsWith("@")) return "Email Address should not end with '@'";
        if(email.endsWith(".")) return "Email Address should not end with '.'";
        if(email.contains("..") || email.contains(".@") || email.contains("@.") || email.contains("._.")) return "Email Address should not contain '..' or '.@' or '@.' or '._.'";
        for(Character c: email.toCharArray())
            if(!Character.isLetter(c) && !Character.isDigit(c) && c!='_' && c!='.' && c!='@') return "Invalid Email Address";
        String[] chunks = email.split("@");
        if(chunks.length > 2) return "Email Address should contain '@' only once";
        if((chunks[0].startsWith("."))) return "Email Address should not start with '.'";
        if(!Character.isLetter(chunks[0].charAt(0))) return "Email Address should start with a letter";
        if(!chunks[1].contains(".")) return "Email Address should contain '.'";
        return "false";
    }
    private String is_password_not_valid() {
        if(password.length()<6) return "Password should be atleast 6 characters long";
        if(password.length()>20) return "Password should be atmost 20 characters long";
        for(Character c: password.toCharArray())
            if(!Character.isLetter(c) && !Character.isDigit(c)) return "Password should only contain alphabets and numbers";
        return "false";
    }
}