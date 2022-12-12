package fmi.android.galaxyfighter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static MediaPlayer player;

    ImageView muteImageView;
    boolean playSound = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        muteImageView = findViewById(R.id.muteImageView);
        startPlayer(R.raw.homescreen);
    }

    public void toggleSound(View view) {
        if (playSound) {
            muteImageView.setImageResource(R.drawable.sound_on);
            player.start();
            playSound = false;
        } else {
            muteImageView.setImageResource(R.drawable.sound_off);
            player.pause();
            playSound = true;
        }
    }

    public void start(View view) {
        if (player.isPlaying()) {
            player.stop();
            startPlayer(R.raw.battle);
        }
        startActivity(new Intent(MainActivity.this, GameActivity.class));
    }

    public void exit(View view) {
        finish();
        System.exit(0);
    }

    public void startPlayer(int music) {
        player = MediaPlayer.create(this, music);
        player.setLooping(true);
        player.start();
    }
}