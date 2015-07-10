package kirin.cloud9;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ctsou on 7/10/2015.
 */
public class Player {

    int x;
    int y;
    final int DX = 15;
    final int DY = 15;
    int width;
    int height;
    Bitmap player;

    public Player(int px, int py, Bitmap pPlayer) {
        x = px;
        y = py;
        width = pPlayer.getWidth();
        height = pPlayer.getHeight();
        player = pPlayer;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDX() {
        return DX;
    }

    public int getDY() {
        return DY;
    }

    public void jumpLeft() {

    }

    public void jumpRight() {

    }

    public void jump() {

    }

}


