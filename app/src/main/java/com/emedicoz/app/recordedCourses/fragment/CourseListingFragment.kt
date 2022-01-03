package com.emedicoz.app.recordedCourses.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.databinding.CourseListingFragmentBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.recordedCourses.model.CourseListingViewModel
import com.emedicoz.app.templateAdapters.CourseListingAdapter
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants

class CourseListingFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(fragType: String, id: String): CourseListingFragment {
            val fragment = CourseListingFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            args.putString(Constants.Extras.ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var wlBinding: CourseListingFragmentBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = wlBinding

    private lateinit var viewModel: CourseListingViewModel

    //    private lateinit var wishListAdapter: WishListAdapter
    private lateinit var courseListAdapter: CourseListingAdapter
    var recyclerViewState: Parcelable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        wlBinding = CourseListingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this).get(CourseListingViewModel::class.java)
        viewModel.courseId = arguments?.getString(Constants.Extras.ID).toString()

        viewModel.publishResult.observe(viewLifecycleOwner, Observer<List<Course>> { list ->
            if (list == null || list.isEmpty()) {
                binding.textnocontent.visibility = View.VISIBLE
                if (arguments?.get(Const.FRAG_TYPE)?.equals("WISHLIST")!!) {
                    binding.imgnobookmarks.visibility = View.VISIBLE
                    binding.textnocontent.text = "No Wishlist Found ! \n\nAll your wishlisted course will be available here !"
                } else {
                    binding.textnocontent.text = getString(R.string.no_data_found)
                    binding.imgnobookmarks.visibility = View.GONE
                }
            } else {
                binding.imgnobookmarks.visibility = View.GONE
                binding.textnocontent.visibility = View.GONE
                setCourseListAdapter(list as ArrayList<Course>)
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        // Save state
        recyclerViewState = binding.courseListRecyclerView.layoutManager?.onSaveInstanceState()
        if (arguments?.get(Const.FRAG_TYPE)?.equals("WISHLIST")!!)
            viewModel.getUserWishList()
        else
            viewModel.getChildCourseList()

    }

    private fun setCourseListAdapter(courseList: ArrayList<Course>) {
//        wishListAdapter = WishListAdapter(wishLists, context!!)
        courseListAdapter = CourseListingAdapter(courseList, context!!, true, viewModel, Const.ALLCOURSES)
        binding.courseListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.courseListRecyclerView.adapter = courseListAdapter
        // Restore state
        binding.courseListRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CourseListingViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        wlBinding = null
    }

}