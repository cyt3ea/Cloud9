package kirin.cloud9;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * Created by ctsou on 7/10/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    String TAG = "GameView: ";

    int x, y;
    int leftCoord, rightCoord, centerCoord, startY, yMax;
    final double jumpVertexFactor = (1 / 25.0);
    boolean jumpStraight = false;
    int cloudDist;
    int numCloudRows;

    int screenWidth, screenHeight;

    Player player;

    //Bitmaps
    Bitmap backgroundBit;
    Bitmap playerBit;
    Bitmap cloudBit;

    //Swipe detection variables
    private float initialX = 0;
    private float initialY = 0;
    private float deltaX = 0;
    private float deltaY = 0;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setWillNotDraw(false);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        screenWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = View.MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(screenWidth, screenHeight);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);

        backgroundBit = BitmapFactory.decodeResource(getResources(), R.drawable.game_back);
        playerBit = BitmapFactory.decodeResource(getResources(), R.drawable.fox);
        playerBit = Bitmap.createScaledBitmap(playerBit, 150, 150, false);
        int cloudHeight = 150;
        int cloudWidth = 225;
        cloudBit = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
        cloudBit = Bitmap.createScaledBitmap(cloudBit, cloudWidth, cloudHeight, false);

        centerCoord = (screenWidth - playerBit.getWidth()) / 2;
        startY = (int) (screenHeight * 0.8);
        x = centerCoord;
        y = startY;
        player = new Player(centerCoord, startY, playerBit);

        int pixelsToMoveLeft = (screenWidth - playerBit.getWidth()) / 2 + (screenWidth - playerBit.getWidth()) / 3;
        leftCoord = pixelsToMoveLeft - pixelsToMoveLeft % player.getDX();
        int pixelsToMoveRight = (screenWidth - playerBit.getWidth()) / 2 - (screenWidth - playerBit.getWidth()) / 3;
        rightCoord = pixelsToMoveRight - pixelsToMoveRight % player.getDX();

        yMax = (int) (jumpVertexFactor * ((rightCoord + centerCoord) / 2 - rightCoord) * ((rightCoord + centerCoord) / 2 - centerCoord) + startY);
        cloudDist = (startY - yMax) - cloudHeight/2;
        numCloudRows = startY/cloudDist;

        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                deltaX = deltaY = 0;
                initialX = event.getRawX();
                initialY = event.getRawY();
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                deltaX = event.getRawX() - initialX;
                deltaY = event.getRawX() - initialY;
                if (deltaX < 0) {//swiped right
                    if (player.getX() == centerCoord)
                        player.setX(rightCoord);
                    else if(player.getX() == leftCoord)
                        player.setX(centerCoord);
                    Log.d(TAG, "Swiped right");
                } else if (deltaX > 0) { //swiped left
                    if (player.getX() == centerCoord)
                        player.setX(leftCoord);
                    else if(player.getX() == rightCoord)
                        player.setX(centerCoord);
                    Log.d(TAG, "Swiped left");
                } else {
                    //player.setX((screenWidth - playerBit.getWidth())/2);
                    jumpStraight = true;
                    Log.d(TAG, "Tapped");
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgroundBit, 0, 0, null);

        //clouds
        for(int i = 1; i<=numCloudRows; i++) {
            canvas.drawBitmap(cloudBit, x, startY - cloudDist * i, null);
        }

        //move player jumping
        if (jumpStraight) {
            if (player.getX() == x) {
                if (y > yMax)
                    y -= player.getDY();
                else {
                    jumpStraight = false;
                }
            }
        }
        else {
            if (player.getX() == x && y < startY) { //fall down after jumping straight up
                y += player.getDY();
            } else if (x < player.getX() && x < centerCoord) { //middle -> right
                x += player.getDX();
                y = (int) (jumpVertexFactor * (x - rightCoord) * (x - centerCoord) + startY);
            } else if (x > player.getX() && x > centerCoord) { //middle -> left
                x -= player.getDX();
                y = (int) (jumpVertexFactor * (x - leftCoord) * (x - centerCoord) + startY);
            } else if (x < player.getX() && x >= centerCoord) { //left -> middle
                x += player.getDX();
                y = (int) (jumpVertexFactor * (x - leftCoord) * (x - centerCoord) + startY);
            } else if (x > player.getX() && x <= centerCoord) { //right -> middle
                x -= player.getDX();
                y = (int) (jumpVertexFactor * (x - rightCoord) * (x - centerCoord) + startY);
            }
        }
        canvas.drawBitmap(playerBit, x, y, null);

        invalidate();
    }
}