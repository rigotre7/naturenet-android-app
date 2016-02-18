package naturenet.org.naturenet.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import naturenet.org.naturenet.BuildConfig;
import naturenet.org.naturenet.R;

public class NatureNetService extends IntentService {

    public static final String REGISTER_USER_ERROR = "error";
    public static final String REGISTER_USER_STATUS = "status";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CONSENT = "consent";

    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_REGISTER_USER = "REGISTER_USER";
    public static final String ACTION_REGISTER_USER_RESULT = "REGISTER_USER_RESULT";

    private Logger mLogger = LoggerFactory.getLogger(NatureNetService.class);

    private Handler mHandler;
    private SharedPreferences mPreferences;
    private String mCachedUsername;
    private String mCachedPassword;

    private Firebase mFirebase = new Firebase(BuildConfig.FIREBASE_ROOT_URL);

    public NatureNetService() {
        super("NatureNetService");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mPreferences = getSharedPreferences(getString(R.string.naturenet_prefs), MODE_PRIVATE);
        mCachedUsername = intent.getStringExtra(USERNAME);
        mCachedPassword = intent.getStringExtra(PASSWORD);

        //TODO
        String action = intent.getAction();

        if(action.equals(ACTION_LOGIN)) {
            if(mFirebase.getAuth() != null) {
                mLogger.info("User already authenticated");
                //TODO already logged in, redirect
                return;
            }
            signInWithPassword();
            return;
        }
        if(action.equals(ACTION_REGISTER_USER)) {

            mFirebase.createUser(mCachedUsername, mCachedPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    mLogger.info("Successfully created user account {}", result.get("uid"));
                    signInWithPassword();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    //TODO: handle error
                    mLogger.error("Failed to create new user: {}", firebaseError);
                }
            });
            return;
        }

        mLogger.debug("Unhandled action {}", action);
    }

    private void signInWithPassword() {
        Firebase.AuthResultHandler authHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                mLogger.debug("Successfully authenticated user {}", authData.getUid());
                //TODO insecure mPreferences.edit().putString(USERNAME, mCachedUsername).putString(PASSWORD, mCachedPassword).commit();
                //TODO: proceed
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mLogger.error("Failed to authenticate user: {}", firebaseError);
                //TODO: report error
            }
        };

        mLogger.debug("Authenticating with password");
        mFirebase.authWithPassword(mCachedUsername, mCachedPassword, authHandler);
    }

    private void signOut() {
        mLogger.info("Signing out");
        mFirebase.unauth();
    }
}
