package naturenet.org.naturenet.activity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import naturenet.org.naturenet.R;
import naturenet.org.naturenet.data.model.Account;

public class LoginActivity extends AppCompatActivity {

    Account mAccount = new Account();
    //TODO: custom app
    Application mApp;
    Logger mLogger = LoggerFactory.getLogger(LoginActivity.class);

    private ImageView mEmailIcon;
    private EditText mEmail;
    private ImageView mPasswordIcon;
    private EditText mPassword;
    private ImageView mUsernameIcon;
    private EditText mUsername;
    private TextView mPasswordWarning;

    private class UserRegistrationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger.debug("Creating LoginActivity");

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mApp = (Application)getApplicationContext();
        setContentView(R.layout.activity_login);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        mEmail = (EditText)findViewById(R.id.emial);
        mEmailIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                validateForm();
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                validateForm();
            }
        });

        mEmailIcon.getDrawable().setAlpha(0x7f);
        mPasswordIcon.getDrawable().setAlpha(0x7f);
        mUsernameIcon.getDrawable().setAlpha(0x7f);

        mPasswordWarning = (TextView) findViewById(R.id.password_warning);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (mPassword.getText().length() >= (mIsSignup ? 6 : 1)) {
                    mPasswordWarning.setVisibility(View.GONE);
                } else {
                    mPasswordWarning.setVisibility(View.VISIBLE);
                }

                validateForm();
            }
        });
    }

    public Account getAccount() {
        return mAccount;
    }

    private void validateForm() {

    }
}
