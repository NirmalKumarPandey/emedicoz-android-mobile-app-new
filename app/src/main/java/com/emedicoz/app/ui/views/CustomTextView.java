package com.emedicoz.app.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static Typeface typeface;
    public CustomTextView(Context context) {
        super(context);
        init(context);

    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);

    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context){
        if(typeface == null){
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Regular.otf");
        }
        setTypeface(typeface);
    }
}
