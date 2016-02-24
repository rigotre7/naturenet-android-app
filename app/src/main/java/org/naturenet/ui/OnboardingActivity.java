package org.naturenet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.naturenet.R;

public class OnboardingActivity extends Activity {
    private static final int REQUEST_CODE_SIGNUP = 0x10000;
    private static final int REQUEST_CODE_LOGIN = 0x10001;

    public static final String LOGIN = "login";
    public static final String SHOW_SKIP = "show_skip";

    private UiHelper mHelper;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mPrefEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.onboarding);

        Intent intent = getIntent();

        Boolean shouldLogin = false;
        Boolean showSkip = false;
        if (savedInstanceState == null) {
            shouldLogin = intent.getBooleanExtra(LOGIN, false);
            showSkip = intent.getBooleanExtra(SHOW_SKIP, false);
        }

        mHelper = new UiHelper(this);
        mPreferences = getSharedPreferences("NatureNetPreferences", MODE_PRIVATE);
        mPrefEditor = mPreferences.edit();

        View closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        View signUpWithEmail = findViewById(R.id.sign_up_with_email);
        signUpWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mSignInTask.pause();

                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.SIGNUP, true);
                startActivityForResult(intent, REQUEST_CODE_SIGNUP);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        View login = (View) findViewById(R.id.login_with_email);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mSignInTask.pause();

                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.SIGNUP, false);
                startActivityForResult(intent, REQUEST_CODE_LOGIN);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Button skip = (Button) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mSignInTask.pause();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        if (!showSkip) {
            skip.setVisibility(View.INVISIBLE);
        } else {
            // Don't show both skip and the X (close) buttons together
            closeButton.setVisibility(View.INVISIBLE);
        }

        View loginWithGoogle = findViewById(R.id.login_with_gplus);
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_LONG).show();
                    return;
                }
                //mSignInTask.signIn(INaturalistService.LoginType.GOOGLE, null, null);
            }
        });

        //mSignInTask = new SignInTask(this, this, mFacebookLoginButton);

        if (shouldLogin) {
            // Show login screen
            login.performClick();
        }

    }

    //@Override
    public void onLoginSuccessful() {
        // Close this screen
        //mSignInTask.pause();
        setResult(RESULT_OK);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mSignInTask.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_LOGIN) || (requestCode == REQUEST_CODE_SIGNUP)) {
            if (resultCode == RESULT_OK) {
                // Successfully registered / logged-in from the sub-activity we've opened
                //mSignInTask.pause();
                setResult(RESULT_OK);
                finish();
            } else {
                //mSignInTask.resume();
            }
        }
    }

    public void onBackPressed(){
        //mSignInTask.pause();
        setResult(RESULT_CANCELED);
        finish();
    }

}