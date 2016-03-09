package org.naturenet.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import org.naturenet.data.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.naturenet.R;
import org.naturenet.data.NatureNetService;

public class LoginActivity extends Activity {

    private Logger mLogger = LoggerFactory.getLogger(LoginActivity.class);
    private UiHelper mHelper;

    public static final String SIGNUP = "signup";

    private ImageView mEmailIcon;
    private EditText mEditEmail;
    private ImageView mPasswordIcon;
    private EditText mEditPassword;
    private ImageView mUsernameIcon;
    private EditText mEditUsername;
    private TextView mPasswordWarning;
    private ImageView mCheckbox;
    private boolean mIsConsent;
    private Button mBtnSignUp;
    private boolean mIsSignUp;
    private int mPasswordMinLength;

    private UserRegisterReceiver mUserRegisterReceiver = new UserRegisterReceiver();
    private UserLoginReceiver mUserLoginReceiver = new UserLoginReceiver();
    private TextView mTerms;

    private class UserRegisterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mLogger.debug("Received user registration broadcast");
            unregisterReceiver(mUserRegisterReceiver);
            mHelper.stopLoading();

            boolean status = intent.getBooleanExtra(NatureNetService.USER_STATUS, false);
            String error = intent.getStringExtra(NatureNetService.USER_ERROR);

            if (!status) {
                mHelper.alert(getString(R.string.could_not_register_user), error);
            } else {
                // TODO Registration successful - proceed
                mHelper.alert("Signup Success");
            }
        }
    }

    private class UserLoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mLogger.debug("Received user login broadcast");
            unregisterReceiver(mUserLoginReceiver);
            mHelper.stopLoading();

            boolean status = intent.getBooleanExtra(NatureNetService.USER_STATUS, false);
            String error = intent.getStringExtra(NatureNetService.USER_ERROR);

            if (!status) {
                mHelper.alert(getString(R.string.login_error), error);
            } else {
                // TODO Registration successful - proceed
                mHelper.alert("Login Success");
                onSuccess();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger.trace("Creating LoginActivity");

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_signup);

        mHelper = new UiHelper(this);
        mIsSignUp = getIntent().getBooleanExtra(SIGNUP, false);
        mPasswordMinLength = (mIsSignUp ? getResources().getInteger(R.integer.password_min_length) : 1);

        View backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mEmailIcon = (ImageView) findViewById(R.id.email_icon);
        mEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditEmail.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mEditEmail = (EditText) findViewById(R.id.email);
        mEditEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    mEmailIcon.getDrawable().setAlpha(0xff);
                } else {
                    mEmailIcon.getDrawable().setAlpha(0x7f);
                }
            }
        });
        mEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkFields();
            }
        });

        mPasswordIcon = (ImageView) findViewById(R.id.password_icon);
        mPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditPassword.requestFocus();
            }
        });
        mEditPassword = (EditText) findViewById(R.id.password);
        mEditPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    mPasswordIcon.getDrawable().setAlpha(0xff);
                } else {
                    mPasswordIcon.getDrawable().setAlpha(0x7f);
                }
            }
        });

        mUsernameIcon = (ImageView) findViewById(R.id.username_icon);
        mUsernameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditUsername.requestFocus();
            }
        });
        mEditUsername = (EditText) findViewById(R.id.username);
        mEditUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    mUsernameIcon.getDrawable().setAlpha(0xff);
                } else {
                    mUsernameIcon.getDrawable().setAlpha(0x7f);
                }
            }
        });
        mEditUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkFields();
            }
        });

        mEmailIcon.getDrawable().setAlpha(0x7f);
        mPasswordIcon.getDrawable().setAlpha(0x7f);
        mUsernameIcon.getDrawable().setAlpha(0x7f);

        mPasswordWarning = (TextView) findViewById(R.id.password_warning);
        mEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditPassword.getText().length() >= mPasswordMinLength) {
                    mPasswordWarning.setVisibility(View.GONE);
                } else {
                    mPasswordWarning.setVisibility(View.VISIBLE);
                }

                checkFields();
            }
        });

        mIsConsent = true;

        mTerms = (TextView) findViewById(R.id.terms);
        mTerms.setText(Html.fromHtml(mTerms.getText().toString()));
        mTerms.setMovementMethod(LinkMovementMethod.getInstance());

        mCheckbox = (ImageView) findViewById(R.id.checkbox);
        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsConsent = !mIsConsent;
                if (mIsConsent) {
                    mCheckbox.setImageResource(R.drawable.ic_check_box_white_24dp);
                } else {
                    mCheckbox.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                }
            }
        });

        mBtnSignUp = (Button) findViewById(R.id.sign_up);
        mBtnSignUp.setEnabled(false);

        if (!mIsSignUp) {
            TextView title = (TextView) findViewById(R.id.action_bar_title);
            title.setText(R.string.log_in);
            findViewById(R.id.username_container).setVisibility(View.GONE);
            View checkboxContainer = findViewById(R.id.checkbox_container);
            checkboxContainer.setVisibility(View.GONE);

            mTerms.setVisibility(View.GONE);

//            View usernameContainer = findViewById(R.id.username_container);
//            ViewGroup parent = (ViewGroup)usernameContainer.getParent();
//            parent.removeView(usernameContainer);
//            parent.addView(usernameContainer, 0);

            mBtnSignUp.setText(R.string.log_in);
            mPasswordWarning.setText(R.string.forgot);
            mPasswordWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO recover password
                }
            });
        } else {
            View loginButtons = findViewById(R.id.login_buttons_container);
            loginButtons.setVisibility(View.GONE);
            View loginWith = findViewById(R.id.login_with);
            loginWith.setVisibility(View.GONE);
            mTerms.setVisibility(View.VISIBLE);
        }

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsSignUp) {
                    // Login
                    IntentFilter filter = new IntentFilter(NatureNetService.ACTION_LOGIN_RESULT);
                    registerReceiver(mUserLoginReceiver, filter);

                    Intent serviceIntent = new Intent(NatureNetService.ACTION_LOGIN, null, LoginActivity.this.getApplicationContext(), NatureNetService.class);
                    serviceIntent.putExtra(NatureNetService.EMAIL, mEditEmail.getText().toString());
                    serviceIntent.putExtra(NatureNetService.USER_PASSWORD, mEditPassword.getText().toString());
                    startService(serviceIntent);

                    mHelper.loading(getString(R.string.logging_in));
                } else {
                    // Sign up
                    IntentFilter filter = new IntentFilter(NatureNetService.ACTION_REGISTER_USER_RESULT);
                    registerReceiver(mUserRegisterReceiver, filter);

                    User newUser = new User();
                    newUser.getPrivate().email = mEditEmail.getText().toString();
                    //TODO fix consent
                    newUser.getPrivate().consent = ImmutableMap.of("consent", mIsConsent);
                    newUser.getPublic().displayName = mEditUsername.getText().toString();
                    //TODO
                    newUser.getPublic().avatar = "";
                    //TODO
                    newUser.getPublic().affiliation = "";

                    Intent serviceIntent = new Intent(NatureNetService.ACTION_REGISTER_USER, null, LoginActivity.this.getApplicationContext(), NatureNetService.class);
                    serviceIntent.putExtra(NatureNetService.USER_ACCOUNT, newUser);
                    serviceIntent.putExtra(NatureNetService.EMAIL, newUser.getPrivate().getEmail());
                    serviceIntent.putExtra(NatureNetService.USER_PASSWORD, mEditPassword.getText().toString());
                    startService(serviceIntent);

                    mHelper.loading(getString(R.string.registering));
                }
            }
        });

        //TODO eh?
        Intent logoutIntent = new Intent(NatureNetService.ACTION_LOGOUT, null, getApplicationContext(), NatureNetService.class);
        startService(logoutIntent);

    }

    private void checkFields() {
        if ( (mEditEmail.getText().length() == 0)
                || (mEditPassword.getText().length() < mPasswordMinLength)
                || ((mEditUsername.getText().length() == 0) && mIsSignUp) ) {
            mBtnSignUp.setEnabled(false);
        } else {
            mBtnSignUp.setEnabled(true);
        }
    }

    public void onBackPressed(){
        //mSignInTask.pause();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mSignInTask.onActivityResult(requestCode, resultCode, data);
    }

    private void onSuccess() {
        //mSignInTask.pause();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            if (mUserRegisterReceiver != null) {
                unregisterReceiver(mUserRegisterReceiver);
            }
            if (mUserLoginReceiver != null) {
                unregisterReceiver(mUserLoginReceiver);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
