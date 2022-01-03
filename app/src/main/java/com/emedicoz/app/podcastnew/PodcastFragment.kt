package com.emedicoz.app.podcastnew

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.PodcastfragmentnewBinding
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.podcast.Author
import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.podcastnew.adapter.PodcastChannelAdapter
import com.emedicoz.app.podcastnew.callback.OnPodcastBookmarkClick
import com.emedicoz.app.ui.podcast.Adapter.PodcastRecycleViewAdapter
import com.emedicoz.app.ui.podcast.Adapter.SpacesItemDecoration
import com.emedicoz.app.ui.podcast.ViewHolder.PodcastRecycleViewHolder
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.SharedPreference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PodcastFragment : Fragment(), ProgressUpdateListner, OnPodcastBookmarkClick {


    private val ARG_SECTION_NUMBER = "section_number"

    private var mPodcastViewModel: PodcastViewModel? = null
    private var binding: PodcastfragmentnewBinding? = null
    private var mProgressBar: ProgressBar? = null
    private var authorSpinner: Spinner? = null
    var userID: String? = null
    var mRecyclerView: RecyclerView? = null
    var mProgressListner: ProgressUpdateListner? = null

    var authorList: List<Author>? = ArrayList()
    var selectedAuthorId = ""
    var podcastType: String = ""
    var mAdapter: PodcastRecycleViewAdapter? = null
    var filterType: String = ""
    var episodePodcastList: List<Podcast> = ArrayList<Podcast>()
    var bookmarkedPodcastList: List<Podcast>? = null
    private var fromRecentlyAdded: Boolean = false
    lateinit var linearLayoutManager: LinearLayoutManager
    private val isLoading = false
    private val isLastPage = false
    var pageNumber = 1
    private var loading = true
    private val visibleThreshold = 5
    var previousTotalItemCount = 0
    var type: Int = 0
    var firstVisibleItem = 0
    var totalItemCount = 0
    var visibleItemCount = 0

    companion object {
        @JvmStatic
        fun newInstance(param: Int) =
            PodcastFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, param)
                }
            }

        @JvmStatic
        fun newInstance(podcastType: String) =
            PodcastFragment().apply {
                arguments = Bundle().apply {
                    putString("PODCAST_TYPE", podcastType)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPodcastViewModel = ViewModelProvider(this).get(PodcastViewModel::class.java)
        var index = 1
        if (arguments != null) {
            arguments?.let {
                index = it.getInt(ARG_SECTION_NUMBER)
                podcastType = it.getString("PODCAST_TYPE")!!
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PodcastfragmentnewBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        if (podcastType == Const.CHANNEL) {
            binding!!.filterLL.visibility = View.GONE
            binding!!.recentAdded.visibility = View.GONE
            binding!!.filterlayout.visibility = View.GONE
        } else {
            binding!!.filterLL.visibility = View.VISIBLE
            binding!!.recentAdded.visibility = View.VISIBLE
            binding!!.filterlayout.visibility = View.VISIBLE
        }
        binding!!.filtertilte.setOnClickListener { showPopMenu(it) }
        mProgressBar = binding!!.progressBar
        val textView: TextView = binding!!.sectionLabel

        observeViewModelData()
        userID = SharedPreference.getInstance().loggedInUser.id
        mProgressBar!!.visibility = View.VISIBLE
        mRecyclerView = binding!!.RecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.layoutManager = linearLayoutManager
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.addItemDecoration(SpacesItemDecoration(8))
        loadApiData()
        binding!!.recentAdded.setOnClickListener {
            clearPaginationValues()
            pageNumber = 1
            fromRecentlyAdded = true
            type = 0
            mAdapter = null
            filterType = "recent"
            if (podcastType.equals(Const.EPISODE, false)) {
                mPodcastViewModel!!.loadPoadcastData(
                    activity,
                    this,
                    userID,
                    selectedAuthorId,
                    filterType,
                    pageNumber
                )
            } else if (podcastType.equals(Const.BOOKMARK, false)) {
                mPodcastViewModel!!.loadBookmarkedPoadcastData(
                    activity,
                    this,
                    userID,
                    selectedAuthorId,
                    filterType,
                    pageNumber
                )
            }
        }
        setPagination()
        return root
    }

    private fun observeViewModelData() {
        if (podcastType.equals(Const.CHANNEL, false)) {
            mPodcastViewModel!!.authorList.observe(viewLifecycleOwner,
                Observer { s ->
                    authorList = s
                    initialize_Author_List(s)
                })
        }
        if (podcastType.equals(Const.EPISODE, false)) {
            mPodcastViewModel!!.getpodcastList().observe(viewLifecycleOwner,
                Observer { s ->
                    if (type == 0) {
                        episodePodcastList = ArrayList<Podcast>()
                    }
                    (episodePodcastList as MutableList).addAll(s)
                    initialize_list(episodePodcastList)
                })
        }

        if (podcastType.equals(Const.BOOKMARK, false)) {
            mPodcastViewModel!!.getBookmarkedPodcastList().observe(viewLifecycleOwner,
                Observer { s ->
                    if (type == 0) {
                        bookmarkedPodcastList = ArrayList<Podcast>()
                    }
                    (bookmarkedPodcastList as MutableList).addAll(s)
                    initialize_list(bookmarkedPodcastList)
                })
        }
    }

    private fun loadApiData() {
        fromRecentlyAdded = false
        if (podcastType.equals(Const.CHANNEL, false))
            mPodcastViewModel!!.loadPoadcastAuthorList(
                activity,
                userID,
                selectedAuthorId,
                this,
                filterType
            )
        if (podcastType.equals(Const.EPISODE, false))
            mPodcastViewModel!!.loadPoadcastData(
                activity,
                this,
                userID,
                selectedAuthorId,
                filterType,
                pageNumber
            )
        if (podcastType.equals(Const.BOOKMARK, false))
            mPodcastViewModel!!.loadBookmarkedPoadcastData(
                activity,
                this,
                userID,
                selectedAuthorId,
                filterType,
                pageNumber
            )
    }

    private fun setPagination() {
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = linearLayoutManager.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                if (dy > 0) {
                    if (loading) {
                        if (totalItemCount > previousTotalItemCount) {
                            loading = false
                            previousTotalItemCount = totalItemCount
                        }
                    }
                    if (!loading && totalItemCount - visibleItemCount
                        <= firstVisibleItem + visibleThreshold
                    ) {
                        // End has been reached
                        Log.i("Yaeye!", "end called")
                        // Do something
                        pageNumber++
                        type = 1
                        loadApiData()
                        loading = true
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun update(b: Boolean) {
        mProgressBar!!.visibility = View.GONE
    }

    private fun initialize_list(mList: List<Podcast>?) {
        Log.e("TAG", "initialize_list: ")
        mRecyclerView!!.itemAnimator = null
        if (mAdapter == null) {
            mAdapter = PodcastRecycleViewAdapter(activity, mList, this)
            mRecyclerView!!.adapter = mAdapter
        } else {
            mAdapter?.notifyDataSetChanged()
        }
    }


    private fun initialize_Author_List(authorList: List<Author>?) {
        val podcastChannelAdapter = PodcastChannelAdapter(requireActivity(), authorList!!)
        mRecyclerView?.adapter = podcastChannelAdapter
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
                    requireActivity(),
                    R.drawable.ic_baseline_bookmark_white
                )
            )
        } else {
            podcast.is_bookmarked = "0"
            podcastRecycleViewHolder.imgBookmark.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_baseline_bookmark_border
                )
            )
        }
        mAdapter?.notifyDataSetChanged()
        mPodcastViewModel!!.networkCallForBookmark(requireActivity(), podcast.id)
    }

    fun showPopMenu(view: View) {
        val popup: PopupMenu? = activity?.let { PopupMenu(it, view) }
        popup?.apply {
            inflate(R.menu.podcast_filter_menu)
        }?.show()

        popup?.setOnMenuItemClickListener { item ->
            clearPaginationValues()
            fromRecentlyAdded = false
            pageNumber = 1
            type = 0
            when (item.itemId) {
                R.id.mostViewedRC -> {
                    filterType = "mostviewed"
                }
                R.id.stlRC -> {
                    filterType = "length"
                }
                R.id.otnRC -> {
                    filterType = "oldest"
                }
                R.id.channelWiseRC -> {
                    filterType = "channel"
                }
                R.id.lastMonthRC -> {
                    filterType = "last_month"
                }
            }
            mAdapter = null
            if (podcastType.equals(Const.EPISODE, false)) {
                mPodcastViewModel!!.loadPoadcastData(
                    activity,
                    this,
                    userID,
                    selectedAuthorId,
                    filterType,
                    pageNumber
                )
            } else if (podcastType.equals(Const.CHANNEL, false)) {
                mPodcastViewModel!!.loadPoadcastAuthorList(
                    activity,
                    userID,
                    selectedAuthorId,
                    this,
                    filterType
                )
            } else if (podcastType.equals(Const.BOOKMARK, false)) {
                mPodcastViewModel!!.loadBookmarkedPoadcastData(
                    activity,
                    this,
                    userID,
                    selectedAuthorId,
                    filterType,
                    pageNumber
                )
            }
            true
        }
    }

    private fun clearPaginationValues() {
        firstVisibleItem = 0
        previousTotalItemCount = 0
        visibleItemCount = 0
    }

    private fun addCreatedAtTimeStamp(podcastList: MutableList<Podcast>) {
        for (podcast in podcastList) {
            podcast.createdAtTimestamp = getTimestampFromDate(podcast.createdAt)
        }
        Collections.sort(podcastList, FilterRecentlyAddedComparator())
        podcastList.reverse();
        mAdapter?.notifyDataSetChanged()
    }

    class FilterRecentlyAddedComparator : Comparator<Podcast?> {
        override fun compare(left: Podcast?, right: Podcast?): Int {
            return left!!.createdAtTimestamp.compareTo(right!!.createdAtTimestamp)
        }
    }

    private fun getTimestampFromDate(createdAt: String): Long {
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd KK:mm:ss")
        val date = formatter.parse(createdAt) as Date
        return date.time
    }

}