package fmi.android.galaxyfighter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import fmi.android.galaxyfighter.R;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    private int screenX, screenY;
    Resources res;
    private Player player;
    private Bitmap background;
    private Paint paint;

    private List<Laser> onScreenLasers;
    private List<Laser> outOfBoundsLasers;

    private Bitmap fireBitmap;
    private Bitmap[] cooldownStages;
    private int cooldownStageHeight;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenX = screenX;
        this.screenY = screenY;

        res = getResources();
        player = new Player(screenX, screenY, res);
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        cooldownStages = new Bitmap[] {
                BitmapFactory.decodeResource(res, R.drawable.charge_red),
                BitmapFactory.decodeResource(res, R.drawable.charge_orange),
                BitmapFactory.decodeResource(res, R.drawable.charge_green)
        };
        cooldownStageHeight = cooldownStages[0].getHeight();

        paint = new Paint();

        onScreenLasers = new ArrayList<>();
        outOfBoundsLasers = new ArrayList<>();
    }

    @Override
    public void run() {
        while(isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        player.x += player.isGoingLeft ? -30 : 30;

        if (player.x < 0)
            player.x = 0;

        if (player.x > screenX - player.width)
            player.x = screenX - player.width;

        for (Laser laser : onScreenLasers) {
            if (laser.y < 0)
                outOfBoundsLasers.add(laser);
            laser.y -= 70;
        }

        for (Laser laser : outOfBoundsLasers)
            onScreenLasers.remove(laser);
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background, 0, 0, paint);
            canvas.drawBitmap(player.playerBitmap, player.x, player.y, paint);
            for (Laser laser : onScreenLasers)
                canvas.drawBitmap(laser.laser, laser.x, laser.y -= 30, paint);

            switch (player.cooldown) {
                case 2:
                    canvas.drawBitmap(cooldownStages[0], 0, (int)(screenY*0.70), paint);
                    break;
                case 1:
                    canvas.drawBitmap(cooldownStages[0], 0, (int)(screenY*0.70), paint);
                    canvas.drawBitmap(cooldownStages[1], 0, (int)(screenY*0.70) - cooldownStages[0].getHeight(), paint);
                    break;
                case 0:
                    canvas.drawBitmap(cooldownStages[0], 0, (int)(screenY*0.70), paint);
                    canvas.drawBitmap(cooldownStages[1], 0, (int)(screenY*0.70) - cooldownStageHeight, paint);
                    canvas.drawBitmap(cooldownStages[2], 0, (int)(screenY*0.70) - cooldownStageHeight*2, paint);
                    break;
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(34);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > (screenY - screenY / 4))
                player.isGoingLeft = event.getX() < screenX / 2 ? true : false;

            if (player.cooldown-- > 0)
                return true;

            newLaser();
            player.cooldown = 3;
        }

        return true;
    }

    public void newLaser() {
        Laser laser = new Laser(res);
        laser.x = player.x + player.width / 4;
        laser.y = player.y + (player.height / 2);
        onScreenLasers.add(laser);
    }
}

