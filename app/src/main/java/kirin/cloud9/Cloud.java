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
public class Cloud {

    int x;
    int y;
    int drawX;
    int drawY;
    boolean touched = false;
    int disappearCounter = 1000;
    int counterInterval = 10;

    public int getCounterInterval() {
        return counterInterval;
    }

    public void setCounterInterval(int counterInterval) {
        this.counterInterval = counterInterval;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public int getDisappearCounter() {
        return disappearCounter;
    }

    public void setDisappearCounter(int disappearCounter) {
        this.disappearCounter = disappearCounter;
    }

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

    final int DX = 15;
    final int DY = 15;
    int width;
    int height;
    Bitmap cloud;
    int distanceRank;

    public Cloud(int px, int py, Bitmap cCloud, int cDrawX, int cDrawY) {
        x = px;
        y = py;
        width = cCloud.getWidth();
        height = cCloud.getHeight();
        cloud = cCloud;
        drawX = cDrawX;
        drawY = cDrawY;
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


