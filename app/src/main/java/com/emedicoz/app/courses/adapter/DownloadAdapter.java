package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.Model.offlineData;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.fragment.DownloadedDataListFragment;
import com.emedicoz.app.epubear.ePubActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {
    Activity activity;
    String type;
    ArrayList<File> downloadedFiles;
    ArrayList<offlineData> offlineDataArrayList;
    String[] ids;
    private DownloadedDataListFragment.MediaPrepared mediaPrepared;

    public DownloadAdapter(Activity activity, ArrayList<File> downloadedFiles, ArrayList<offlineData> offlineDataArrayList,
                           String type, DownloadedDataListFragment.MediaPrepared mediaPrepared) {
        this.activity = activity;
        this.downloadedFiles = downloadedFiles;
        this.type = type;
        this.offlineDataArrayList = offlineDataArrayList;
        this.mediaPrepared = mediaPrepared;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_download, viewGroup, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder downloadViewHolder, final int position) {
        Log.e("onBindViewHolder: ", offlineDataArrayList.get(position).getLink() + " " + offlineDataArrayList.get(position).getId());

        ids = offlineDataArrayList.get(position).getId().split("_");

        if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.VIDEO)) {
            downloadViewHolder.fileIV.setImageBitmap(ThumbnailUtils.createVideoThumbnail(activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink(), MediaStore.Video.Thumbnails.MICRO_KIND));
            downloadViewHolder.downloadFileNameTV.setText(getVideoFileName(offlineDataArrayList.get(position).getLink()));

        } else if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.EPUB)) {
            downloadViewHolder.fileIV.setImageResource(R.mipmap.epub_new_course);
            downloadViewHolder.downloadFileNameTV.setText(getDocFileName(offlineDataArrayList.get(position).getLink()));

        } else if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.PDF)) {
            downloadViewHolder.fileIV.setImageResource(R.mipmap.pdf);
            downloadViewHolder.downloadFileNameTV.setText(getDocFileName(offlineDataArrayList.get(position).getLink()));

        } else if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.PODCAST)) {
            downloadViewHolder.fileIV.setImageResource(R.mipmap.play_test);
            downloadViewHolder.downloadFileNameTV.setText(getDocFileName(offlineDataArrayList.get(position).getLink()));
        }

        downloadViewHolder.layoutRoot.setOnClickListener(v -> {
            if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.VIDEO)) {
                Log.e("TAG", "onClick: " + activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink());
                if ((activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink()).contains(".mp4")) {
                    Helper.GoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink(), Const.VIDEO_STREAM, ids[2], Const.COURSE_VIDEO_TYPE);
                } else {
                    Helper.DecryptAndGoToVideoActivity(activity, activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink(), Const.VIDEO_STREAM, ids[2], Const.COURSE_VIDEO_TYPE);
                }
            } else if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.EPUB)) {
                Intent intent = new Intent(activity, ePubActivity.class);
                intent.putExtra("filePath", activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink());
                activity.startActivity(intent);

            } else if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.PDF)) {
                Helper.openPdfActivity(activity, offlineDataArrayList.get(position).getLink(),
                        activity.getFilesDir() + "/" + offlineDataArrayList.get(position).getLink());

            } else if (type.equalsIgnoreCase(Constants.MY_DOWNLOAD_TABS.PODCAST)) {
                if (eMedicozApp.getInstance().getPodcastPlayer() == null) {
                    eMedicozApp.getInstance().setPodcastPlayer(new MediaPlayer());
                }
                mediaPrepared.onMediaPrepared(offlineDataArrayList.get(position));
            }
        });

        downloadViewHolder.moreIV.setOnClickListener(v -> showDeleteMenu(v, position));
    }


    private String getDocFileName(String strTitle) {
        String tempTitle = strTitle.contains("/") ? Objects.requireNonNull(strTitle).substring(strTitle.lastIndexOf("/") + 1) : strTitle;
        return tempTitle.replaceAll("\\d{5,10}", "").replaceAll("_", " ")
                .replace(".epub", "").replace(".pdf", "").replace(".mp3", "");
    }

    private String getVideoFileName(String strTitle) {
        String tempTitle = strTitle.contains("/") ? Objects.requireNonNull(strTitle).substring(strTitle.lastIndexOf("/") + 1) : strTitle;
        return tempTitle.replaceAll("_", " ").replace(".mp4", "");
    }

    private void showDeleteMenu(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.deleteMenu) {
                View view1 = Helper.newCustomDialog(activity, false, activity.getString(R.string.app_name), activity.getString(R.string.deleteMessage));

                Button btnCancel;
                Button btnSubmit;

                btnCancel = view1.findViewById(R.id.btn_cancel);
                btnSubmit = view1.findViewById(R.id.btn_submit);

                btnCancel.setText(activity.getString(R.string.cancel));
                btnSubmit.setText(activity.getString(R.string.delete));

                btnCancel.setOnClickListener(view -> Helper.dismissDialog());

                btnSubmit.setOnClickListener(view -> {
                    Helper.dismissDialog();
                    eMedicozDownloadManager.removeOfflineData(ids[2], offlineDataArrayList.get(position).getType(), activity, ids[1]);
                    offlineDataArrayList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(activity, "File Deleted Successfully", Toast.LENGTH_SHORT).show();
                });
                return true;
            }
            return false;
        });

        popup.inflate(R.menu.delete_menu);
        Menu menu = popup.getMenu();
        popup.show();
    }

    private void deleteVideo(int position, String name) {
        eMedicozDownloadManager.removeOfflineData(ids[2], offlineDataArrayList.get(position).getType(), activity, ids[1]);
        notifyDataSetChanged();
        Toast.makeText(activity, "File Deleted Successfully", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return offlineDataArrayList.size();
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {

        TextView downloadFileNameTV;
        CardView layoutRoot;
        ImageView fileIV, moreIV;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            downloadFileNameTV = itemView.findViewById(R.id.downloadFileNameTV);
            layoutRoot = itemView.findViewById(R.id.layout_root);
            fileIV = itemView.findViewById(R.id.fileIV);
            moreIV = itemView.findViewById(R.id.moreIV);
        }
    }
}
