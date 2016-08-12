package org.naturenet.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Strings;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;

import org.naturenet.R;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.PreviewInfo;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    final private static int CAMERA_REQUEST = 1;
    final private static int GALLERY_REQUEST = 2;
    final private static int GV_IMAGE_WIDTH = 240;
    final private static int GV_IMAGE_HEIGHT = 240;
    static String ZERO = "0";
    static String LATITUDE = "0";
    static String LONGITUDE = "1";
    static String MY_LOCATION = "My Location";
    static String OBSERVATION = "Observation";
    static String NO_DESCRIPTION = "No Description";
    ImageButton add_observation, add_design_idea;
    Button explore, camera, gallery, design_ideas, design_challenges;
    TextView preview_observer_user_name, preview_observer_affiliation, preview_observation_text, preview_likes_count, preview_comments_count, select, add_observation_cancel, add_design_idea_cancel;
    LinearLayout dialog_preview, dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView preview_cancel, preview_observation_image, preview_observer_avatar, gallery_item;
    MainActivity main;
    List<String> galleryLatestList;
    String[] galleryLatest;
    String selectedImage;
    double latValue, longValue;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    MapView mMapView;
    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Observation previewSelectedObservation;
    private Map<Marker, PreviewInfo> allMarkersMap = new HashMap<Marker, PreviewInfo>();
    public ExploreFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ((MainActivity) this.getActivity());
        latValue = 0;
        longValue = 0;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(main)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10*1000);
            locationRequest.setFastestInterval(1*1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        mMapView = (MapView) v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            googleMap.setMyLocationEnabled(true);
        final LocationManager manager = (LocationManager) main.getSystemService( Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(main);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        for(int i=0; i<main.observations.size(); i++) {
            final Observation observation = main.observations.get(i);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(observation.getL().get(LATITUDE), observation.getL().get(LONGITUDE)))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(OBSERVATION));
            allMarkersMap.put(marker, main.previews.get(observation));
        }
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getTitle().equals(MY_LOCATION)) {
                    PreviewInfo preview = allMarkersMap.get(marker);
                    for (Observation observation: main.previews.keySet()) {
                        if (main.previews.get(observation).equals(preview)) {
                            previewSelectedObservation = observation;
                            break;
                        }
                    }
                    Picasso.with(main).load(Strings.emptyToNull(preview.observationImageUrl)).fit().into(preview_observation_image);
                    Picasso.with(main).load(Strings.emptyToNull(preview.observerAvatarUrl)).fit().into(preview_observer_avatar, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            preview_observer_avatar.setImageBitmap(GetBitmapClippedCircle(((BitmapDrawable) preview_observer_avatar.getDrawable()).getBitmap()));
                        }
                        @Override
                        public void onError() {
                            preview_observer_avatar.setImageBitmap(GetBitmapClippedCircle(BitmapFactory.decodeResource(main.getResources(), R.drawable.default_avatar)));
                        }
                    });
                    preview_observer_user_name.setText(preview.observerName);
                    preview_observer_affiliation.setText(preview.affiliation);
                    preview_observation_text.setText(preview.observationText);
                    preview_likes_count.setText(preview.likesCount);
                    preview_comments_count.setText(preview.commentsCount);
                    floating_buttons.setVisibility(View.GONE);
                    explore.setVisibility(View.GONE);
                    dialog_preview.setVisibility(View.VISIBLE);
                } else {
                    if (dialog_preview.getVisibility() == View.VISIBLE) {
                        floating_buttons.setVisibility(View.VISIBLE);
                        explore.setVisibility(View.VISIBLE);
                        dialog_preview.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
        return v;
    }
    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Path path = new Path();
        path.addCircle((float) (width/2), (float) (height/2), (float) Math.min(width, (height/2)), Path.Direction.CCW);
        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onLocationChanged(Location location) {
        if (latValue == 0 && longValue == 0) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title(MY_LOCATION));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        }
        latValue = location.getLatitude();
        longValue = location.getLongitude();
    }
    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onResume() {
        mMapView.onResume();
        if (mGoogleApiClient.isConnected())
            requestLocationUpdates();
        super.onResume();
    }
    @Override
    public void onPause() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (dialog_preview != null) {
            dialog_preview = null;
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        explore = (Button) main.findViewById(R.id.explore_b_explore);
        floating_buttons = (FrameLayout) main.findViewById(R.id.fl_floating_buttons);
        add_observation = (ImageButton) main.findViewById(R.id.floating_buttons_ib_add_observation);
        add_design_idea = (ImageButton) main.findViewById(R.id.floating_buttons_ib_add_design_idea);
        dialog_preview = (LinearLayout) main.findViewById(R.id.ll_dialog_preview);
        preview_cancel = (ImageView) main.findViewById(R.id.preview_cancel);
        preview_observation_image = (ImageView) main.findViewById(R.id.preview_observation_iv);
        preview_observer_avatar = (ImageView) main.findViewById(R.id.preview_observer_avatar);
        preview_observer_user_name = (TextView) main.findViewById(R.id.preview_observer_user_name);
        preview_observer_affiliation = (TextView) main.findViewById(R.id.preview_observer_affiliation);
        preview_observation_text = (TextView) main.findViewById(R.id.preview_observation_text);
        preview_likes_count = (TextView) main.findViewById(R.id.preview_likes_count);
        preview_comments_count = (TextView) main.findViewById(R.id.preview_comments_count);
        dialog_add_observation = (LinearLayout) main.findViewById(R.id.ll_dialog_add_observation);
        add_observation_cancel = (TextView) main.findViewById(R.id.dialog_add_observation_tv_cancel);
        camera = (Button) main.findViewById(R.id.dialog_add_observation_b_camera);
        gallery = (Button) main.findViewById(R.id.dialog_add_observation_b_gallery);
        select = (TextView) main.findViewById(R.id.dialog_add_observation_tv_select);
        gridview = (GridView) main.findViewById(R.id.dialog_add_observation_gv);
        gallery_item = (ImageView) main.findViewById(R.id.gallery_iv);
        dialog_add_design_idea = (LinearLayout) main.findViewById(R.id.ll_dialog_add_design_idea);
        add_design_idea_cancel = (TextView) main.findViewById(R.id.dialog_add_design_idea_tv_cancel);
        design_ideas = (Button) main.findViewById(R.id.dialog_add_design_idea_b_design_ideas);
        design_challenges = (Button) main.findViewById(R.id.dialog_add_design_idea_b_design_challenges);
        cameraPhoto = new CameraPhoto(main);
        galleryPhoto = new GalleryPhoto(main);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.goToObservationActivity();
            }
        });
        preview_observation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.previewSelectedObservation = previewSelectedObservation;
                main.goToObservationActivity();
            }
        });
        preview_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                dialog_preview.setVisibility(View.GONE);
            }
        });
        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST);
                else
                    setGallery();
                select.setVisibility(View.GONE);
                floating_buttons.setVisibility(View.GONE);
                explore.setVisibility(View.GONE);
                dialog_add_observation.setVisibility(View.VISIBLE);
            }
        });
        add_design_idea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.GONE);
                explore.setVisibility(View.GONE);
                dialog_add_design_idea.setVisibility(View.VISIBLE);
            }
        });
        add_observation_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryLatest != null)
                    for (int i = 0; i < galleryLatest.length; i++)
                        if (galleryLatest[i].equals(selectedImage))
                            gridview.getChildAt(i).findViewById(R.id.gallery_iv).setBackgroundResource(0);
                selectedImage = null;
                select.setVisibility(View.GONE);
                floating_buttons.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                dialog_add_observation.setVisibility(View.GONE);
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.observationPath = selectedImage;
                try {
                    Bitmap bitmap = ImageLoader.init().from(main.observationPath).requestSize(512, 512).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    main.observationBitmap = stream.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                main.newObservation = new Observation();
                Map<String, Double> latLong = new HashMap<String, Double>();
                double latitude = latValue;
                double longitude = longValue;
                latLong.put(LATITUDE, latitude);
                latLong.put(LONGITUDE, longitude);
                main.newObservation.setL(latLong);
                setGallery();
                main.goToAddObservationActivity();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                fileUri = getOutputMediaFileUri(1);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent, CAMERA_REQUEST);
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                } catch (IOException e) {
                    Toast.makeText(main, "Something Wrong while taking photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGallery();
                select.setVisibility(View.GONE);
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });
        add_design_idea_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_buttons.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                dialog_add_design_idea.setVisibility(View.GONE);
            }
        });
        design_ideas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        design_challenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        floating_buttons.setVisibility(View.VISIBLE);
        explore.setVisibility(View.VISIBLE);
        dialog_add_observation.setVisibility(View.GONE);
        dialog_add_design_idea.setVisibility(View.GONE);
        dialog_preview.setVisibility(View.GONE);
    }
//    private static Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
//    }
//    private static File getOutputMediaFile(int type) {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
//        if (!mediaStorageDir.exists())
//            if (!mediaStorageDir.mkdirs())
//                return null;
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == 1)
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
//        else
//            return null;
//        return mediaFile;
//    }
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context c) {
            mContext = c;
        }
        public int getCount() {
            return galleryLatest.length;
        }
        public Object getItem(int position) {
            return null;
        }
        public long getItemId(int position) {
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_gv_item, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_iv);
            imageView.setLayoutParams(new GridView.LayoutParams(GV_IMAGE_WIDTH, GV_IMAGE_HEIGHT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(main.decodeURI(galleryLatest[position]));
            imageView.setPadding(10,10,10,10);
            imageView.setBackgroundResource(0);
            return imageView;
        }
    }
    public void setGallery() {
        galleryLatestList = main.getAllShownImagesPath();
        if (galleryLatestList.size() != 0) {
            galleryLatest = galleryLatestList.toArray(new String[galleryLatestList.size()]);
            gridview.setAdapter(new ImageAdapter(main));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ImageView iv = (ImageView) v.findViewById(R.id.gallery_iv);
                    if (selectedImage == null) {
                        selectedImage = galleryLatest[position];
                        iv.setBackground(getResources().getDrawable(R.drawable.border_selected_image));
                        select.setVisibility(View.VISIBLE);
                    } else if (selectedImage.equals(galleryLatest[position])) {
                        selectedImage = null;
                        iv.setBackgroundResource(0);
                        select.setVisibility(View.GONE);
                    } else {
                        for (int i=0; i<galleryLatest.length; i++)
                            if (galleryLatest[i].equals(selectedImage))
                                gridview.getChildAt(i).findViewById(R.id.gallery_iv).setBackgroundResource(0);
                        selectedImage = galleryLatest[position];
                        iv.setBackground(getResources().getDrawable(R.drawable.border_selected_image));
                        select.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == main.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
//                main.observationPath = fileUri.getPath();
//                Toast.makeText(main, "Image saved to:\n" + data.getData().toString(), Toast.LENGTH_LONG).show();
//                main.observationPath = data.getData().toString();
                Log.d("camera", "Path: "+cameraPhoto.getPhotoPath());
                main.observationPath = cameraPhoto.getPhotoPath();
                cameraPhoto.addToGallery();
            } else if (requestCode == GALLERY_REQUEST) {
                galleryPhoto.setPhotoUri(data.getData());
                Log.d("gallery", "Path: "+galleryPhoto.getPath());
                main.observationPath = galleryPhoto.getPath();
            }
            try {
                Bitmap bitmap = ImageLoader.init().from(main.observationPath).requestSize(512, 512).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                main.observationBitmap = stream.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            main.newObservation = new Observation();
            Map<String, Double> latLong =  new HashMap<String, Double>();
            double latitude = latValue;
            double longitude = longValue;
            latLong.put(LATITUDE, latitude);
            latLong.put(LONGITUDE, longitude);
            main.newObservation.setL(latLong);
            setGallery();
            main.goToAddObservationActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setGallery();
                else
                    Toast.makeText(main, "Gallery Access Permission Denied", Toast.LENGTH_SHORT).show();
                break;
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setGallery();
                else
                    Toast.makeText(main, "Camera Access Permission Denied", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}