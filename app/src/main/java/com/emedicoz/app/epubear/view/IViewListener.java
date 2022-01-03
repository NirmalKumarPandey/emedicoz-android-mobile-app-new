package com.emedicoz.app.epubear.view;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by amykh on 09.09.2016.
 */
public interface IViewListener {
    public void onDraw(Canvas canvas);

    public void onSizeChanged(int width, int height);

    public void onSwipeLeft();

    public void onSwipeRight();

    public boolean onTouch(View v, MotionEvent event);
}
