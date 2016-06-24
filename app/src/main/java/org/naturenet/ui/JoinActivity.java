package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.naturenet.R;
import org.naturenet.data.model.Users;
import org.naturenet.data.model.UsersPrivate;

import java.util.Map;

public class JoinActivity extends AppCompatActivity {
    static String JOIN = "join";
    static String IDS = "ids";
    static String NAMES = "names";
    static String LAUNCH = "launch";
    static String GUEST = "guest";
    static String LOGIN = "login";
    static String NEW_USER = "new_user";
    static String USERS = "users";
    static String USERS_PRIVATE = "users-private";
    String userName, password, name, emailAddress, affiliation, error;
    String[] affiliation_ids, affiliation_names;
    Firebase fbRef;
    Spinner sp_affiliation;
    ImageButton back;
    Toolbar toolbar;
    Button join;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        back = (ImageButton) findViewById(R.id.join_back);
        sp_affiliation = (Spinner) findViewById(R.id.join_s_affiliation);
        join = (Button) findViewById(R.id.join_b_join);
        setSupportActionBar(toolbar);
        affiliation_ids = getIntent().getStringArrayExtra(IDS);
        affiliation_names = getIntent().getStringArrayExtra(NAMES);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, affiliation_names);
        sp_affiliation.setAdapter(adapter);
        sp_affiliation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                affiliation = affiliation_ids[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToLaunchFragment();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = ((EditText) findViewById(R.id.join_et_user_name)).getText().toString();
                password = ((EditText) findViewById(R.id.join_et_password)).getText().toString();
                name = ((EditText) findViewById(R.id.join_et_name)).getText().toString();
                emailAddress = ((EditText) findViewById(R.id.join_et_email_address)).getText().toString();
                if (is_any_field_empty() || is_email_address_invalid() || is_password_invalid()) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                } else {
                    Firebase.setAndroidContext(getApplicationContext());
                    fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
                    fbRef.createUser(emailAddress, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            fbRef.authWithPassword(emailAddress, password, new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(final AuthData authData) {
                                    final String id = authData.getUid().toString();
                                    String default_avatar = getResources().getString(R.string.join_default_avatar);
                                    final Users user = new Users(id, userName, affiliation, default_avatar);
                                    final UsersPrivate userPrivate = new UsersPrivate(id, name);
                                    fbRef.child(USERS).child(id).setValue(user, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            if (firebaseError == null) {
                                                fbRef.child(USERS_PRIVATE).child(id).setValue(userPrivate, new Firebase.CompletionListener() {
                                                    @Override
                                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                        if (firebaseError == null) {
                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.join_success_message), Toast.LENGTH_SHORT).show();
                                                            continueAsSignedUser(user);
                                                        } else {
                                                            Log.d("join", "Could not create record in users-private, " + firebaseError);
                                                            Toast.makeText(getApplicationContext(), "Could not create record in users-private, " + firebaseError, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d("join", "Could not create record in users, " + firebaseError);
                                                Toast.makeText(getApplicationContext(), "Could not create record in users, " + firebaseError, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_error_message_firebase_login) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.join_error_message_firebase_create) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private boolean is_any_field_empty() {
        if(userName.isEmpty() || password.isEmpty() || name.isEmpty() || emailAddress.isEmpty() || affiliation.isEmpty()) {
            error = getResources().getString(R.string.join_error_message_empty);
            return true;
        }
        return false;
    }
    private boolean is_password_invalid() {
        if (password.length() < getResources().getInteger(R.integer.password_min_length)) {
            error = getResources().getString(R.string.join_error_message_password);
            return true;
        }
        return false;
    }
    private boolean is_email_address_invalid() {
        String ePattern = "^.+@.+\\..+$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher matcher = pattern.matcher(emailAddress);
        if (matcher.matches()) {
            return false;
        }
        error = getResources().getString(R.string.join_error_message_email_address);
        return true;
    }
    public void clearResources() {
        userName = null;
        password = null;
        name = null;
        emailAddress = null;
        affiliation = null;
        error = null;
        affiliation_ids = null;
        affiliation_names = null;
        fbRef = null;
        sp_affiliation = null;
        back = null;
        toolbar = null;
        join = null;
    }
    public void goBackToLaunchFragment() {
        affiliation_ids = null;
        affiliation_names = null;
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(JOIN, LAUNCH);
        setResult(Activity.RESULT_OK, resultIntent);
        clearResources();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void continueAsGuest() {
        affiliation_ids = null;
        affiliation_names = null;
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(JOIN, GUEST);
        setResult(Activity.RESULT_OK, resultIntent);
        clearResources();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    public void continueAsSignedUser(Users createdUser) {
        affiliation_ids = null;
        affiliation_names = null;
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(JOIN, LOGIN);
        resultIntent.putExtra(NEW_USER, createdUser);
        setResult(Activity.RESULT_OK, resultIntent);
        clearResources();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}