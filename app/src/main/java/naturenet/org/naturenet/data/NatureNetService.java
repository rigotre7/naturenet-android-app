package naturenet.org.naturenet.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import naturenet.org.naturenet.R;

public class NatureNetService extends IntentService{

    private Handler mHandler;
    private SharedPreferences mPreferences;
    private String mLogin;
    private String mCredentials;

    public NatureNetService() {
        super("NatureNetService");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mPreferences = getSharedPreferences(getString(R.string.naturenet_prefs), MODE_PRIVATE);
        mLogin = mPreferences.getString("username", null);
        mCredentials = mPreferences.getString("credentials", null);
        //TODO
        String action = intent.getAction();
    }
}
