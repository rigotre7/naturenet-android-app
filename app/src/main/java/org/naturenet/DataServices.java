package org.naturenet;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.data.model.Project;

import java.util.ArrayList;

/**
 * Created by rigot on 6/8/2017.
 */

public class DataServices {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    /*
        Lists of all data.
     */
    ArrayList<Project> projectsList = new ArrayList<>();


    private static final DataServices ourInstance = new DataServices();

    public static DataServices getInstance() {
            return ourInstance;
    }

    /*
        Here initialize all listeners.
     */
    private DataServices() {
        setProjectListener();
    }

    private void setProjectListener(){
        dbRef.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Project project;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    project = data.getValue(Project.class);
                    projectsList.add(project);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Project> getProjectsList(){
        return projectsList;
    }
}
