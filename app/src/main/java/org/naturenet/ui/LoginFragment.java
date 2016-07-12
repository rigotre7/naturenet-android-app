package org.naturenet.ui;

import android.app.ProgressDialog;
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
import org.naturenet.data.model.Users;

import java.util.Map;

public class LoginFragment extends Fragment {
    static String USERS = "users";
    static String ID = "id";
    static String DISPLAY_NAME = "display_name";
    static String AFFILIATION = "affiliation";
    static String AVATAR = "avatar";
    static String BIO = "bio";
    static String LATEST_CONTRIBUTION = "latest_contribution";
    static String CREATED_AT = "created_at";
    static String UPDATED_AT = "updated_at";
    static String SIGNING_IN = "Signing In...";
    DatabaseReference fbRef;
    String email, password;
    LoginActivity log;
    ProgressDialog pd;
    public LoginFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log = ((LoginActivity) this.getActivity());
        pd = new ProgressDialog(log);
        pd.setMessage(SIGNING_IN);
        log.findViewById(R.id.login_b_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ((EditText) log.findViewById(R.id.login_et_email_address)).getText().toString();
                password = ((EditText) log.findViewById(R.id.login_et_password)).getText().toString();
                if (email.equals("")) {
                    Toast.makeText(log, getResources().getString(R.string.login_error_message_empty_email), Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(log, getResources().getString(R.string.login_error_message_empty_password), Toast.LENGTH_SHORT).show();
                } else {
                    pd.setCancelable(false);
                    pd.show();
                    fbRef = FirebaseDatabase.getInstance().getReference();
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
                                                if (map.get(ID) != null)
                                                    id = map.get(ID).toString();
                                                if (map.get(DISPLAY_NAME) != null)
                                                    display_name = map.get(DISPLAY_NAME).toString();
                                                if (map.get(AFFILIATION) != null)
                                                    affiliation = map.get(AFFILIATION).toString();
                                                if (map.get(AVATAR) != null)
                                                    avatar = map.get(AVATAR).toString();
                                                if (map.get(BIO) != null)
                                                    bio = map.get(BIO).toString();
                                                if (map.get(LATEST_CONTRIBUTION) != null)
                                                    latest_contribution = (Long) map.get(LATEST_CONTRIBUTION);
                                                if (map.get(CREATED_AT) != null)
                                                    created_at = map.get(CREATED_AT);
                                                if (map.get(UPDATED_AT) != null)
                                                    updated_at = map.get(UPDATED_AT);
                                                Users loggedUser = new Users(id, display_name, affiliation, avatar, bio, latest_contribution, created_at, updated_at);
                                                log.signed_user_email = email;
                                                log.signed_user_password = password;
                                                clearResources();
                                                pd.dismiss();
                                                log.continueAsSignedUser(loggedUser);
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                pd.dismiss();
                                                Toast.makeText(log, getResources().getString(R.string.login_error_message_firebase_read) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(log, getResources().getString(R.string.login_error_message_firebase_login) + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
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