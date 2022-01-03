package com.emedicoz.app.Books.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Books.Model.Banner;
import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Helper;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.Objects;

public class BookBannerViewPagerAdapter extends PagerAdapter {
    // Context object
    public Context context;
    List<Banner> images;
    LayoutInflater mLayoutInflater;
    Activity activity;
    // Viewpager Constructor
    public BookBannerViewPagerAdapter(Context context, List<Banner> images) {
        this.context = context;
        this.images = images;
        this.activity=activity;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_banner_layout, container, false);

        // referencing the image view from the item.xml file
        RoundedImageView imageView = (RoundedImageView) itemView.findViewById(R.id.imageView);

        // setting the image in the imageView
        // imageView.setImageResource(Integer.parseInt(images[position]));
        Glide.with(context).load(images.get(position).getImage_link()).into(imageView);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);


       /* imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.fullScreenImageDialog(activity, images.get(position).getImage_link());
            }
        });*/

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((RelativeLayout) object);
    }
}
