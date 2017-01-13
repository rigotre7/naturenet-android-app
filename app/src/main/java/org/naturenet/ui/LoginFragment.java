package org.naturenet.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Users;

public class LoginFragment extends Fragment {

    static String USERS = "users";
    static String SIGNING_IN = "Signing In...";

    DatabaseReference fbRef;
    String email, password;
    LoginActivity log;
    Button signIn;
    TextView join, forgotPassword;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        log = ((LoginActivity) this.getActivity());
        signIn = (Button) log.findViewById(R.id.login_b_sign_in);
        join = (TextView) log.findViewById(R.id.login_tv_join);
        forgotPassword = (TextView) log.findViewById(R.id.login_tv_forgot);
        pd = new ProgressDialog(log);
        pd.setMessage(SIGNING_IN);

        signIn.setOnClickListener(v -> {
            email = ((EditText) log.findViewById(R.id.login_et_email_address)).getText().toString();
            password = ((EditText) log.findViewById(R.id.login_et_password)).getText().toString();

            if (email.equals("")) {
                Toast.makeText(log, getResources().getString(R.string.login_error_message_empty_email), Toast.LENGTH_SHORT).show();
            } else if (password.equals("")) {
                Toast.makeText(log, getResources().getString(R.string.login_error_message_empty_password), Toast.LENGTH_SHORT).show();
            } else if (log.haveNetworkConnection()) {
                signIn.setVisibility(View.GONE);
                pd.setCancelable(false);
                pd.show();
                fbRef = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(log, task -> {
                    if (task.isSuccessful()) {
                        fbRef.child(USERS).child(task.getResult().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Users loggedUser = snapshot.getValue(Users.class);
                                log.signed_user_email = email;
                                log.signed_user_password = password;
                                pd.dismiss();
                                signIn.setVisibility(View.GONE);
                                log.continueAsSignedUser(loggedUser);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                pd.dismiss();
                                signIn.setVisibility(View.GONE);
                                Toast.makeText(log, getResources().getString(R.string.login_error_message_firebase_read) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        pd.dismiss();
                        signIn.setVisibility(View.GONE);
                        Toast.makeText(log, getResources().getString(R.string.login_error_message_firebase_login) + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(log, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        join.setOnClickListener(v -> {
            if (log.haveNetworkConnection()) {
                join.setVisibility(View.GONE);
                log.goToJoinActivity();
            } else {
                Toast.makeText(log, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPassword.setOnClickListener(v -> {
            email = ((EditText) log.findViewById(R.id.login_et_email_address)).getText().toString();
            if (Strings.isNullOrEmpty(email)) {
                Toast.makeText(LoginFragment.this.getActivity(), "Please provide the email addressed you used to join NatureNet", Toast.LENGTH_LONG).show();
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginFragment.this.getActivity(), "Check your email for instructions on resetting your password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginFragment.this.getActivity(), "There was an error handling your request. Please check the email address and try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}