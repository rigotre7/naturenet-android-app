package org.naturenet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.naturenet.data.model.Users;
import org.naturenet.R;

public class LoginActivity extends AppCompatActivity {
    static String FRAGMENT_TAG_LOGIN = "login_fragment";
    static String FRAGMENT_TAG_FORGOT = "forgot_fragment";
    static String KEY_SIGNIN = "key_signin";
    static String KEY_JOIN = "key_join";
    static String EMPTY = "";
    static String JOIN = "join";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        goToLoginFragment();
    }
    public void continueAsSignedUser(Users user) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        String[] signed_user = {user.getmId(), user.getmDisplayName(), user.getmAffiliation()};
        resultIntent.putExtra(KEY_SIGNIN, signed_user);
        resultIntent.putExtra(KEY_JOIN, EMPTY);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    public void goToLoginFragment() {
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new LoginFragment(), FRAGMENT_TAG_LOGIN).
                addToBackStack(null).
                commit();
    }
    public void goToForgotFragment() {
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ForgotFragment(), FRAGMENT_TAG_FORGOT).
                addToBackStack(null).
                commit();
    }
    public void goToConsentActivity() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(KEY_JOIN, JOIN);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
}