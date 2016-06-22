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

import org.naturenet.R;
import org.naturenet.data.model.Users;
import org.naturenet.data.model.UsersPrivate;

import java.util.Map;

public class SignUpFragment extends Fragment {
    ConsentActivity con;
    String userName, password, name, emailAddress, affiliation, error;
    static String UID = "uid";
    public SignUpFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        con = ((ConsentActivity) this.getActivity());
        Spinner sp_affiliation = (Spinner) con.findViewById(R.id.sign_up_s_affiliation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(con, android.R.layout.simple_spinner_dropdown_item, con.affiliation_names);
        sp_affiliation.setAdapter(adapter);
        sp_affiliation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                affiliation = con.affiliation_ids[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        con.findViewById(R.id.sign_up_tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.goToConsentFragment();
            }
        });
        con.findViewById(R.id.sign_up_b_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = ((EditText) con.findViewById(R.id.sign_up_et_user_name)).getText().toString();
                password = ((EditText) con.findViewById(R.id.sign_up_et_password)).getText().toString();
                name = ((EditText) con.findViewById(R.id.sign_up_et_name)).getText().toString();
                emailAddress = ((EditText) con.findViewById(R.id.sign_up_et_email_address)).getText().toString();
                if (is_any_field_empty() || is_email_address_invalid() || is_password_invalid()) {
                    Toast.makeText(con, error, Toast.LENGTH_SHORT).show();
                } else {
                    Firebase.setAndroidContext(con);
                    final Firebase fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
                    fbRef.createUser(emailAddress, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            final String id = result.get(UID).toString();
                            final Users user = new Users(id, userName, affiliation);
                            final UsersPrivate userPrivate = new UsersPrivate(id, name);
                            fbRef.child("users").child(id).setValue(user, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    Log.d("signup", "onComplete block of users");
                                    if (firebaseError == null) {
                                        Log.d("signup", "Created record in users");
                                        fbRef.child("users-private").child(id).setValue(userPrivate, new Firebase.CompletionListener() {
                                            @Override
                                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                Log.d("signup", "onComplete block of users-private");
                                                if (firebaseError == null) {
                                                    Log.d("signup", "Created record in users-private");
                                                    Toast.makeText(con, getResources().getString(R.string.sign_up_success_message), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.d("signup", "Could not create record in users-private, " + firebaseError);
                                                    Toast.makeText(con, "Could not create record in users-private, " + firebaseError, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d("signup", "Could not create record in users, " + firebaseError);
                                        Toast.makeText(con, "Could not create record in users, " + firebaseError, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(con, getResources().getString(R.string.sign_up_error_message_firebase_create) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private boolean is_any_field_empty() {
        if(userName.isEmpty() || password.isEmpty() || name.isEmpty() || emailAddress.isEmpty() || affiliation.isEmpty()) {
            error = getResources().getString(R.string.sign_up_error_message_empty);
            return true;
        }
        return false;
    }
    private boolean is_password_invalid() {
        if (password.length() < getResources().getInteger(R.integer.password_min_length)) {
            error = getResources().getString(R.string.sign_up_error_message_password);
            return true;
        }
        return false;
    }
    private boolean is_email_address_invalid() {
        String ePattern = "^.+@.+\\..+$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher matcher = pattern.matcher(emailAddress);
        if (matcher.matches())
            return false;
        error = getResources().getString(R.string.sign_up_error_message_email_address);
        return true;
    }
//    private boolean is_email_address_invalid() {
//        if (email.length()<3) {
//            error = "Email Address should be atleast 3 characters long";
//            return true;
//        } else if (email.length()>20) {
//            error = "Email Address should be atmost 20 characters long";
//            return true;
//        } else if (!email.contains("@")) {
//            error = "Email Address should contain '@'";
//            return true;
//        } else if (email.endsWith("@")) {
//            error = "Email Address should not end with '@'";
//            return true;
//        } else if (email.endsWith(".")) {
//            error = "Email Address should not end with '.'";
//            return true;
//        } else if (email.contains("..") || email.contains(".@") || email.contains("@.") || email.contains("._.")) {
//            error = "Email Address should not contain '..' or '.@' or '@.' or '._.'";
//            return true;
//        }
//        for (Character c: email.toCharArray()) {
//            if (!Character.isLetter(c) && !Character.isDigit(c) && c!='_' && c!='.' && c!='@') {
//                error = "Invalid Email Address";
//                return true;
//            }
//        }
//        String[] chunks = email.split("@");
//        if (chunks.length > 2) {
//            error = "Email Address should contain '@' only once";
//            return true;
//        } else if ((chunks[0].startsWith("."))) {
//            error = "Email Address should not start with '.'";
//            return true;
//        } else if (!Character.isLetter(chunks[0].charAt(0))) {
//            error = "Email Address should start with a letter";
//            return true;
//        } else if (!chunks[1].contains(".")) {
//            error = "Email Address should contain '.'";
//            return true;
//        }
//        return false;
//    }
}