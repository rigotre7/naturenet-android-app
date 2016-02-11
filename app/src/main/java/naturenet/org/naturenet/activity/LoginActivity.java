package naturenet.org.naturenet.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import naturenet.org.naturenet.R;
import naturenet.org.naturenet.data.model.Account;

public class LoginActivity extends AppCompatActivity {

    Account mAccount = new Account();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public Account getAccount() {
        return mAccount;
    }
}
