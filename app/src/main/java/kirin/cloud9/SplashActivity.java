package kirin.cloud9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashActivity extends Activity {

    static boolean load = false;
    private MediaPlayer logoMusic;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash);

        //logoMusic = MediaPlayer.create(SplashActivity.this, R.raw.splash_sound);
        //logoMusic.start();
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                    Intent gameIntent = new Intent(SplashActivity.this, GameActivity.class);
                    startActivity(gameIntent);
                    //Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    //logoMusic.stop();
                    //startActivity(menuIntent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        logoTimer.start();
    }
}

