package org.naturenet.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.naturenet.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingsFragment extends Fragment implements Settings {

    private static String url = "https://iid.googleapis.com/iid/info/<TOKEN>?details=true";
    public static final String FRAGMENT_TAG = "settings_fragment";

    Switch newProjectSwitch, newIdeaSwitch;
    ProgressBar progressBar;
    String notification_token;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String token) {

        Bundle args = new Bundle();
        args.putString("token", token);
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        newProjectSwitch = (Switch) root.findViewById(R.id.new_project_switch);
        newIdeaSwitch = (Switch) root.findViewById(R.id.new_idea_switch);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        notification_token = getArguments().getString("token");
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String newUrl = url.replace("<TOKEN>", notification_token);
        //retrieve the user's notification preferences
        new RetrieveTopics(SettingsFragment.this).execute(newUrl);

        newProjectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    Toast.makeText(getActivity(), "Checked - Project", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Not Checked - Project", Toast.LENGTH_SHORT).show();
            }
        });

        newIdeaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    Toast.makeText(getActivity(), "Checked - Idea", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Not Checked - Idea", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelProgress(boolean activities, boolean ideas) {
        progressBar.setVisibility(View.GONE);

        newProjectSwitch.setChecked(activities);
        newIdeaSwitch.setChecked(ideas);
    }
}

class RetrieveTopics extends AsyncTask<String, Void, String>{

    Settings fragment;

    public RetrieveTopics(Settings fragment){
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        fragment.setProgress();
    }

    @Override
    protected String doInBackground(String... strings) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "key=AAAAR-VFMbs:APA91bHD5VgvXscN0ixmFuZ6bVlOq-oY-z5f-YkiQEK34fzCHph3lUx0QZojvBqfmA7rsW2z8qCHYgRjUr9nrMlgseHU0VTHoAPy2coTbLRqiWkChQqNeC5SStGA--hc18LvcZH2N-4i");
            connection.setRequestProperty("Content-Type", "application/json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        boolean activities = false, ideas = false;
        JSONObject rel, topics;

        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

            if(jsonObject.has("rel")){
                rel = jsonObject.getJSONObject("rel");
                if(rel.has("topics")){
                    topics = rel.getJSONObject("topics");

                    if(topics.has("activities"))
                        activities = true;
                    if(topics.has("ideas"))
                        ideas = true;

                    fragment.cancelProgress(activities, ideas);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

interface Settings{
    void setProgress();
    void cancelProgress(boolean activities, boolean ideas);
}
