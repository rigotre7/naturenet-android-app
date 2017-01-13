package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.naturenet.R;

public class LaunchFragment extends Fragment {

    ImageButton join_ib;
    MainActivity main;
    TextView toolbar_title, sign_in;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launch, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        main = ((MainActivity)  this.getActivity());
        toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        sign_in = (TextView) main.findViewById(R.id.launch_tv_sign_in);
        toolbar_title.setText(R.string.launch_title);
        join_ib = (ImageButton) main.findViewById(R.id.launch_ib_join);

        join_ib.setOnClickListener(v -> {
            if (main.haveNetworkConnection()) {
                join_ib.setVisibility(View.GONE);
                main.goToJoinActivity();
            } else {
                Toast.makeText(main, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        sign_in.setOnClickListener(v -> {
            if (main.haveNetworkConnection()) {
                sign_in.setVisibility(View.GONE);
                main.goToLoginActivity();
            } else {
                Toast.makeText(main, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}