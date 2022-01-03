package com.emedicoz.app.video.ui.video

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.databinding.VideoFragmentBinding
import com.emedicoz.app.modelo.Video
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.video.adapter.BannerAdapter
import com.emedicoz.app.video.adapter.TagsAdapter
import com.emedicoz.app.video.adapter.VideoAdapter
import com.emedicoz.app.video.ui.models.TagResponse
import com.emedicoz.app.video.ui.models.VideoResponse

class VideoFragment : Fragment() {
    private var _binding: VideoFragmentBinding? = null
    private val binding get() = _binding!!

    private var tagsList = ArrayList<TagResponse.Data.AllTags>()
    var imageList = ArrayList<TagResponse.Data.VideoBanner>()

    var tagId: String = "0"
    var list: VideoResponse? = null
    private lateinit var viewModel: VideoViewModel
    var videoList = ArrayList<Video>()

    var videoAdapter: VideoAdapter? = null
    var currentPage = 1
    private var lastVideoId: String = ""
    lateinit var layoutManager: LinearLayoutManager
    private var isScrolling: Boolean = false
    var currentItems = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = VideoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, VideoFactory(VideoRepository())).get(VideoViewModel::class.java)
        videoList = ArrayList()
        if (activity is HomeActivity) {
            (activity as HomeActivity).item_search.isVisible = false
            (activity as HomeActivity).floatingActionButton.visibility = View.GONE
            (activity as HomeActivity).itemMyCartFragment.isVisible = false
            (activity as HomeActivity).toolbarHeader.visibility = View.GONE
        }

        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        } else {
            getTagsData()
        }

        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvVideo.layoutManager = layoutManager
        videoAdapter = VideoAdapter(requireActivity(), videoList)
        binding.rvVideo.adapter = videoAdapter

        /**
         * RecyclerView scroll listener method
         */

        binding.rvVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentItems = layoutManager.childCount
                totalItems = layoutManager.itemCount
                scrollOutItems = layoutManager.findFirstVisibleItemPosition()

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    if (videoList.size >= 10) {
                        isScrolling = false
                        lastVideoId = videoList[totalItems - 1].id
                        currentPage++
                        getVideoApi(tagId, lastVideoId, currentPage.toString())
                    }
                }
            }
        })


        /**
         * Pull to refresh method
         */
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            removeAllItems(tagId)
        }
    }

    private fun removeAllItems(tagId: String) {
        videoList.clear()
        lastVideoId = ""
        currentPage = 1
        getVideoApi(tagId, lastVideoId, currentPage.toString())
    }

    /**
     * Get banner and all tag
     */
    private fun getTagsData() {
        viewModel.getTags(SharedPreference.getInstance().loggedInUser.id, SharedPreference.getInstance().getString(Constants.SharedPref.STREAM_ID))

        viewModel.bannerResponse.observe(viewLifecycleOwner) {
            imageList = it as ArrayList<TagResponse.Data.VideoBanner>
            val adapter = BannerAdapter(requireActivity(), imageList)
            binding.imageSlider.setSliderAdapter(adapter)
            binding.imageSlider.isAutoCycle = true
            binding.imageSlider.startAutoCycle()
            adapter.notifyDataSetChanged()
        }

        viewModel.tagResponse.observe(viewLifecycleOwner) {
            val tags = TagResponse.Data.AllTags("0", "", "All", "", "", "")
            tagsList = it as ArrayList
            tagsList.add(0, tags)
            tagsList[0] = tags
            val tagsAdapter = TagsAdapter(requireActivity(), tagsList) { tagsId ->
                tagId = tagsId.id
                removeAllItems(tagId)
            }
            binding.rvTags.adapter = tagsAdapter
            removeAllItems(tagId)
        }
        showError()
    }

    /**
     * Get All video from server method
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun getVideoApi(tagId: String, lastVideoId: String, page: String) {
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.getAllVideo(SharedPreference.getInstance().loggedInUser.id, tagId, lastVideoId, page)
        viewModel.videoResponse.observe(viewLifecycleOwner, {
            if (isAdded) {
                val status = it.status
                if (status) {
                    if (lastVideoId == "" && currentPage == 1) {
                        videoList.clear()
                        videoAdapter?.notifyDataSetChanged()
                    }
                    videoList.addAll(it.data)
                    videoAdapter?.notifyDataSetChanged()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    /**
    Show error message from server
     */
    private fun showError() {
        viewModel.errorMessage.observe(viewLifecycleOwner, {
            videoList.clear()
            videoAdapter?.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = VideoFragment()
    }
}