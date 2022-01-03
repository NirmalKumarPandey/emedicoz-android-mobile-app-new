package com.emedicoz.app.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by shri ram
 */
public class CustomTextViewHeader extends androidx.appcompat.widget.AppCompatTextView {
    private static Typeface typeface;
    public CustomTextViewHeader(Context context) {
        super(context);
        init(context);

    }

    public CustomTextViewHeader(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);

    }

    public CustomTextViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
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
