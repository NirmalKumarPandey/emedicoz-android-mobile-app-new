package com.emedicoz.app.epubear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.emedicoz.app.R;


/**
 * Created by amykh on 09.09.2016.
 */
public class HtmlView extends View implements View.OnTouchListener {

    public static boolean mAnchorClicked = true;
    GestureDetector mGestureDetector;
    boolean flingStarted = false;
    private IViewListener mListener;
    private int mTurnPageArea = 0;
    private boolean mPageTurned = false;

    public HtmlView(Context context) {
        super(context);
        init(context);
    }

    public HtmlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HtmlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new SwipeGestureListener());
        setOnTouchListener(this);
        mTurnPageArea = (int) context.getResources().getDimension(R.dimen.reader_turn_page_area_width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mListener != null)
            mListener.onDraw(canvas);
        else
            Log.e("View", "listener is null!");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null)
            mListener.onSizeChanged(w, h);
    }

    public void setViewListener(IViewListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                if (x <= mTurnPageArea) {
                    if (mListener != null)
                        mListener.onSwipeRight();
                    mPageTurned = true;
                    mAnchorClicked = false;
                    return true;
                } else if (x >= v.getWidth() - mTurnPageArea) {
                    if (mListener != null)
                        mListener.onSwipeLeft();
                    mPageTurned = true;
                    mAnchorClicked = false;
                    return true;
                }
                if (!mPageTurned) {
                    mAnchorClicked = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mPageTurned = false;
                break;
        }
        //  prevents fling after successful turning a page
        if (mPageTurned)
            return true;

        boolean bRes = mGestureDetector.onTouchEvent(event);
        if (!flingStarted)
            bRes = bRes | mListener.onTouch(v, event);
        else
            flingStarted = false;
        return bRes;
       /* boolean bRes = mGestureDetector.onTouchEvent(event);
        if (!bRes)
            bRes = mListener.onTouch(v, event);
        return bRes;*/

/*        boolean bRes = mGestureDetector.onTouchEvent(event);
        bRes = bRes | mListener.onTouch(v, event);
        return bRes;*/
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        } catch (Exception e) {
            return false;
        }
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 50;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_VELOCITY_THRESHOLD = 200;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffAbs = Math.abs(e1.getY() - e2.getY());
            if (diffAbs > SWIPE_MAX_OFF_PATH)
                return false;

            float diff = e1.getX() - e2.getX();
            if (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diff > SWIPE_MIN_DISTANCE) {
                    if (mListener != null)
                        mListener.onSwipeLeft();
                } else if (-diff > SWIPE_MIN_DISTANCE) {
                    if (mListener != null)
                        mListener.onSwipeRight();
                }
            }

            flingStarted = true;
            return true;
        }

    }
}
