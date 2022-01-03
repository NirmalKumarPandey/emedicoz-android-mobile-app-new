package com.emedicoz.app.podcastnew.activity

import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityPodcastByAuthorBinding
import com.emedicoz.app.databinding.LayoutPodcastNewRevmpBinding
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.podcastnew.PodcastFragment
import com.emedicoz.app.podcastnew.PodcastViewModel
import com.emedicoz.app.podcastnew.callback.OnPodcastBookmarkClick
import com.emedicoz.app.ui.podcast.Adapter.PodcastRecycleViewAdapter
import com.emedicoz.app.ui.podcast.Adapter.SpacesItemDecoration
import com.emedicoz.app.ui.podcast.ViewHolder.PodcastRecycleViewHolder
import com.emedicoz.app.utilso.*
import java.io.IOException
import java.lang.StringBuilder

class PodcastByAuthor : AppCompatActivity(), View.OnClickListener, OnPodcastBookmarkClick,
    ProgressUpdateListner, DialogInterface.OnClickListener {
    lateinit var binding: ActivityPodcastByAuthorBinding
    private var mediaPlayer: MediaPlayer? = null
    private var mPodcastViewModel: PodcastViewModel? = null
    private val handler = Handler()
    private var adapter: PodcastRecycleViewAdapter? = null
    private var id: String? = null
    var playbackSpeed: Float? = null

    // var  ActivityPostBinding mbinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityPodcastByAuthorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getStringExtra("id")
        Log.e(TAG, "onCreate: $id")
        val linearLayoutManager = LinearLayoutManager(this)
        binding.podcastByAuthorRV.layoutManager = linearLayoutManager
        binding.podcastByAuthorRV.setHasFixedSize(true)
        binding.podcastByAuthorRV.addItemDecoration(SpacesItemDecoration(8))
        mPodcastViewModel = ViewModelProvider(this).get(PodcastViewModel::class.java)
        mPodcastViewModel!!.getPodcastByUserId().observe(this, Observer {
            initializeList(it)
        })

        mPodcastViewModel!!.loadAuthorPodcastById(
            this,
            this,
            id
        )
        binding.apply {
            btnCross.setOnClickListener(this@PodcastByAuthor)
            ffwd.setOnClickListener(this@PodcastByAuthor)
            rew.setOnClickListener(this@PodcastByAuthor)
            play.setOnClickListener(this@PodcastByAuthor)
        }

        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    seekChange(seekBar)
                }
            })
        binding.toolbarPod.toolbarBackIV.setOnClickListener {
            onBackPressed()
        }

        binding.toolbarPod.toolbarTitleTV.text = "Podcast"

        binding.playbackSpeed.setOnClickListener {
            val optionsArr = resources.getStringArray(R.array.PlaybackSpeed)
            val alertDialog: AlertDialog =
                AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle("Select playback speed")
                    .setItems(optionsArr, this)
                    .create()
            alertDialog.show()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cross -> resetMediaPlayer()
            R.id.ffwd -> {
                if (mediaPlayer != null) {
                    mediaPlayer?.seekTo(mediaPlayer?.currentPosition!! + 11000)
                    startPlayProgressUpdater()
                }
            }
            R.id.rew -> {
                if (mediaPlayer != null) {
                    mediaPlayer?.seekTo(mediaPlayer?.currentPosition!! - 10000)
                    startPlayProgressUpdater()
                }
            }
            R.id.play -> {
                if (mediaPlayer?.isPlaying!!) {
                    mediaPlayer?.pause()
                    binding.play.setImageResource(R.drawable.exo_play)
                } else {
                    mediaPlayer?.start()
                    binding.play.setImageResource(R.drawable.exo_pause)
                }

                startPlayProgressUpdater()
            }
        }
    }

    fun loadPlayerWithURL(pod: Podcast) {
        binding.layoutPlayer.visibility = View.VISIBLE
        mediaPlayer =
            eMedicozApp.getInstance().podcastPlayer
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            eMedicozApp.getInstance().podcastPlayer =
                mediaPlayer
        }
        binding.txvPodcastTitle.text = pod.title
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(
                if (GenericUtils.isEmpty(
                        pod.downloadedUrl
                    )
                ) pod.url else pod.downloadedUrl
            )
            mediaPlayer?.prepareAsync()
            binding.loader.visibility = View.VISIBLE
            mediaPlayer?.setOnPreparedListener { mediaPlayer: MediaPlayer ->
                mediaPlayer.start()
                binding.loader.visibility = View.GONE
                binding.play.setImageResource(R.drawable.exo_pause)
                binding.seekBar.max = mediaPlayer.duration
                binding.txvPodcastDuration.setText(getTimeString(mediaPlayer.duration.toLong()))
                startPlayProgressUpdater()
            }
            mediaPlayer?.setOnCompletionListener {
                binding.play.setImageResource(
                    R.drawable.exo_play
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.playbackSpeed.visibility = View.VISIBLE
                mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(1.0f)!!
                binding.playbackSpeed.text = "1.0"
            }else{
                binding.playbackSpeed.visibility = View.GONE
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startPlayProgressUpdater() {
        if (mediaPlayer == null) return
        binding.seekBar.progress = mediaPlayer?.currentPosition!!
        binding.txvCurrentPosition.setText(getTimeString(mediaPlayer?.currentPosition?.toLong()!!))
        if (mediaPlayer?.isPlaying!!) {
            val notification = Runnable { startPlayProgressUpdater() }
            handler.postDelayed(notification, 1000)
        } else {
            binding.play.setImageResource(R.drawable.exo_play)
        }
    }

    // This is event handler thumb moving event
    private fun seekChange(v: View) {
        if (mediaPlayer?.isPlaying!!) {
            val sb = v as SeekBar
            mediaPlayer?.seekTo(sb.progress)
            binding.txvCurrentPosition.setText(getTimeString(sb.progress.toLong()))
            startPlayProgressUpdater()
        }
    }

    private fun getTimeString(millis: Long): String {
        val buf = StringBuilder()
        val hours = (millis / (1000 * 60 * 60)).toInt()
        val minutes = (millis % (1000 * 60 * 60) / (1000 * 60)).toInt()
        val seconds = (millis % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        if (hours != 0) {
            buf.append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds))
        } else buf.append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))
        return buf.toString()
    }

    private fun resetMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer = null
            eMedicozApp.getInstance().podcastPlayer = mediaPlayer
            binding.layoutPlayer.visibility = View.GONE
        }
    }

    override fun onPause() {
        resetMediaPlayer()
        super.onPause()
    }

    private fun initializeList(mList: List<Podcast>?) {
        adapter = PodcastRecycleViewAdapter(this, mList, this)
        binding.podcastByAuthorRV.itemAnimator = null
        binding.podcastByAuthorRV.adapter = adapter
    }

    override fun onPodcastBookmarkClick(
        podcast: Podcast,
        position: Int,
        podcastRecycleViewHolder: PodcastRecycleViewHolder
    ) {
        if (podcast.is_bookmarked == "0") {
            podcast.is_bookmarked = "1"
            podcastRecycleViewHolder.imgBookmark.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bookmark_white
                )
            )
        } else {
            podcast.is_bookmarked = "0"
            podcastRecycleViewHolder.imgBookmark.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bookmark_border
                )
            )
        }
        adapter?.notifyDataSetChanged()
        mPodcastViewModel!!.networkCallForBookmark(this, podcast.id)
    }

    override fun update(b: Boolean) {
        binding.progressBar.visibility = View.GONE
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            0 -> playbackSpeed = 0.5f
            1 -> playbackSpeed = 1.0f
            2 -> playbackSpeed = 1.25f
            3 -> playbackSpeed = 1.5f
            4 -> playbackSpeed = 1.75f
            5 -> playbackSpeed = 2.0f
            6 -> playbackSpeed = 2.5f
            7 -> playbackSpeed = 3.0f
            else -> {
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(playbackSpeed!!)!!
            binding.play.setImageResource(R.drawable.exo_pause)
            binding.playbackSpeed.text = playbackSpeed.toString()
        }
    }

    companion object {
        private const val TAG = "PodcastByAuthor"
    }

}