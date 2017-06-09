package org.naturenet.ui;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.naturenet.R;
import org.naturenet.data.model.Project;


public class AddProjectFragment extends Fragment {

    public static final String ADD_PROJECT_FRAGMENT = "add_project_fragment";

    private EditText projectTitle, projectDescription;
    private TextView submitButton;
    private String title, description;
    DatabaseReference dbRef;

    public AddProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_project, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final AddProjectActivity activity = (AddProjectActivity) getActivity();

        dbRef = FirebaseDatabase.getInstance().getReference();
        projectDescription = (EditText) activity.findViewById(R.id.projectDescription);
        projectTitle = (EditText) activity.findViewById(R.id.projectTitle);
        submitButton = (TextView) activity.findViewById(R.id.projectSubmitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = projectTitle.getText().toString();
                description = projectDescription.getText().toString();

                //make sure user is logged in
                if(activity.signed_user!=null){
                    if(!title.isEmpty() && !description.isEmpty()){

                        //create node for the new Project and get the unique id
                        DatabaseReference projectRef = dbRef.child(Project.NODE_NAME).push();

                        //create new Project object
                        Project project = Project.createNew(projectRef.getKey(), "https://res.cloudinary.com/university-of-colorado/image/upload/v1486742181/static/Love_Environment.png",
                                description, title, null, null, null);

                        projectRef.setValue(project, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                //make sure there isn't an error
                                if(databaseError!=null){
                                    Toast.makeText(activity, "Project could not be submitted.", Toast.LENGTH_LONG).show();
                                    Log.d("permissionerror", databaseReference.toString());
                                }else{
                                    Toast.makeText(activity, "Project submitted!", Toast.LENGTH_LONG).show();
                                    projectTitle.getText().clear();
                                    projectDescription.getText().clear();
                                    activity.finish();
                                }
                            }
                        });

                    }else{  //notify user they must enter all field information
                        Toast.makeText(activity, "Please complete all fields", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(activity, "Please log in to submit a project.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(activity, LoginActivity.class);
                    startActivityForResult(loginIntent, 100);
                }
            }
        });
    }
}
