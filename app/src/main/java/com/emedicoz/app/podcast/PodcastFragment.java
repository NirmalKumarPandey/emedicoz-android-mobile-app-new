package com.emedicoz.app.podcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

interface MediaPrepared {
    void onMediaPrepared(Podcast pod);
}

/**
 * A simple {@link Fragment} subclass.
 */

public class PodcastFragment extends Fragment {

    private static final String TAG = "PodcastFragment";
    private static MediaPlayer mediaPlayer;
    public SwipeRefreshLayout swipeRefreshLayout;
    public PodcastAdapter adapter;
    private RelativeLayout podcastFilter;
    int firstVisibleItem;
    int visibleItemCount;
    public int previousTotalItemCount;
    int totalItemCount;
    public String lastPostId = "";
    public Activity activity;
    LinearLayoutManager layoutManager;
    int isAlreadyConnected = 0;
    ArrayList<Podcast> podcastList = new ArrayList<>();
    Progress mProgress;
    private RecyclerView recyclerView;
    private final Handler handler = new Handler();
    private RelativeLayout layoutPlayer;
    private ImageView btnPlay;
    private TextView txvCurrentPos, txvDuration;
    private SeekBar seekBar;
    private ProgressBar loader;
    private boolean loading = true;
    private int visibleThreshold = 3;
    private int total_item_count = 0;
    private boolean bookmarkedListScreen;
    private TextView textNoContent, txvTitle;
    private ImageView imgNoBookmarks;
    private Spinner authorSpinner;
    private List<Author> authorList;
    private String selectedAuthorId;
    private View createPodcast;

    public static PodcastFragment newInstance(boolean bookMarkList) {

        PodcastFragment fragment = new PodcastFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.BOOKMARK_LIST, bookMarkList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            bookmarkedListScreen = getArguments().getBoolean(Const.BOOKMARK_LIST);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        activity = getActivity();

    }

    private void getPodcastAuthorList() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.getPodcastAuthorData(SharedPreference.getInstance().getLoggedInUser().getId(),
                ApiClient.getStreamId(SharedPreference.getInstance().getLoggedInUser()));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (activity.isFinishing()) return;
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray jsonObject1 = GenericUtils.getJsonArray(jsonResponse);

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Author>>() {
                            }.getType();
                            authorList = gson.fromJson(jsonObject1.toString(), type);
                            setCreatePodcastFilterVisibility();
                            if (authorList.size() > 1)
                                podcastFilter.setVisibility(View.VISIBLE);
                            else
                                refreshPodcastList(false);
                        } else {
                            authorList = new ArrayList<>();
                            recyclerView.setVisibility(View.GONE);

                            imgNoBookmarks.setVisibility(View.VISIBLE);
                            textNoContent.setVisibility(View.VISIBLE);
                            textNoContent.setText(getString(R.string.no_data_found));
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }

                        ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(activity, R.layout.single_row_spinner_item, getAuthorList(authorList));
                        authorSpinner.setAdapter(authorAdapter);
                    } catch (JSONException e) {
                        refreshPodcastList(false);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                refreshPodcastList(false);
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void setCreatePodcastFilterVisibility() {
        boolean show = false;
        for (Author author : authorList) {
            if (author.getId() != null && author.getId().equals(SharedPreference.getInstance().getLoggedInUser().getId())) {
                show = true;
                break;
            }
        }

        if (show)
            createPodcast.setVisibility(View.VISIBLE);
        else
            createPodcast.setVisibility(View.GONE);
    }

    private ArrayList<String> getAuthorList(List<Author> list) {
        ArrayList<String> authorList = new ArrayList<>();
        authorList.add("All");
        for (Author temp : list) {
            authorList.add(temp.getName());
        }

        return authorList;
    }

    @Override
    public void onPause() {
        resetMediaPlayer();
        super.onPause();
    }

    private void resetMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = null;
            eMedicozApp.getInstance().setPodcastPlayer(mediaPlayer);
            layoutPlayer.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.podcast_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getDataOnRecyclerViewScroll();

    }

    private void initView(View view) {
        podcastList = new ArrayList<>();
        layoutPlayer = view.findViewById(R.id.layout_player);
        btnPlay = view.findViewById(R.id.play);
        loader = view.findViewById(R.id.loader);
        seekBar = view.findViewById(R.id.seek_bar);
        txvCurrentPos = view.findViewById(R.id.txv_current_position);
        txvDuration = view.findViewById(R.id.txv_podcast_duration);
        textNoContent = view.findViewById(R.id.textnocontent);
        txvTitle = view.findViewById(R.id.txv_title);
        imgNoBookmarks = view.findViewById(R.id.imgnobookmarks);
        authorSpinner = view.findViewById(R.id.author_spinner);
        createPodcast = view.findViewById(R.id.create_podcast);
        podcastFilter = view.findViewById(R.id.podcast_filter);

        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this::pullToRefresh);

        authorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    selectedAuthorId = authorList.get(i - 1).getId();
                else
                    selectedAuthorId = "";
                podcastList.clear();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                resetMediaPlayer();
                refreshPodcastList(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekChange(seekBar);
            }
        });
        btnPlay.setOnClickListener(view1 -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.exo_play);
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.exo_pause);
            }

            startPlayProgressUpdater();
        });

        view.findViewById(R.id.ffwd).setOnClickListener(view1 -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 11000);
                startPlayProgressUpdater();
            }
        });

        createPodcast.setOnClickListener(view1 -> {
            Helper.GoToAddPodcastScreen(activity);
        });

        view.findViewById(R.id.rew).setOnClickListener(view1 -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                startPlayProgressUpdater();
            }
        });

        view.findViewById(R.id.btn_cross).setOnClickListener(view1 -> {
            resetMediaPlayer();
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                int firstPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0 && !swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        });
    }

    public void startPlayProgressUpdater() {
        if (mediaPlayer == null) return;
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        txvCurrentPos.setText(getTimeString(mediaPlayer.getCurrentPosition()));

        if (mediaPlayer.isPlaying()) {
            Runnable notification = () -> startPlayProgressUpdater();
            handler.postDelayed(notification, 1000);
        } else {
            btnPlay.setImageResource(R.drawable.exo_play);
        }
    }

    // This is event handler thumb moving event
    private void seekChange(View v) {
        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
            txvCurrentPos.setText(getTimeString(sb.getProgress()));
            startPlayProgressUpdater();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getTimeString(long millis) {
        StringBuilder buf = new StringBuilder();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        if (hours != 0) {
            buf.append(String.format("%02d", hours))
                    .append(":")
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));
        } else
            buf.append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));

        return buf.toString();
    }

    private void getDataOnRecyclerViewScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (showBookmarkedList) return;

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

              /*  if (totalItemCount >= total_item_count) { // if the list is more than 25 then only pagination will work
                    visibleThreshold = total_item_count * 20 / 100;
                    if (loading && totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        if (!GenericUtils.isListEmpty(arrayList) && arrayList.size() > totalItemCount - 1) {
                            lastPostId = arrayList.get(totalItemCount - 1).getId();
                        }

                        if (isAlreadyConnected == 0) {
                            refreshPodcastList(false); // at the time of pagination
                            isAlreadyConnected = 1;
                        }
                        loading = true;
                    }
                }*/
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof BaseABNavActivity) {

            ((BaseABNavActivity) activity).manageToolbar(Constants.ScreenName.PODCAST);
            BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
            BaseABNavActivity.postFAB.setVisibility(View.GONE);

            if (bookmarkedListScreen)
                ((BaseABNavActivity) activity).bookmarkIV.setVisibility(View.GONE);
        }

        if (bookmarkedListScreen) {
            authorSpinner.setVisibility(View.GONE);
            txvTitle.setVisibility(View.GONE);
            createPodcast.setVisibility(View.GONE);
            refreshPodcastList(false);
        } else {
            authorSpinner.setVisibility(View.VISIBLE);
            txvTitle.setVisibility(View.VISIBLE);
//            createPodcast.setVisibility(View.VISIBLE);
            getPodcastAuthorList();
        }
    }

    public void pullToRefresh() {
        podcastList = new ArrayList<>();
        lastPostId = "";
        previousTotalItemCount = 0;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        refreshPodcastList(true); // for pull to refresh
    }

    public void networkCallForPodcastList() {

        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.getPodcastData(SharedPreference.getInstance().getLoggedInUser().getId(),
                selectedAuthorId,"","1");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (activity.isFinishing()) return;
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        isAlreadyConnected = 0;
                        Log.e(TAG, " networkcallForSingleCatVideoData onResponse: " + jsonResponse);
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONObject jsonObject1 = GenericUtils.getJsonObject(jsonResponse);
                            String podcastListData = jsonObject1.getString("my_podcast");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Podcast>>() {
                            }.getType();
                            List<Podcast> list = gson.fromJson(podcastListData, type);

                            if (TextUtils.isEmpty(lastPostId)) {
                                podcastList = new ArrayList<>();
                            }
                            podcastList.addAll(list);
                            total_item_count = podcastList.size();

                            recyclerView.setVisibility(View.VISIBLE);
                            textNoContent.setVisibility(View.GONE);
                            imgNoBookmarks.setVisibility(View.GONE);
                            initPodcastAdapter();
                        } else {
                            if (isAdded()) {
                                if (TextUtils.isEmpty(lastPostId)) {
//                                    Helper.showErrorLayoutForNoNav("PODCAST", activity, 1, 0);
                                    recyclerView.setVisibility(View.GONE);

                                    imgNoBookmarks.setVisibility(View.VISIBLE);
                                    textNoContent.setVisibility(View.VISIBLE);
                                    textNoContent.setText(getString(R.string.no_data_found));
                                } else {
                                    initPodcastAdapter();
                                }
                            }
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    public void networkCallForBookmarkedPodcastList() {

        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.getBookmarkedPodcastData(SharedPreference.getInstance().getLoggedInUser().getId(),
                selectedAuthorId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (activity.isFinishing() && !isAdded()) return;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        isAlreadyConnected = 0;
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray jsonObject1 = GenericUtils.getJsonArray(jsonResponse);

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Podcast>>() {
                            }.getType();
                            List<Podcast> list = gson.fromJson(jsonObject1.toString(), type);

                            if (TextUtils.isEmpty(lastPostId)) {
                                podcastList = new ArrayList<>();
                            }
                            podcastList.addAll(list);
                            total_item_count = podcastList.size();

                            initPodcastAdapter();
                        } else {
                            if (isAdded()) {
                                if (TextUtils.isEmpty(lastPostId)) {
                                    recyclerView.setVisibility(View.GONE);
//                                    Helper.showErrorLayoutForNoNav("PODCAST", activity, 1, 0);
                                    textNoContent.setText(String.format("No Bookmarks Found ! \n\nAll your Bookmarked %s will be available here !", "podcasts"));
                                    textNoContent.setVisibility(View.VISIBLE);
                                    imgNoBookmarks.setVisibility(View.VISIBLE);
                                } else {
                                    initPodcastAdapter();
                                }
                            }
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    protected void initPodcastAdapter() {
//        if (TextUtils.isEmpty(lastPostId) && adapter == null) {
        adapter = new PodcastAdapter(podcastList, bookmarkedListScreen, activity, pod -> loadPlayerWithURL(pod), new PodcastDeleteInterface() {
            @Override
            public void podcastDeleteCallback(int count) {
                if(count == 0){
                    recyclerView.setVisibility(View.GONE);
                    textNoContent.setText(String.format("No Bookmarks Found ! \n\nAll your Bookmarked %s will be available here !", "podcasts"));
                    textNoContent.setVisibility(View.VISIBLE);
                    imgNoBookmarks.setVisibility(View.VISIBLE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
//        } else {
//            adapter.notifyDataSetChanged();
//        }
    }

    public void loadPlayerWithURL(Podcast pod) {

        layoutPlayer.setVisibility(View.VISIBLE);
        mediaPlayer = eMedicozApp.getInstance().getPodcastPlayer();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            eMedicozApp.getInstance().setPodcastPlayer(mediaPlayer);
        }

        ((TextView) layoutPlayer.findViewById(R.id.txv_podcast_title)).setText(pod.getTitle());
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(GenericUtils.isEmpty(pod.getDownloadedUrl()) ? pod.getUrl() : pod.getDownloadedUrl());
            mediaPlayer.prepareAsync();
            loader.setVisibility(View.VISIBLE);
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                loader.setVisibility(View.GONE);
                btnPlay.setImageResource(R.drawable.exo_pause);

                seekBar.setMax(mediaPlayer.getDuration());
                txvDuration.setText(getTimeString(mediaPlayer.getDuration()));
                startPlayProgressUpdater();
//                pod.setIsPrepared(true);
//                adapter.notifyDataSetChanged();
            });

            mediaPlayer.setOnCompletionListener(mediaPlayer -> btnPlay.setImageResource(R.drawable.exo_play));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshPodcastList(boolean show) {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(show);
        }

        if (bookmarkedListScreen)
            networkCallForBookmarkedPodcastList();
        else
            networkCallForPodcastList();
    }
}