package com.emedicoz.app.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by app on 29/11/17.
 */

public class TestSeriesOptionWebView extends WebView implements GestureDetector.OnGestureListener {
    private boolean check = false;

    public TestSeriesOptionWebView(Context context) {
        super(context);
        handleClick();
    }

    public TestSeriesOptionWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        handleClick();
    }

    public TestSeriesOptionWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        handleClick();
    }

    private void handleClick() {
        setFocusable(false);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setVerticalScrollBarEnabled(false);
        setBackgroundColor(0);
        setHapticFeedbackEnabled(false);
        setOnLongClickListener(new LongClick(this));
    }

    public void setDisableWebViewTouchListener(boolean z) {
        this.check = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.check) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean canScrollHorizontal(int i) {
        int computeHorizontalScrollOffset = computeHorizontalScrollOffset();
        int computeHorizontalScrollRange = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (computeHorizontalScrollRange == 0) {
            return false;
        }
        if (i < 0) {
            return computeHorizontalScrollOffset > 0;
        } else return computeHorizontalScrollOffset < computeHorizontalScrollRange - 1;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();

        Log.v("[View name] onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("[View name] onMeasure h", MeasureSpec.toString(heightMeasureSpec_custom));
    }

    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return f != 0.0f;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return true;
    }

    class LongClick implements OnLongClickListener {
        final /* synthetic */ TestSeriesOptionWebView testSeriesOptionWebView;

        LongClick(TestSeriesOptionWebView testSeriesOptionWebView) {
            this.testSeriesOptionWebView = testSeriesOptionWebView;
        }

        public boolean onLongClick(View view) {
            return true;
        }
    }
}
