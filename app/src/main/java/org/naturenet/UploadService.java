package org.naturenet;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.common.collect.Maps;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.data.model.Users;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class UploadService extends IntentService {

    public static final String EXTRA_URI_PATH = "uri_path";
    public static final String EXTRA_ACTIVE_USER = "active_user";
    public static final String EXTRA_OBSERVATION = "observation";

    private static final int MAX_IMAGE_DIMENSION = 1920;
    private static final String LATEST_CONTRIBUTION = "latest_contribution";

    private FirebaseDatabase mDatabase;
    private Observation newObservation;
    private Uri mImageUri;
    private Users mUser;

    public UploadService() {
        super("UploadService");
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public void onHandleIntent(Intent intent) {
        newObservation = intent.getParcelableExtra(EXTRA_OBSERVATION);
        mImageUri = Uri.parse(intent.getStringExtra(EXTRA_URI_PATH));
        mUser = intent.getParcelableExtra(EXTRA_ACTIVE_USER);
        uploadObservation();
    }

    private void uploadObservation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getUid().equals(mUser.id)) {
            Timber.d("Preparing image for upload");
            Toast.makeText(this, "Your observation is being submitted.", Toast.LENGTH_SHORT).show();

            final Map<String, String> config = Maps.newHashMap();
            config.put("cloud_name", "university-of-colorado");
            Cloudinary cloudinary = new Cloudinary(config);

            try {
                Map results = cloudinary.uploader().unsignedUpload(new File(mImageUri.getPath()), "android-preset", ObjectUtils.emptyMap());
                continueWithCloudinaryUpload(results);
            } catch (IOException ex) {
                Timber.w(ex, "Failed to upload image to Cloudinary");
                uploadImageWithFirebase();
            }

        } else {
            Timber.w("Attempt to upload observation without valid login");
            Toast.makeText(getApplicationContext(), "Please sign in to contribute an observation.", Toast.LENGTH_LONG).show();
        }
    }

    private void continueWithCloudinaryUpload(Map results) {
        Timber.i("Image uploaded to Cloudinary as %s", results.get("public_id"));
        writeObservationToFirebase((String)results.get("secure_url"));
    }

    private void uploadImageWithFirebase() {
        Timber.i("Attempting upload to Firebase");
        Picasso.with(this).load(mImageUri).resize(MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION).centerInside().onlyScaleDown().into(new Target() {

            final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos").child(UUID.randomUUID().toString());

            @Override
            public int hashCode() {
                return mImageUri.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return (obj != null) && (obj instanceof Target) && mImageUri.equals(obj);
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Timber.d("Bitmap loaded; uploading data stream to Firebase");
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] data = stream.toByteArray();
                continueWithFirebaseUpload(storageRef.putBytes(data));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Timber.e("Could not load bitmap; uploading original file to Firebase");
                continueWithFirebaseUpload(storageRef.putFile(mImageUri));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Timber.d("Loading raw bitmap data from %s", mImageUri.getPath());
            }
        });
    }

    private void continueWithFirebaseUpload(UploadTask uploadTask) {
        uploadTask.addOnSuccessListener(taskSnapshot -> writeObservationToFirebase(taskSnapshot.getDownloadUrl().toString()))
                .addOnFailureListener(ex -> {
                    Timber.w(ex, "Image upload task failed: %s", ex.getMessage());
                    Toast.makeText(getApplicationContext(), "Your photo could not be uploaded.", Toast.LENGTH_SHORT).show();
                });
    }

    private void writeObservationToFirebase(String imageUrl) {
        final String id = mDatabase.getReference(Observation.NODE_NAME).push().getKey();
        newObservation.id = id;
        newObservation.data.image = imageUrl;
        mDatabase.getReference(Observation.NODE_NAME).child(id).setValue(newObservation,(databaseError, databaseReference) -> {
            if (databaseError != null) {
                Timber.w(databaseError.toException(), "Failed to write observation to database: %s", databaseError.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_add_observation_error), Toast.LENGTH_SHORT).show();
            } else {
                mDatabase.getReference(Users.NODE_NAME).child(mUser.id).child(LATEST_CONTRIBUTION).setValue(ServerValue.TIMESTAMP);
                mDatabase.getReference(Project.NODE_NAME).child(newObservation.projectId).child(LATEST_CONTRIBUTION).setValue(ServerValue.TIMESTAMP);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_add_observation_success), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
