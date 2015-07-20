package kirin.cloud9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by ctsou on 7/10/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    String TAG = "GameView: ";

    int leftCoord, rightCoord, centerCoord, startY, yMaxCloud, yMaxCharacter;
    final double jumpVertexFactor = (1 / 25.0);
    boolean jumpStraight = false;
    boolean dead = false;
    int cloudDist;
    int numCloudRows;
    int playerOffset;
    ArrayList<ArrayList<Cloud>> cloudList = new ArrayList<ArrayList<Cloud>>();

    boolean jump = false;

    Random rng = new Random();

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
        int cloudWidth = 150;
        playerOffset = cloudHeight/3;
        cloudBit = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
        cloudBit = Bitmap.createScaledBitmap(cloudBit, cloudWidth, cloudHeight, false);


        centerCoord = (screenWidth - playerBit.getWidth()) / 2;
        startY = (int) (screenHeight * 0.8);
        player = new Player(centerCoord, startY - playerOffset, playerBit, centerCoord, startY);

        int pixelsToMoveRight = (screenWidth - playerBit.getWidth()) / 2 + (screenWidth - playerBit.getWidth()) / 3;
        rightCoord = pixelsToMoveRight - ((pixelsToMoveRight-centerCoord) % player.getDX());
        int pixelsToMoveLeft = (screenWidth - playerBit.getWidth()) / 2 - (screenWidth - playerBit.getWidth()) / 3;
        leftCoord = pixelsToMoveLeft + ((centerCoord - pixelsToMoveLeft) % player.getDX());

        yMaxCloud = (int) (jumpVertexFactor * ((rightCoord + centerCoord) / 2 - rightCoord) * ((rightCoord + centerCoord) / 2 - centerCoord) + startY);
        yMaxCharacter = (startY - yMaxCloud)/2 + yMaxCloud - playerOffset;
        cloudDist = (startY - yMaxCloud) - cloudHeight/3;
        numCloudRows = startY/cloudDist;

        //cloudList.add(new ArrayList<Cloud>());
        for(int k = 1; k <= numCloudRows+1; k++) {
            cloudList.add(generateCloudRow(k));
        }

        //Log.d(TAG, "LEFT: " + leftCoord + ", CENTER: " + centerCoord + ", RIGHT: " + rightCoord);

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
                if(!jump && !dead) {
                    jump = true;
                    deltaX = event.getRawX() - initialX;
                    deltaY = event.getRawX() - initialY;
                    if (deltaX > 0) {//swiped right
                        if (player.getX() == centerCoord) {
                            player.setX(rightCoord);
                            player.setMovement("MidToRight");
                        } else if (player.getX() == leftCoord) {
                            player.setX(centerCoord);
                            player.setMovement("LeftToMid");
                        }
                        Log.d(TAG, "Swiped right");
                    } else if (deltaX < 0) { //swiped left
                        if (player.getX() == centerCoord) {
                            player.setX(leftCoord);
                            player.setMovement("MidToLeft");
                        } else if (player.getX() == rightCoord) {
                            player.setX(centerCoord);
                            player.setMovement("RightToMid");
                        }
                        Log.d(TAG, "Swiped left");
                    } else {
                        //player.setX((screenWidth - playerBit.getWidth())/2);
                        jumpStraight = true;
                        Log.d(TAG, "Tapped");
                    }
                    //cloudList.remove(0);
                    //Log.d(TAG, cloudList.size() + " " + numCloudRows);
                    if (cloudCharacterCollide() == 1) {
                        for (int k = 0; k < cloudList.size(); k++) {
                            for (Cloud c : cloudList.get(k)) {
                                c.setY(c.getY() + cloudDist);
                            }
                        }
                        cloudList.add(generateCloudRow(numCloudRows + 1));
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void removeOffScreenClouds() {
        ArrayList<Cloud> lowestCloudRow = cloudList.get(0);
        for(Cloud c : lowestCloudRow) {
            if(c.getY() > screenHeight) {
                cloudList.remove(0);
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(dead && player.getDrawY() > screenHeight) {
            thread.setRunning(false);
        }

        canvas.drawBitmap(backgroundBit, 0, 0, null);

        //clouds
        for(int i = 1; i<=cloudList.size(); i++) {
            Iterator cloudItr = cloudList.get(i-1).iterator();
            while(cloudItr.hasNext()) {
                Cloud c = (Cloud) cloudItr.next();
                if(c.isTouched())
                    c.setDisappearCounter(c.getDisappearCounter()-c.getCounterInterval());
                if(c.getDrawY() < c.getY()) {
                    c.setDrawY(c.getDrawY() + player.getDY());
                    removeOffScreenClouds();
                }
                if(c.getDisappearCounter() > 0)
                    canvas.drawBitmap(cloudBit, c.getDrawX(), c.getDrawY(), null);
                else {
                    cloudItr.remove();
                    Log.d(TAG, "Removed cloud");
                    if(cloudCharacterCollide() == -1) {
                        dead = true;
                        Log.d(TAG, "Character dead");
                    }
                }
            }
        }

        //move player jumping
        if (jumpStraight) {
            player.setMovement("Straight");
            if (player.getX() == player.getDrawX()) {
                if (player.getDrawY() > yMaxCharacter)
                    player.setDrawY(player.getDrawY()-player.getDY());
                else {
                    jumpStraight = false;
                }
            }
        }
        else {
            if ((player.getX() == player.getDrawX() && player.getDrawY() < startY - playerOffset) || (dead && player.getMovement().equals("None") && player.getDrawY() < screenHeight)) { //fall down after jumping straight up
                player.setDrawY(player.getDrawY() + player.getDY());
            } else if ((player.getDrawX() < player.getX() && player.getDrawX() < centerCoord && !dead) || (dead && player.getMovement().equals("LeftToMid") && player.getDrawY() < screenHeight)) { //left -> mid
                //Log.d(TAG, "Left to Mid");
                player.setDrawX(player.getDrawX() + player.getDX());
                player.setDrawY((int) (jumpVertexFactor / 2 * (player.getDrawX() - leftCoord) * (player.getDrawX() - centerCoord) + startY - playerOffset));
            } else if ((player.getDrawX() > player.getX() && player.getDrawX() > centerCoord && !dead) || (dead && player.getMovement().equals("RightToMid") && player.getDrawY() < screenHeight)) { //right -> mid
                //Log.d(TAG, "Right to Mid");
                player.setDrawX(player.getDrawX() - player.getDX());
                player.setDrawY((int) (jumpVertexFactor / 2 * (player.getDrawX() - rightCoord) * (player.getDrawX() - centerCoord) + startY - playerOffset));
            } else if ((player.getDrawX() < player.getX() && player.getDrawX() >= centerCoord && !dead) || (dead && player.getMovement().equals("MidToRight") && player.getDrawY() < screenHeight)) { //mid -> right
                //Log.d(TAG, "Mid to Right");
                player.setDrawX(player.getDrawX() + player.getDX());
                player.setDrawY((int) (jumpVertexFactor / 2 * (player.getDrawX() - rightCoord) * (player.getDrawX() - centerCoord) + startY - playerOffset));
            } else if ((player.getDrawX() > player.getX() && player.getDrawX() <= centerCoord && !dead) || (dead && player.getMovement().equals("MidToLeft") && player.getDrawY() < screenHeight)) { //mid -> left
                //Log.d(TAG, "Mid to Left");
                player.setDrawX(player.getDrawX() - player.getDX());
                player.setDrawY((int) (jumpVertexFactor / 2 * (player.getDrawX() - leftCoord) * (player.getDrawX() - centerCoord) + startY - playerOffset));
            }
            else {
                jump = false;
                player.setMovement("None");
            }
            //Log.d(TAG, x + "  " + y);
        }
        canvas.drawBitmap(playerBit, player.getDrawX(), player.getDrawY(), null);

        invalidate();
    }

    public int cloudCharacterCollide() {
        ArrayList<Cloud> closestCloudRow;

        //hits row of clouds above it
        if(jump) {
            if (cloudList.size() == numCloudRows + 1)
                closestCloudRow = cloudList.get(0);
            else
                closestCloudRow = cloudList.get(1);
            for (Cloud c : closestCloudRow) {
                if (c.getX() == player.getX()) {
                    c.setTouched(true);
                    return 1;
                }
            }
        }

        //hits current row player is on
        if(cloudList.size() == numCloudRows + 1) { //on the ground
            return 0;
        }
        else {
            closestCloudRow = cloudList.get(0);
            for(Cloud c : closestCloudRow) {
                if(c.getX() == player.getX() ) {
                    c.setTouched(true);
                    return 0;
                }
            }
        }
        //does not hit any
        dead = true;
        return -1;
    }

    public ArrayList<Cloud> generateCloudRow(int rank) {
        //Log.d(TAG, cloudList.size() + " ");
        ArrayList<Cloud> tempCloudList = new ArrayList<Cloud>();
        if(cloudList.size() == 0) {
            int numClouds = 1 + (int)(Math.random()*3);
            Set<Integer> generated = new LinkedHashSet<Integer>();
            while(generated.size() != numClouds) {
                int next = rng.nextInt(3) + 1;
                generated.add(next);
            }
            for(Integer cloudPos : generated) {
                if (cloudPos == 1)
                    tempCloudList.add(new Cloud(leftCoord, startY - cloudDist * rank, cloudBit, leftCoord, startY - cloudDist * rank));
                else if (cloudPos == 2)
                    tempCloudList.add(new Cloud(centerCoord, startY - cloudDist * rank, cloudBit, centerCoord, startY - cloudDist * rank));
                else if (cloudPos == 3)
                    tempCloudList.add(new Cloud(rightCoord, startY - cloudDist * rank, cloudBit, rightCoord, startY - cloudDist * rank));
            }

        }
        else {
            ArrayList<Cloud> lastCloudRow = cloudList.get(cloudList.size()-1);
            Set<Integer> generated = new LinkedHashSet<Integer>();
            if(lastCloudRow.size() != 0) {
                int cloudSelection = 0 + (int) (Math.random() * (lastCloudRow.size() - 1));
                Cloud c = lastCloudRow.get(cloudSelection);
                if (c.getX() == leftCoord) {
                    int numClouds = 1 + (int) (Math.random() * 2);
                    for (int i = 0; i < numClouds; i++) {
                        int next = rng.nextInt(2) + 1;
                        generated.add(next);
                    }
                } else if (c.getX() == centerCoord) {
                    int numClouds = 1 + (int) (Math.random() * 3);
                    for (int i = 0; i < numClouds; i++) {
                        int next = rng.nextInt(3) + 1;
                        generated.add(next);
                    }
                } else if (c.getX() == rightCoord) {
                    int numClouds = 1 + (int) (Math.random() * 2);
                    for (int i = 0; i < numClouds; i++) {
                        int next = rng.nextInt(3) + 2;
                        generated.add(next);
                    }
                }
                for (Integer cloudPos : generated) {
                    if (cloudPos == 1)
                        tempCloudList.add(new Cloud(leftCoord, startY - cloudDist * rank, cloudBit, leftCoord, startY - cloudDist * rank));
                    else if (cloudPos == 2)
                        tempCloudList.add(new Cloud(centerCoord, startY - cloudDist * rank, cloudBit, centerCoord, startY - cloudDist * rank));
                    else if (cloudPos == 3)
                        tempCloudList.add(new Cloud(rightCoord, startY - cloudDist * rank, cloudBit, rightCoord, startY - cloudDist * rank));
                }
            }
        }
        if(tempCloudList.size() == 0)
            tempCloudList.add(new Cloud(centerCoord, startY - cloudDist * rank, cloudBit, centerCoord, startY - cloudDist * rank));
        return tempCloudList;
    }
}