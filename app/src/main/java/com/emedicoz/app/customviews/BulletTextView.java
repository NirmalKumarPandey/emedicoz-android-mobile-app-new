package com.emedicoz.app.customviews;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class BulletTextView extends TextView {

    public BulletTextView(Context context) {
        super(context);
        addBullet();
    }

    public BulletTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addBullet();
    }

    public BulletTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addBullet();
    }

    public BulletTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addBullet();
    }

    private void addBullet() {
        CharSequence text = getText();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new BulletSpan(16), 0, text.length(), 0);
        setText(spannable);
    }
}
