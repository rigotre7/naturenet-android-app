package org.naturenet.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.naturenet.R;

public class LaunchFragment extends Fragment {
    public LaunchFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launch, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivity main = ((MainActivity)  this.getActivity());
        main.findViewById(R.id.launch_ib_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.goToJoinActivity();
            }
        });
    }
}