package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class ImageRVAdapter extends RecyclerView.Adapter<ImageRVAdapter.ViewHolder> {

    private static final String TAG = "ImageRVAdapter";
    ArrayList<MediaFile> imageArrayBM;
    Activity activity;
    Bitmap bitmap;


    public ImageRVAdapter(Activity activity, ArrayList<MediaFile> imageArrayBM) {
        this.imageArrayBM = imageArrayBM;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_newpost_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MediaFile image = imageArrayBM.get(position);
        Log.e(TAG, "onBindViewHolder: " + image.getImage());
        if (image.getImage() == null) {
            holder.imageIV.setScaleType(ImageView.ScaleType.FIT_CENTER);

            if (image.getFile_type().equals(Const.VIDEO)) {
                if (image.getFile().contains(Helper.getS3EndPoint()))
                    Glide.with(activity).load(image.getFile()).into(holder.imageIV);
                else
                    Glide.with(activity).load(Helper.getS3EndPoint() + Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES +
                            "/" + image.getFile().split(".mp4")[0] + ".png")
                            .into(holder.imageIV);
            } else {
                Glide.with(activity)
                        .asBitmap()
                        .load(image.getFile())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.bg))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                holder.imageIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                holder.imageIV.setImageBitmap(result);
                            }
                        });
            }
        } else
            holder.imageIV.setImageBitmap(image.getImage());

        holder.deleteIV.setTag(holder);
        holder.deleteIV.setOnClickListener(v -> {
            ViewHolder holder1 = (ViewHolder) v.getTag();

            // if post it getting edited each old mediafile will have ids. if we will remove that id we have to send it to server to remove them from db.

            if (!TextUtils.isEmpty(imageArrayBM.get(holder1.getAdapterPosition()).getId()))
                ((PostActivity) activity).addDeletedIdToMeta(imageArrayBM.get(holder1.getAdapterPosition()).getId());

            imageArrayBM.remove(holder1.getAdapterPosition());
            notifyItemRemoved(holder1.getAdapterPosition());
            if (imageArrayBM.isEmpty()) {
                ((PostActivity) activity).isImageAdded = false;
                ((PostActivity) activity).isVideoAdded = false;
                ((PostActivity) activity).isYoutubeVideoAttached = false;
                ((PostActivity) activity).isAttachmentAdded = false;
            }
        });
        // setting the name of the file
        if (image.getFile_type().equals(Const.PDF) || image.getFile_type().equals(Const.DOC) || image.getFile_type().equals(Const.XLS)) {
            holder.nameTV.setVisibility(View.VISIBLE);
            holder.nameTV.setText(image.getFile_name());
        } else holder.nameTV.setVisibility(View.GONE);

        //setting the play button if it is video
        if (image.getFile_type().equals(Const.VIDEO)) {
            holder.playIV.setVisibility(View.VISIBLE);
        } else holder.playIV.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return imageArrayBM.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageIV, deleteIV, playIV;

        TextView nameTV;

        public ViewHolder(final View view) {
            super(view);

            nameTV = view.findViewById(R.id.nameTV);

            playIV = view.findViewById(R.id.playIV);
            imageIV = view.findViewById(R.id.imageIV);
            deleteIV = view.findViewById(R.id.deleteIV);

        }

    }
}
