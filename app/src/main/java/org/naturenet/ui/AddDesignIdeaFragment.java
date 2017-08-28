package org.naturenet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.naturenet.R;
import org.naturenet.data.model.Idea;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddDesignIdeaFragment extends Fragment {

    public static final String ADD_DESIGN_IDEA_FRAGMENT = "add_design_idea_fragment";

    EditText ideaTextEntry;
    TextView sendButton, participatoryLink;
    AddDesignIdeaActivity addIdeaAct;
    DatabaseReference dbRef;
    Spinner ideaTypeSpinner;
    String ideaText;

    public AddDesignIdeaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_design_idea, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addIdeaAct = (AddDesignIdeaActivity) getActivity();
        ideaTextEntry = (EditText) addIdeaAct.findViewById(R.id.design_idea_text);
        sendButton = (TextView) addIdeaAct.findViewById(R.id.design_idea_send_button);
        ideaTypeSpinner = (Spinner) addIdeaAct.findViewById(R.id.ideaTypeSpinner);
        dbRef = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<CharSequence> ideaTypeAdapter = ArrayAdapter.createFromResource(addIdeaAct, R.array.idea_types, android.R.layout.simple_spinner_item);
        ideaTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ideaTypeSpinner.setAdapter(ideaTypeAdapter);
        participatoryLink = (TextView) addIdeaAct.findViewById(R.id.participatory_design_link);
        participatoryLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextWatcher textWatcher = new TextWatcher() {

            SpannableString hashText;

            String prevText = "";

            boolean tagChange, isSpace, backspace = false;

            int indexOfNewChar;

            //Create matcher to match hashtags
            Matcher matcher;

            Pattern hash = Pattern.compile("#([A-Za-z0-9_-]+)");

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                Log.d("indexoutof", "after: " + after);
                Log.d("indexoutof", "start: " + start);
                Log.d("indexoutof", "count: " + count + "\n");

                if(prevText.length() >= charSequence.length())
                    backspace = true;

                //Check to make sure the change in text wasn't a space
                if(count != after) {
                    indexOfNewChar = start + count;
                    isSpace = false;
                }
                else    //if the change was actually a space, set boolean flag
                    isSpace = true;


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

//                Log.d("index-onTextChanged", "start: " + start);
//                Log.d("index-onTextChanged", "before: " + before);
//                Log.d("index-onTextChanged", "count: " + count);
//
                    //If the change wasn't a space, continue
                    if (!isSpace && charSequence.length() >= indexOfNewChar) {
                        char newestChar = charSequence.charAt(indexOfNewChar);
                        //Check to see if the most recent addition was a hashtag symbol and that we're not doubling up on hashtag symbols
                        if (newestChar == '#' && !tagChange)
                            tagChange = true;
                    } else   //if the change was actually a space, set the boolean flag
                        tagChange = false;



            }

            @Override
            public void afterTextChanged(Editable editable) {

                //check to see if we're currently on a tag
                if(tagChange){

                    //get the last index
                    int lastIndex = indexOfNewChar;
                    //Iterate over the text until we find the most recent # symbol
                    while(lastIndex >= 0){
                        if(editable.charAt(lastIndex) == '#'){
                            String t = editable.subSequence(lastIndex, editable.length()).toString();
                            matcher = hash.matcher(editable.subSequence(lastIndex, editable.length()));
                            //Check to see if it's a valid hashtag
                            if(matcher.matches()){
                                hashText = new SpannableString(editable);
                                hashText.setSpan(new ForegroundColorSpan(Color.parseColor("#F5C431")), lastIndex, editable.length(), 0);
                                ideaTextEntry.setText(hashText);
                                ideaTextEntry.setSelection(editable.length());
                                break;
                            }else
                                lastIndex--;

                        }else
                            lastIndex--;
                    }

                }

                prevText = editable.toString();


            }
        };

        ideaTextEntry.addTextChangedListener(textWatcher);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ideaText = ideaTextEntry.getText().toString();

                if(!ideaText.isEmpty()){

                    if(addIdeaAct.signed_user!=null){
                        sendButton.setVisibility(View.GONE);

                        //create a node for the new idea
                        DatabaseReference ideaRef = dbRef.child(Idea.NODE_NAME).push();
                        //create the new Idea object
                        Idea newIdea = Idea.createNew(ideaRef.getKey(), ideaText, addIdeaAct.signed_user.id, ideaTypeSpinner.getSelectedItem().toString());

                        //push the new idea to the newly created node
                        ideaRef.setValue(newIdea, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                //if we get an error, display an appropriate message
                                if(databaseError!=null){
                                    Toast.makeText(addIdeaAct, "Design Idea could not be submitted.", Toast.LENGTH_LONG).show();
                                    Log.d("permissionerror", databaseReference.toString());
                                }else{
                                    Toast.makeText(addIdeaAct, "Design Idea submitted!", Toast.LENGTH_SHORT).show();
                                    ideaTextEntry.getText().clear();
                                    sendButton.setVisibility(View.VISIBLE);
                                    addIdeaAct.finish();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(addIdeaAct, "Please login to submit an Idea.", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(addIdeaAct, LoginActivity.class);
                        startActivityForResult(loginIntent, 99);
                    }

                }else{
                    Toast.makeText(addIdeaAct, "Enter an Idea", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
