package fmi.android.galaxyfighter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView muteImageView;
    MediaPlayer player;
    boolean playSound = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        muteImageView = findViewById(R.id.muteImageView);
        player = MediaPlayer.create(this, R.raw.homescreen);
        player.setLooping(true);
        player.start();
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

    public void exit(View view) {
        finish();
        System.exit(0);
    }
}