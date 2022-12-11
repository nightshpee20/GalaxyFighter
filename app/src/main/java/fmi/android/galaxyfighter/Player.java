package fmi.android.galaxyfighter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player {
    int x, y, width, height;
    Bitmap playerBitmap;
    Bitmap laser;
    boolean isGoingLeft;
    int cooldown;

    Player(int screenX, int screenY, Resources res) {
        playerBitmap = BitmapFactory.decodeResource(res, R.drawable.spaceship_up);
        laser = BitmapFactory.decodeResource(res, R.drawable.laser);
        cooldown = 4;
        width = playerBitmap.getWidth();
        height = playerBitmap.getHeight();
        isGoingLeft = false;
        y = (int) (screenY / 1.30);
        x = (int) (screenX / 2.5);
    }
}
