package naturenet.org.naturenet.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import naturenet.org.naturenet.NatureNetApp;
import naturenet.org.naturenet.R;
import naturenet.org.naturenet.data.NatureNetService;

public class LoginActivity extends Activity {

    Logger mLogger = LoggerFactory.getLogger(LoginActivity.class);
    private NatureNetApp mApp;
    private UiHelper mHelper;

    public static final String SIGNUP = "signup";

    private ImageView mEmailIcon;
    private EditText mEmail;
    private ImageView mPasswordIcon;
    private EditText mPassword;
    private ImageView mUsernameIcon;
    private EditText mUsername;
    private TextView mPasswordWarning;
    private ImageView mCheckbox;
    private boolean mUseCCLicense;
    private Button mSignup;
    private boolean mIsSignup;

    private UserRegisterReceiver mUserRegisterReceiver;
    private TextView mTerms;

    private class UserRegisterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(mUserRegisterReceiver);
            mHelper.stopLoading();

            boolean status = intent.getBooleanExtra(NatureNetService.REGISTER_USER_STATUS, false);
            String error = intent.getStringExtra(NatureNetService.REGISTER_USER_ERROR);

            if (!status) {
                mHelper.alert(getString(R.string.could_not_register_user), error);
            } else {
                // TODO Registration successful - login the user
                //recreateSignInTaskIfNeeded();
                //mSignInTask.signIn(NatureNetService.LoginType.PASSWORD, mUsername.getText().toString(), mPassword.getText().toString());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mApp = (NatureNetApp) getApplicationContext();
        setContentView(R.layout.login_signup);

        mHelper = new UiHelper(this);
        mIsSignup = getIntent().getBooleanExtra(SIGNUP, false);

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
                mEmail.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mEmail = (EditText) findViewById(R.id.email);
        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    mEmailIcon.getDrawable().setAlpha(0xff);
                } else {
                    mEmailIcon.getDrawable().setAlpha(0x7f);
                }
            }
        });
        mEmail.addTextChangedListener(new TextWatcher() {
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
                mPassword.requestFocus();
            }
        });
        mPassword = (EditText) findViewById(R.id.password);
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                mUsername.requestFocus();
            }
        });
        mUsername = (EditText) findViewById(R.id.username);
        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    mUsernameIcon.getDrawable().setAlpha(0xff);
                } else {
                    mUsernameIcon.getDrawable().setAlpha(0x7f);
                }
            }
        });
        mUsername.addTextChangedListener(new TextWatcher() {
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
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mPassword.getText().length() >= (mIsSignup ? 6 : 1)) {
                    mPasswordWarning.setVisibility(View.GONE);
                } else {
                    mPasswordWarning.setVisibility(View.VISIBLE);
                }

                checkFields();
            }
        });

        mUseCCLicense = true;

        mTerms = (TextView) findViewById(R.id.terms);
        mTerms.setText(Html.fromHtml(mTerms.getText().toString()));
        mTerms.setMovementMethod(LinkMovementMethod.getInstance());

        mCheckbox = (ImageView) findViewById(R.id.checkbox);
        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUseCCLicense = !mUseCCLicense;
                if (mUseCCLicense) {
                    mCheckbox.setImageResource(R.drawable.ic_check_box_white_24dp);
                } else {
                    mCheckbox.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                }
            }
        });

        mSignup = (Button) findViewById(R.id.sign_up);
        mSignup.setEnabled(false);

        if (!mIsSignup) {
            TextView title = (TextView) findViewById(R.id.action_bar_title);
            title.setText(R.string.log_in);
            View emailContainer = findViewById(R.id.email_container);
            emailContainer.setVisibility(View.GONE);
            View checkboxContainer = findViewById(R.id.checkbox_container);
            checkboxContainer.setVisibility(View.GONE);
            mUsername.setHint(R.string.username_or_email);

            mTerms.setVisibility(View.GONE);

            View usernameContainer = findViewById(R.id.username_container);
            ViewGroup parent = (ViewGroup)usernameContainer.getParent();
            parent.removeView(usernameContainer);
            parent.addView(usernameContainer, 0);

            mSignup.setText(R.string.log_in);
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

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsSignup) {
                    // Login
                    //recreateSignInTaskIfNeeded();
                    //mSignInTask.signIn(NatureNetService.LoginType.PASSWORD, mUsername.getText().toString(), mPassword.getText().toString());
                } else {
                    // Sign up
                    mUserRegisterReceiver = new UserRegisterReceiver();
                    IntentFilter filter = new IntentFilter(NatureNetService.ACTION_REGISTER_USER_RESULT);
                    registerReceiver(mUserRegisterReceiver, filter);

                    Intent serviceIntent = new Intent(NatureNetService.ACTION_REGISTER_USER, null, LoginActivity.this, NatureNetService.class);
                    serviceIntent.putExtra(NatureNetService.EMAIL, mEmail.getText().toString());
                    serviceIntent.putExtra(NatureNetService.USERNAME, mUsername.getText().toString());
                    serviceIntent.putExtra(NatureNetService.PASSWORD, mPassword.getText().toString());
                    serviceIntent.putExtra(NatureNetService.CONSENT, (mUseCCLicense ? "CC-BY-NC" : "on"));
                    startService(serviceIntent);

                    mHelper.loading(getString(R.string.registering));
                }
            }
        });

    }

    private void checkFields() {
        if (((mEmail.getText().length() == 0) && (mIsSignup)) || (mPassword.getText().length() < (mIsSignup ? 6 : 1)) || (mUsername.getText().length() == 0)) {
            mSignup.setEnabled(false);
        } else {
            mSignup.setEnabled(true);
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

    //@Override
    public void onLoginSuccessful() {
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
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
