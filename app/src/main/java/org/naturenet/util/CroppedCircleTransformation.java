package org.naturenet.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class CroppedCircleTransformation  implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, Math.min(source.getWidth(), source.getHeight()) / 2, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "cropped-circle";
    }
}
