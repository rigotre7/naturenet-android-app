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
    static String LOGIN = "login";
    static String GUEST = "guest";
    static String JOIN = "join";
    static String SIGNED_USER = "signed_user";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        goToLoginFragment();
    }
    public void continueAsSignedUser(Users signed_user) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(LOGIN, LOGIN);
        resultIntent.putExtra(SIGNED_USER, signed_user);
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
    public void goToJoinActivity() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(LOGIN, JOIN);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
}