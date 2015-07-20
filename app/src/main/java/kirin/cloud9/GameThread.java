package kirin.cloud9;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by ctsou on 7/10/2015.
 */
public class GameThread extends Thread {

    String TAG = "GameThread: ";
    private boolean running;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;

    public GameThread(SurfaceHolder sh, GameView gV) {
        super();
        surfaceHolder = sh;
        gameView = gV;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long tickCount = 0L;
        Log.d(TAG, "Starting game loop");
        while(running) {

            tickCount++;
        }
        Log.d(TAG, "Game loop executed " + tickCount + " times");
        Intent gameIntent = new Intent(gameView.getContext(), GameOverActivity.class);
        Activity a = (Activity) gameView.getContext();
        a.startActivity(gameIntent);
        a.finish();
    }
}
