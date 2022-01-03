package com.emedicoz.app

import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.emedicoz.app.databinding.ActivityPodcastNewBinding
import com.emedicoz.app.databinding.LayoutPodcastNewRevmpBinding
import com.emedicoz.app.podcast.Author
import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.podcastnew.PodcastFragment
import com.emedicoz.app.podcastnew.PodcastPagerAdapter
import com.emedicoz.app.ui.podcast.PodCastMediaPlayerFragment
import com.emedicoz.app.utilso.*
import com.google.android.material.tabs.TabLayout
import java.io.IOException
import java.lang.StringBuilder

class PodcastNewKotlin : Fragment(), View.OnClickListener, DialogInterface.OnClickListener {

    lateinit var binding: LayoutPodcastNewRevmpBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()
    var playbackSpeed: Float? = null
    private var menu:Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutPodcastNewRevmpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            episode.setOnClickListener(this@PodcastNewKotlin)
            channel.setOnClickListener(this@PodcastNewKotlin)
            bookmark.setOnClickListener(this@PodcastNewKotlin)
            btnCross.setOnClickListener(this@PodcastNewKotlin)
            ffwd.setOnClickListener(this@PodcastNewKotlin)
            rew.setOnClickListener(this@PodcastNewKotlin)
            play.setOnClickListener(this@PodcastNewKotlin)
        }
        addFragment(PodcastFragment.newInstance(Const.EPISODE))
        changeBackground(Const.EPISODE)

        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    seekChange(seekBar)
                }
            })

        if (SharedPreference.getInstance().masterHitResponse.user_detail.is_add_podcast != null && SharedPreference.getInstance().masterHitResponse.user_detail.is_add_podcast == "1") {
            binding.createPodcast.visibility = View.VISIBLE
        } else {
            binding.createPodcast.visibility = View.GONE
        }

        binding.createPodcast.setOnClickListener {
            Helper.GoToAddPodcastScreen(requireActivity())
        }
        binding.playbackSpeed.setOnClickListener {
            val optionsArr = resources.getStringArray(R.array.PlaybackSpeed)
            val alertDialog: AlertDialog =
                AlertDialog.Builder(requireActivity(), R.style.MyDialogTheme)
                    .setTitle("Select playback speed")
                    .setItems(optionsArr, this)
                    .create()
            alertDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is HomeActivity) {

            val actionBar = (activity as HomeActivity).supportActionBar
            actionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.episode -> {
                changeBackground(Const.EPISODE)
                addFragment(PodcastFragment.newInstance(Const.EPISODE))
            }
            R.id.channel -> {
                changeBackground(Const.CHANNEL)
                addFragment(PodcastFragment.newInstance(Const.CHANNEL))
            }
            R.id.bookmark -> {
                changeBackground(Const.BOOKMARK)
                addFragment(PodcastFragment.newInstance(Const.BOOKMARK))
            }
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

    private fun changeBackground(type: String) {
        binding.apply {
            episode.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.transparent
                )
            )
            episode.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.progress_start
                )
            )

            channel.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.transparent
                )
            )
            channel.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.progress_start
                )
            )

            bookmark.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.transparent
                )
            )
            bookmark.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.progress_start
                )
            )

            when (type) {
                Const.EPISODE -> {
                    episode.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.bg_podcast_tab)
                    episode.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                }

                Const.CHANNEL -> {
                    channel.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.bg_podcast_tab)
                    channel.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                }

                Const.BOOKMARK -> {
                    bookmark.background =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.bg_podcast_tab)
                    bookmark.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                }
            }
        }
    }

    fun addFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .add(R.id.podcastContainer, fragment)
            .commitAllowingStateLoss()
    }

    fun replaceFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.podcastContainer, fragment)
            .commitAllowingStateLoss()
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
                binding.txvPodcastDuration.text = getTimeString(mediaPlayer.duration.toLong())
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
            } else {
                binding.playbackSpeed.visibility = View.GONE
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startPlayProgressUpdater() {
        if (mediaPlayer == null) return
        binding.seekBar.progress = mediaPlayer?.currentPosition!!
        binding.txvCurrentPosition.text = getTimeString(mediaPlayer?.currentPosition?.toLong()!!)
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
            startPlayProgressUpdater()
        }
    }

    fun getCurrentFragment():Fragment {
        return childFragmentManager.findFragmentById(R.id.podcastContainer)!!
    }

    companion object{
        @JvmStatic
        fun newInstance():PodcastNewKotlin{
            return PodcastNewKotlin()
        }
    }

}