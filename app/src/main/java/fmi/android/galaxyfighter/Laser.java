package fmi.android.galaxyfighter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Laser {
    int x, y;
    int width, height;
    Bitmap laser;

    Laser(Resources res) {
        laser = BitmapFactory.decodeResource(res, R.drawable.laser);
        width = laser.getWidth();
        height = laser.getHeight();
    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
