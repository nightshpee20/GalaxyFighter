package fmi.android.galaxyfighter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Laser {
    int x, y;
    int width, height;
    Bitmap laser;

    Laser(Resources res) {
        laser = BitmapFactory.decodeResource(res, R.drawable.laser);
        width = laser.getWidth();
        height = laser.getHeight();
    }
}
