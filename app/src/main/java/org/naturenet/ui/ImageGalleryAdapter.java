package org.naturenet.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.naturenet.R;

import java.util.List;

/**
 * Provides a thumbnail view of images by URI, loaded by Picasso
 */
public class ImageGalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<Uri> mImages;

    public ImageGalleryAdapter(Context c, List<Uri> images) {
        mContext = c;
        mImages = images;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_gv_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_iv);
        Picasso.with(mContext).load(mImages.get(position)).error(R.drawable.no_image).fit().centerCrop().into(imageView);
        return convertView;
    }
}
