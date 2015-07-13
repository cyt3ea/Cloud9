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
    final int DX = 15;
    final int DY = 15;
    int width;
    int height;
    Bitmap cloud;
    int distanceRank;

    public Cloud(int px, int py, Bitmap cCloud) {
        x = px;
        y = py;
        width = cCloud.getWidth();
        height = cCloud.getHeight();
        cloud = cCloud;
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


