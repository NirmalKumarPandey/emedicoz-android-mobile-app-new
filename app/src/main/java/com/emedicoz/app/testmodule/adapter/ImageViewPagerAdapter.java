package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Helper;

import java.util.Objects;

public class ImageViewPagerAdapter extends PagerAdapter {

    // Context object
    public Context context;

    // Array of images
    String[] images;

    // Layout Inflater
    LayoutInflater mLayoutInflater;

    Activity activity;

    // Viewpager Constructor
    public ImageViewPagerAdapter(Context context, String[] images, Activity activity) {
        this.context = context;
        this.images = images;
        this.activity=activity;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_image, container, false);

        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);

        // setting the image in the imageView
        // imageView.setImageResource(images[position]);
        Glide.with(context).load(images[position]).into(imageView);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.fullScreenImageDialog(activity, images[position]);
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
