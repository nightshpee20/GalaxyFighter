package fmi.android.galaxyfighter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Asteroid {
    static Random spawnPointGen;
    static final int MAX_SPEED = 50;

    public int speed;
    int x, y, width, height;
    Bitmap asteroidBitmap;
    String type;

    static {
        spawnPointGen = new Random();
    }

    Asteroid(Resources res, String type, int screenX, int screenY) {

        speed = spawnPointGen.nextInt(MAX_SPEED);
        this.type = type;

        switch (type) {
            case "big":
                asteroidBitmap = BitmapFactory.decodeResource(res, R.drawable.asteroid_big);
                break;
            case "mid":
                asteroidBitmap = BitmapFactory.decodeResource(res, R.drawable.asteroid_mid);
                break;
            case "small":
                asteroidBitmap = BitmapFactory.decodeResource(res, R.drawable.asteroid_small);
                break;
        }

        width = asteroidBitmap.getWidth();
        height = asteroidBitmap.getHeight();


        x = spawnPointGen.nextInt(screenX - width);
        y = -spawnPointGen.nextInt(screenY);
    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
