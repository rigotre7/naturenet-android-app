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
    static String USERS = "users";
    static String ID = "id";
    static String DISPLAY_NAME = "display_name";
    static String AFFILIATION = "affiliation";
    static String AVATAR = "avatar";
    String email, password;
    Firebase fbRef;
    LoginActivity log;
    public LoginFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log = ((LoginActivity) this.getActivity());
        log.findViewById(R.id.login_b_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.setAndroidContext(log);
                fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
                email = ((EditText) log.findViewById(R.id.login_et_email_address)).getText().toString();
                password = ((EditText) log.findViewById(R.id.login_et_password)).getText().toString();
                fbRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(final AuthData authData) {
                        fbRef.child(USERS).child(authData.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Map<String, String> map = snapshot.getValue(Map.class);
                                Users loggedUser = new Users(map.get(ID), map.get(DISPLAY_NAME), map.get(AFFILIATION), map.get(AVATAR));
                                clearResources();
                                log.continueAsSignedUser(loggedUser);
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println(getResources().getString(R.string.login_error_message_firebase_read) + firebaseError.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(log, getResources().getString(R.string.login_error_message_firebase_login) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        log.findViewById(R.id.login_tv_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearResources();
                log.goToJoinActivity();
            }
        });
        log.findViewById(R.id.login_tv_forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearResources();
                log.goToForgotFragment();
            }
        });
    }
    public void clearResources() {
        email = null;
        password = null;
        fbRef = null;
    }
}