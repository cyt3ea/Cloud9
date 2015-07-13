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
    int drawX;
    int drawY;
    final int DX = 8;
    final int DY = 20;
    int width;
    int height;

    public int getDrawX() {
        return drawX;
    }

    public void setDrawX(int drawX) {
        this.drawX = drawX;
    }

    public int getDrawY() {
        return drawY;
    }

    public void setDrawY(int drawY) {
        this.drawY = drawY;
    }

    Bitmap player;

    public Player(int px, int py, Bitmap pPlayer, int pDrawX, int pDrawY) {
        x = px;
        y = py;
        width = pPlayer.getWidth();
        height = pPlayer.getHeight();
        player = pPlayer;
        drawX = pDrawX;
        drawY = pDrawY;
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
}


