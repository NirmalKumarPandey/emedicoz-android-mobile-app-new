package com.emedicoz.app.courses.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.courses.adapter.SeeAllClassesAdapter
import com.emedicoz.app.databinding.AnalysisScreenBinding
import com.emedicoz.app.databinding.FragmentSeeAllClassesBinding
import com.emedicoz.app.modelo.UpcomingCourseData

class SeeAllClassesFragment : Fragment() {
    private lateinit var binding: FragmentSeeAllClassesBinding
    private lateinit var upcomingCourseDataList: ArrayList<UpcomingCourseData>
    private lateinit var seeAllClassesAdapter: SeeAllClassesAdapter
    private var classType:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            upcomingCourseDataList =
                requireArguments().getSerializable("UPCOMING_ONGOING_CLASSES") as ArrayList<UpcomingCourseData>
            classType = requireArguments().getString("CLASS_TYPE")!!
        }
        Log.e("TAG", "onCreate: ${upcomingCourseDataList.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeeAllClassesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seeAllClassesRV.layoutManager = LinearLayoutManager(requireActivity())
        seeAllClassesAdapter = SeeAllClassesAdapter(requireActivity(), upcomingCourseDataList,classType)
        binding.seeAllClassesRV.adapter = seeAllClassesAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(upcomingCourseDataList: ArrayList<UpcomingCourseData>,classType:String): SeeAllClassesFragment {
            val fragment = SeeAllClassesFragment()
            val args = Bundle()
            args.putString("CLASS_TYPE",classType)
            args.putSerializable("UPCOMING_ONGOING_CLASSES", upcomingCourseDataList)
            fragment.arguments = args
            return fragment
        }
    }
}