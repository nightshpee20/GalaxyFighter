package fmi.android.galaxyfighter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    boolean isGameOver;
    private int screenX, screenY;
    private Resources res;
    private Player player;
    private Bitmap background;
    private Paint paint;

    private List<Laser> onScreenLasers;
    private List<Laser> outOfBoundsLasers;

    private Bitmap fireBitmap;
    private Bitmap[] cooldownStages;
    private int cooldownStageHeight;
    private Bitmap loseCard;

    private Asteroid[] asteroids;
    private Random random;

    private GameActivity activity;

    private SoundPool soundPool;
    private int soundLaser;
    private int soundExplosion;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        this.screenX = screenX;
        this.screenY = screenY;

        random = new Random();

        res = getResources();
        player = new Player(screenX, screenY, res);
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        loseCard = BitmapFactory.decodeResource(res, R.drawable.lose_card);
        cooldownStages = new Bitmap[] {
                BitmapFactory.decodeResource(res, R.drawable.charge_red),
                BitmapFactory.decodeResource(res, R.drawable.charge_orange),
                BitmapFactory.decodeResource(res, R.drawable.charge_green)
        };
        cooldownStageHeight = cooldownStages[0].getHeight();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        soundLaser = soundPool.load(activity, R.raw.laser_sfx, 2);
        soundExplosion = soundPool.load(activity, R.raw.explosion_sfx, 1);

        asteroids = new Asteroid[] {
                new Asteroid(res, "big", screenX, screenY),
                new Asteroid(res, "big", screenX, screenY),
                new Asteroid(res, "big", screenX, screenY),
                new Asteroid(res, "mid", screenX, screenY),
                new Asteroid(res, "mid", screenX, screenY),
                new Asteroid(res, "mid", screenX, screenY),
                new Asteroid(res, "small", screenX, screenY),
                new Asteroid(res, "small", screenX, screenY)
        };

        paint = new Paint();
        paint.setColor(Color.YELLOW);

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

            for (Asteroid asteroid : asteroids) {
                if (Rect.intersects(asteroid.getCollisionShape(), laser.getCollisionShape())) {
                    if (MainActivity.player.isPlaying())
                        soundPool.play(soundExplosion, 1, 1, 0, 0, 1);
                    asteroid.x = -asteroid.width;
                    laser.y = 0;
                }
            }
        }

        for (Laser laser : outOfBoundsLasers)
            onScreenLasers.remove(laser);

        for (Asteroid asteroid : asteroids) {
            asteroid.y += asteroid.speed;
            if (asteroid.y - asteroid.height > screenY) {
                asteroid.speed = random.nextInt(Asteroid.MAX_SPEED);
                if (asteroid.speed < 20)
                    asteroid.speed = 20;
                asteroid.x = random.nextInt(screenX - asteroid.width);
                asteroid.y = -random.nextInt(screenY);
            }

            if (Rect.intersects(player.getCollisionShape(), asteroid.getCollisionShape())) {
                if (MainActivity.player.isPlaying())
                    soundPool.play(soundExplosion, 0.2f, 0.2f, 0, 0, 1);

                isGameOver = true;
                return;
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background, 0, 0, paint);

            for (Asteroid asteroid : asteroids)
                canvas.drawBitmap(asteroid.asteroidBitmap, asteroid.x, asteroid.y, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(player.playerDeath, player.x, player.y, paint);
                canvas.drawBitmap(loseCard, screenX / 2 - loseCard.getWidth() / 2, screenY / 2 - loseCard.getHeight(), paint);
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }

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
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        isGameOver = false;
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
                player.isGoingLeft = event.getX() < screenX / 2;

            if (player.cooldown-- > 0)
                return true;

            newLaser();
            player.cooldown = 3;
        }

        return true;
    }

    public void newLaser() {
        if (MainActivity.player.isPlaying())
            soundPool.play(soundLaser, 2, 2, 0, 0, 1);

        Laser laser = new Laser(res);
        laser.x = player.x + player.width / 4;
        laser.y = player.y + (player.height / 2);
        onScreenLasers.add(laser);
    }
}

