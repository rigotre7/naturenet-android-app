package naturenet.org.naturenet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import naturenet.org.naturenet.R;
import naturenet.org.naturenet.activity.LoginActivity;

public class LoginFragment extends Fragment {

    EditText mUsernameField = null;
    EditText mPasswordField = null;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_login, container, false);
        mUsernameField = (EditText)root.findViewById(R.id.input_username);
        mPasswordField = (EditText)root.findViewById(R.id.input_password);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginActivity) {
            throw new RuntimeException(context.toString()
                    + " must extend LoginActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onLoginButtonPressed(View v) {
        final String username = mUsernameField.getText().toString();
        final String password = mPasswordField.getText().toString();

        if(username.isEmpty()) {
            mUsernameField.setError("Please enter your username");
            mUsernameField.requestFocus();
            return;
        }

        if(password.isEmpty() || password.length() < 4) {
            mPasswordField.setError("Please enter your 4-digit PIN");
            mPasswordField.requestFocus();
            return;
        }

        doLogin(username, password);
    }

    public void doLogin(String username, String password) {
        ((TextView)getView().findViewById(R.id.textView)).setText(username + " " + password);
    }

    public void onLoginSuccess() {

    }
}
