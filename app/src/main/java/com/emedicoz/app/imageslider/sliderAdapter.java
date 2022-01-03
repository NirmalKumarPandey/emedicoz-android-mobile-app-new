package com.emedicoz.app.imageslider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.video.activity.VideoDetail;

import java.util.List;

/**
 * Created by admin1 on 6/11/17.
 */

public class sliderAdapter extends PagerAdapter {

    Context ctx;
    List<Video> products;
    Activity activity;
    ViewGroup container;

    public sliderAdapter(Context ctx, List<Video> products, Activity activity) {
        this.ctx = ctx;
        this.products = products;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        this.container = container;
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.vp_image, container, false);
        ImageView featured_tag = view.findViewById(R.id.featured_tag);

        ImageView mImageView = view.findViewById(R.id.image_display);
        final Video item = products.get(position);

        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*                if (item.getVideo_type().equals(Const.VIDEO_LIVE)) {
                    //Toast.makeText(ctx, "This is live video", Toast.LENGTH_SHORT).show();
                   Helper.GoToVideoActivity(activity, item.getURL(), item.getVideo_type());

                }
                else {
                    Intent intent = new Intent(ctx, VideoDetail.class);
                    intent.putExtra(Const.DATA, item);
                    intent.putExtra(Const.TYPE, Const.VIDEO);
                    Toast.makeText(ctx, "This is normal video", Toast.LENGTH_SHORT).show();
                    //ctx.startActivity(intent);
                }*/

                if (TextUtils.isEmpty(item.getLive_url())) {
                    Intent intent = new Intent(ctx, VideoDetail.class);
                    intent.putExtra(Const.DATA, item);
                    intent.putExtra(Constants.Extras.TYPE, Const.VIDEO);
                    //Toast.makeText(ctx, "This is normal video", Toast.LENGTH_SHORT).show();
                    ctx.startActivity(intent);
                } else {
                    Intent intent = new Intent(ctx, VideoDetail.class);
                    intent.putExtra(Const.DATA, item);
                    intent.putExtra(Constants.Extras.TYPE, Const.VIDEO);
                    ctx.startActivity(intent);
/*                    String url="https://dams-apps-production.s3.ap-south-1.amazonaws.com/"+item.getLive_url();
                    Helper.GoToVideoActivity(activity, url, Const.VIDEO_LIVE);*/
                    // Toast.makeText(ctx, "This is live video", Toast.LENGTH_SHORT).show();
/*                    mService = Helper.getApi();
                        HashMap<String,String> param = new HashMap<>();
                        param.put(Const.NAME,item.getLive_url());
                        mService.getLiveVideoLink(param).enqueue(new Callback<LiveVideoResponse>() {
                            @Override
                            public void onResponse(Call<LiveVideoResponse> call, Response<LiveVideoResponse> response) {

                                //  Log.e(TAG, "onResponse:");

                                if (response.body()!=null){
                                    //LiveVideoResponse liveVideoResponse = response.body();
                                   // Log.e("sliderAdapter", "onResponse: "+response.body().getData().getLink());
                                   // String url="https://dams-apps-production.s3.ap-south-1.amazonaws.com/vod/932589Sumer-sir-presentation.m3u8";
                                    Helper.GoToVideoActivity(activity, "http://d1wspjxps4juxd.cloudfront.net/"+item.getLive_url(), Const.VIDEO_LIVE_MULTI);
                                }
                                else {
                                    Toast.makeText(activity, "Data not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LiveVideoResponse> call, Throwable throwable) {

                                Toast.makeText(activity, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });*/

                }
            }
        });


        Glide.with(activity).load((products.get(position)).getThumbnail_url()).into(mImageView);

        if (products.get(position).getIs_new().equals("1")) {
            featured_tag.setImageResource(R.mipmap.newtag);
        } else if (products.get(position).getFeatured().equals("1")) {

            featured_tag.setImageResource(R.mipmap.featured);
        }
        if (!TextUtils.isEmpty(item.getLive_url())) {
            featured_tag.setImageResource(R.mipmap.featured);
        }

/*        if (item.getVideo_type().equals(Const.VIDEO_LIVE)) {
            featured_tag.setImageResource(R.mipmap.live_tag);

        }*/

        //  }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }


    public void ItemChangedatVideoId(Video videoresponse) {
        int i = 0;
        if (videoresponse != null) {

            while (i < products.size()) {
                if ((products.get(i).getId()).equals(videoresponse.getId())) {
                    products.set(i, videoresponse);
                    //                    notifyDataSetChanged();
                    instantiateItem(container, i);
//                    notifyItemChanged(i, videoresponse);
                    i = products.size();
                }
                i++;
            }

        }
    }
}
