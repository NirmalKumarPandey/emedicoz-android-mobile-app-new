package com.emedicoz.app.recordedCourses.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emedicoz.app.databinding.FragmentRecordedDetailVideoBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.recordedCourses.model.CurriculumData
import com.emedicoz.app.recordedCourses.model.CurriculumDataObject
import com.emedicoz.app.recordedCourses.model.SubCurriculumData
import com.emedicoz.app.utilso.Const

class RecordedDetailNotesAndTest : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(fragType: String): RecordedDetailNotesAndTest {
            val fragment = RecordedDetailNotesAndTest()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            fragment.arguments = args
            return fragment
        }
    }

    // Variables Declaration
    private lateinit var rdBinding: FragmentRecordedDetailVideoBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = rdBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rdBinding = FragmentRecordedDetailVideoBinding.inflate(inflater, container, false)
        val view = binding.root
/*
        binding.curriculumExpandList.apply {
            setAdapter(RecordedCourseDetailCurriculumAdapter(context, loadCurriculum()))
        }
*/
        return view
    }

    private fun loadCurriculum(): ArrayList<CurriculumData> {
        var list: ArrayList<CurriculumData> = ArrayList()
        for (i in 1..3) {
            val curriculumData = CurriculumData("Module $i", loadCurriculumData())
            list.add(curriculumData)
        }
        return list
    }


    private fun loadCurriculumData(): ArrayList<SubCurriculumData> {
        var list: ArrayList<SubCurriculumData> = ArrayList()
        for (i in 1..3) {
            val curriculumData = SubCurriculumData("Title $i", loadCurriculumObject())
            list.add(curriculumData)
        }
        return list
    }


    private fun loadCurriculumObject(): ArrayList<CurriculumDataObject> {
        var list: ArrayList<CurriculumDataObject> = ArrayList()
        list.add(CurriculumDataObject("Introduction"))
        list.add(CurriculumDataObject("Chapter 1"))
        list.add(CurriculumDataObject("Chapter 2"))
        return list
    }

    private fun loadRecordedCourse(): ArrayList<Course> {
        var courseList: ArrayList<Course> = ArrayList()
        for (i in 1..10) {
            val course: Course = Course()
            courseList.add(course)
        }
        return courseList
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        rdBinding = null
    }


}