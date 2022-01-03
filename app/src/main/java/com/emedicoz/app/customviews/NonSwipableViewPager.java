package com.emedicoz.app.customviews;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipableViewPager extends ViewPager {

    private boolean enabled;

    public NonSwipableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled && super.onInterceptTouchEvent(event);
    }

    public boolean isPagingEnabled() {
        return enabled;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}