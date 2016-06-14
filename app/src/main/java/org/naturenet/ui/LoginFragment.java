package org.naturenet.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Users;

import java.util.Map;

public class LoginFragment extends Fragment {
    public LoginFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final LoginActivity log = ((LoginActivity) this.getActivity());
        log.findViewById(R.id.login_b_signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.setAndroidContext(log);
                final Firebase fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
                String email = ((EditText) log.findViewById(R.id.login_eT_email)).getText().toString();
                String password = ((EditText) log.findViewById(R.id.login_eT_password)).getText().toString();
                fbRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(final AuthData authData) {
                        fbRef.child("users").child(authData.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Map<String, String> map = snapshot.getValue(Map.class);
                                Users loggedUser = new Users(map.get("email"), map.get("password"), map.get("uid"), map.get("full_name"), map.get("display_name"), map.get("affiliation"), map.get("avatar"));
                                log.continueAsSignedUser(loggedUser);
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println("The read failed: " + firebaseError.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(log, "Login Unsuccessful: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        log.findViewById(R.id.login_tV_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.goToConsentActivity();
            }
        });
        log.findViewById(R.id.login_tV_forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.goToForgotFragment();
            }
        });
    }
}