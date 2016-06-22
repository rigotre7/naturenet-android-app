package org.naturenet.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.naturenet.R;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    ImageButton add_observation, add_design_idea;
    Button explore, camera, gallery, design_ideas, design_challenges;
    TextView select, add_observation_cancel, add_design_idea_cancel;
    LinearLayout dialog_add_observation, dialog_add_design_idea;
    FrameLayout floating_buttons;
    GridView gridview;
    ImageView gallery_item;
    MainActivity main;
    List<String> galleryLatestList;
    String[] galleryLatest;
    private static int RESULT_LOAD_IMG = 3;
    String imgDecodableString, selectedImage;
    public ExploreFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        main = ((MainActivity) this.getActivity());
        explore = (Button) main.findViewById(R.id.explore_b_explore);
        floating_buttons = (FrameLayout) main.findViewById(R.id.fl_floating_buttons);
        add_observation = (ImageButton) main.findViewById(R.id.floating_buttons_ib_add_observation);
        add_design_idea = (ImageButton) main.findViewById(R.id.floating_buttons_ib_add_design_idea);
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
        int permissionCheck = ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        } else {
            galleryLatestList = getAllShownImagesPath(main);
            if (galleryLatestList.size() != 0) {
                galleryLatest = galleryLatestList.toArray(new String[galleryLatestList.size()]);
                gridview.setAdapter(new ImageAdapter(main));
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        ImageView iv = (ImageView) v.findViewById(R.id.gallery_iv);
                        if (selectedImage == null) {
                            selectedImage = galleryLatest[position];
                            iv.setBackground(getResources().getDrawable(R.drawable.rectangular_selection));
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
                            iv.setBackground(getResources().getDrawable(R.drawable.rectangular_selection));
                            select.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                for (int i=0; i<galleryLatest.length; i++)
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
                main.goToAddObservationActivity(selectedImage);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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
    }
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
            imageView.setLayoutParams(new GridView.LayoutParams(240, 240));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(galleryLatest[position]));
            imageView.setPadding(10,10,10,10);
            imageView.setBackgroundResource(0);
            return imageView;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == main.RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = main.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                main.goToAddObservationActivity(imgDecodableString);
            } else {
                Toast.makeText(main, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(main, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
    private List<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        List<String> listOfAllImages = new ArrayList<String>();
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN };
        cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        while (cursor.moveToNext()) {
            if (listOfAllImages.size() < 8)
                listOfAllImages.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        }
        return listOfAllImages;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 12:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    galleryLatestList = getAllShownImagesPath(main);
                }
                break;

            default:
                break;
        }
    }
    public Bitmap decodeURI(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight ? options.outHeight / 100 : options.outWidth / 100;
            options.inSampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
        }
        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);
        return output;
    }
}