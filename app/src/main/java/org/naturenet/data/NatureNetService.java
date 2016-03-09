package org.naturenet.data;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.ServerValue;
import com.firebase.client.Transaction;

import org.naturenet.BuildConfig;
import org.naturenet.data.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NatureNetService extends IntentService {

    public static final String USER_ERROR = "usererror";
    public static final String USER_STATUS = "userstatus";
    public static final String USER_ACCOUNT = "useraccount";
    public static final String EMAIL = "email";
    public static final String USER_PASSWORD = "password";

    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGIN_RESULT = "LOGIN_RESULT";
    public static final String ACTION_REGISTER_USER = "REGISTER_USER";
    public static final String ACTION_REGISTER_USER_RESULT = "REGISTER_USER_RESULT";
    public static final String ACTION_LOGOUT = "LOGOUT";

    private Logger mLogger = LoggerFactory.getLogger(NatureNetService.class);
    private Handler mHandler;
    private Firebase mFirebase = new Firebase(BuildConfig.FIREBASE_ROOT_URL);

    public NatureNetService() {
        super("NatureNetService");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();

        if(action.equals(ACTION_LOGIN)) {
            if(mFirebase.getAuth() != null) {
                mLogger.info("User already authenticated");
                Intent reply = new Intent(ACTION_LOGIN_RESULT);
                reply.putExtra(USER_STATUS, true);
                sendBroadcast(reply);
                return;
            }
            signInWithPassword(intent);
            return;
        }
        if(action.equals(ACTION_REGISTER_USER)) {
            registerUser(intent);
            return;
        }
        if(action.equals(ACTION_LOGOUT)) {
            signOut();
            return;
        }

        mLogger.warn("Unhandled action {}", action);
    }

    private void signInWithPassword(final Intent intent) {
        Firebase.AuthResultHandler authHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                mLogger.debug("Successfully authenticated user {}", authData.getUid());
                //TODO insecure mPreferences.edit().putString(USERNAME, mCachedLogin).putString(USER_PASSWORD, mCachedPassword).commit();
                //TODO read account info from /users/<id> and put in reply
                Intent reply = new Intent(ACTION_LOGIN_RESULT);
                reply.putExtra(USER_STATUS, true);
                sendBroadcast(reply);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mLogger.error("Failed to authenticate user: {}", firebaseError);

                Intent reply = new Intent(ACTION_LOGIN_RESULT);
                reply.putExtra(USER_STATUS, false);
                reply.putExtra(USER_ERROR, firebaseError.getMessage());
                sendBroadcast(reply);
            }
        };

        String email = intent.getStringExtra(EMAIL);
        String password = intent.getStringExtra(USER_PASSWORD);

        if(email != null && password != null) {
            mLogger.debug("Authenticating with password");
            mFirebase.authWithPassword(email, password, authHandler);
        } else {
            mLogger.error("Missing user authentication parameters");
        }
    }

    private void registerUser(final Intent intent){
        final User user = intent.getParcelableExtra(USER_ACCOUNT);
        final String email = user.getPrivate().getEmail();
        final String password = intent.getStringExtra(USER_PASSWORD);

        Firebase.ValueResultHandler<Map<String, Object>> handler = new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                final String uid = result.get("uid").toString();
                user.getPublic().id = uid;
                mLogger.info("Successfully created user account {}", uid);

                mLogger.debug("Authenticating new user");
                mFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        mLogger.info("Creating user record {}", user);
                        final Firebase userRef = mFirebase.child("users").child(uid);
                        userRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                mutableData.setValue(user);
                                userRef.child("public").child("created_at").setValue(ServerValue.TIMESTAMP);
                                userRef.child("public").child("updated_at").setValue(ServerValue.TIMESTAMP);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(FirebaseError firebaseError, boolean completed, DataSnapshot dataSnapshot) {
                                if (completed) {
                                    mLogger.debug("User record created");
                                    Intent reply = new Intent(ACTION_REGISTER_USER_RESULT);
                                    reply.putExtra(USER_STATUS, true);
                                    sendBroadcast(reply);
                                } else {
                                    onUserRegistrationError(firebaseError);
                                }
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        onUserRegistrationError(firebaseError);
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                onUserRegistrationError(firebaseError);
            }
        };

        if(email != null && password != null) {
            mLogger.info("Registering new user");
            mFirebase.createUser(email, password, handler);
        } else {
            mLogger.error("Missing user authentication parameters");
        }
    }

    private void signOut() {
        mLogger.info("Signing out");
        mFirebase.unauth();
    }

    private void onUserRegistrationError(FirebaseError error) {
        mLogger.error("Failed to create new user: {}", error);

        Intent reply = new Intent(ACTION_REGISTER_USER_RESULT);
        reply.putExtra(USER_STATUS, false);
        reply.putExtra(USER_ERROR, error.getMessage());
        sendBroadcast(reply);
    }
}
