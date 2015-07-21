package kirin.cloud9;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class GameActivity extends Activity {

    Player player;
    String TAG = "GameActivity: ";
    RelativeLayout gameOverLayout;
    Button replay, menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        Log.d(TAG, "Set Content View");
        gameOverLayout = (RelativeLayout) findViewById(R.id.gameoverlayout);
        gameOverLayout.setVisibility(View.INVISIBLE);

        replay = (Button) findViewById(R.id.replay);
        replay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(gameIntent);
                finish();
                return true;
            }
        });

        menu = (Button) findViewById(R.id.menu);
        menu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent gameIntent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(gameIntent);
                finish();
                return true;
            }
        });
    }

    public void setGameOverVisible() {
        //Log.d(TAG, "SETTING GAME OVER VISIBLE");
        gameOverLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
