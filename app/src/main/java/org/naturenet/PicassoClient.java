package org.naturenet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(Context c, String url, ImageView img) {
        if (url!=null && url.length()>0) {
            Picasso.with(c).load(url).placeholder(R.drawable.add_design_idea).into(img);
        } else {
            Picasso.with(c).load(R.drawable.add_design_idea).into(img);
        }
    }
}