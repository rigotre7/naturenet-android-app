package org.naturenet.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

import java.util.Map;

public class LoginFragment extends Fragment {
    static String USERS = "users";
    static String ID = "id";
    static String DISPLAY_NAME = "display_name";
    static String AFFILIATION = "affiliation";
    static String AVATAR = "avatar";
    DatabaseReference fbRef;
    String email, password;
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
                fbRef = FirebaseDatabase.getInstance().getReference();
                email = ((EditText) log.findViewById(R.id.login_et_email_address)).getText().toString();
                password = ((EditText) log.findViewById(R.id.login_et_password)).getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(log, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    fbRef.child(USERS).child(task.getResult().getUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                                            String id = null;
                                            String display_name = null;
                                            String affiliation = null;
                                            String avatar = null;
                                            String bio = null;
                                            Long latest_contribution = null;
                                            Object created_at = null;
                                            Object updated_at = null;
                                            if (map.get("id") != null)
                                                id = map.get("id").toString();
                                            if (map.get("display_name") != null)
                                                display_name = map.get("display_name").toString();
                                            if (map.get("affiliation") != null)
                                                affiliation = map.get("affiliation").toString();
                                            if (map.get("avatar") != null)
                                                avatar = map.get("avatar").toString();
                                            if (map.get("bio") != null)
                                                bio = map.get("bio").toString();
                                            if (map.get("latest_contribution") != null)
                                                latest_contribution = (Long) map.get("latest_contribution");
                                            if (map.get("created_at") != null)
                                                created_at = map.get("created_at");
                                            if (map.get("updated_at") != null)
                                                updated_at = map.get("updated_at");
                                            Users loggedUser = new Users(id, display_name, affiliation, avatar, bio, latest_contribution, created_at, updated_at);
                                            clearResources();
                                            log.continueAsSignedUser(loggedUser);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(log, getResources().getString(R.string.join_error_message_firebase_read) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(log, getResources().getString(R.string.login_error_message_firebase_login) + task.getException(), Toast.LENGTH_SHORT).show();
                                }
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