package org.naturenet;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.naturenet.data.model.Users;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TOKEN = "token";
    private static final String TOKEN_NODE = "notification_token";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenId = FirebaseInstanceId.getInstance().getToken();
        Log.d(TOKEN, TOKEN + " is " + tokenId);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            FirebaseDatabase.getInstance().getReference()
                    .child(Users.NODE_NAME)
                    .child(firebaseUser.getUid())
                    .child(TOKEN_NODE)
                    .setValue(tokenId);
        }

    }
}
