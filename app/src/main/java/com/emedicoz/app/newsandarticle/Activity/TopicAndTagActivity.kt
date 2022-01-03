package com.emedicoz.app.newsandarticle.Activity

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityTopicAndTagBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.Adapter.TopicTagListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.TopicListResponse
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TopicAndTagActivity : AppCompatActivity(), View.OnClickListener, ProgressUpdateListner {
    private var mProgressBar: ProgressBar? = null
    var type: String? = null
    var binding: ActivityTopicAndTagBinding? = null
    var NIGHT_MODE = -1
    lateinit var tabs: TabLayout
    var searchedKeyword: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityTopicAndTagBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        binding!!.apply {

            backTopic.setOnClickListener{

                finish()

            }


        }

        if (!TextUtils.isEmpty(searchedKeyword)) {
            binding!!.imgClearSearch.visibility = View.VISIBLE
            binding!!.imgSearchView.visibility = View.GONE
            binding!!.recordedSearchFilter.setText(searchedKeyword)
            binding!!.recordedSearchFilter.setSelection(Helper.GetText(binding!!.recordedSearchFilter).length)
        } else {
            binding!!.imgClearSearch.visibility = View.GONE
            binding!!.imgSearchView.visibility = View.VISIBLE
            binding!!.recordedSearchFilter.setText("")
        }

        binding!!.imgClearSearch.setOnClickListener {
            searchForKeywordInAPI(true)
        }

        binding!!.recordedSearchFilter.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchForKeywordInAPI(false)
                return@setOnEditorActionListener true
            }
            false
        }


        binding!!.recordedSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    binding!!.imgClearSearch.visibility = View.VISIBLE
                    binding!!.imgSearchView.visibility = View.GONE
                } else {
                    binding!!.imgClearSearch.visibility = View.GONE
                    binding!!.imgSearchView.visibility = View.VISIBLE
//                    searchForKeywordInAPI(true)
                }
            }
        })

        /* val topicAndTagPagerAdapter = TopicAndTagPagerAdapter(this, supportFragmentManager)
         val viewPager: ViewPager = binding!!.viewPager
         viewPager.adapter = topicAndTagPagerAdapter
         tabs = binding!!.tabs
         tabs.isInlineLabel = false
         tabs.setupWithViewPager(viewPager)
         val viewTab1: View = LayoutInflater.from(this).inflate(R.layout.news_tab_a, null)
         tabs.getTabAt(0)!!.customView = viewTab1
         val viewTab2: View = LayoutInflater.from(this).inflate(R.layout.news_tab_b, null)
         tabs.getTabAt(1)!!.customView = viewTab2
         val viewTab3: View = LayoutInflater.from(this).inflate(R.layout.news_tab_b, null)
         tabs.getTabAt(2)!!.customView = viewTab3
         val viewTab4: View = LayoutInflater.from(this).inflate(R.layout.news_tab_b, null)
         tabs.getTabAt(3)!!.customView = viewTab4
         val viewTab5: View = LayoutInflater.from(this).inflate(R.layout.news_tab_b, null)
         tabs.getTabAt(4)!!.customView = viewTab5
 */
        // setTabbedListner(tabs, viewTab1, viewTab2, viewTab3)
        var appLinkData: Uri? = null
        mProgressBar = binding!!.progressBar
        val linearLayoutManager = LinearLayoutManager(this)
        binding!!.RecyclerView.setLayoutManager(linearLayoutManager)
        binding!!.RecyclerView.setHasFixedSize(true)


        binding!!.progressBar.visibility = View.VISIBLE
        // SharedPreference.getInstance().putString("type", "subject")
        SharedPreference.getInstance().putString("type", "category")
        call_topic_tag_List()

        binding!!.apply {
            subjectTab.setOnClickListener(this@TopicAndTagActivity)
            topicTab.setOnClickListener(this@TopicAndTagActivity)
            //   facultyTab.setOnClickListener(this@TopicAndTagActivity)
            tagTab.setOnClickListener(this@TopicAndTagActivity)
            fallowtagTab.setOnClickListener(this@TopicAndTagActivity)

        }
    }

    public fun call_topic_tag_List() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)
        val mCall = mApiInterface?.getTopicTagList(SharedPreference.getInstance().getLoggedInUser().getId(), SharedPreference.getInstance().getString("type"))

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("topic and Home", "Response News : " + response.body())

                var topicListResponse: TopicListResponse = Gson().fromJson(response.body(), TopicListResponse::class.java)

                // mTopicTagHomeViewModel.topTagList.value = topicListResponse
                //// mProgressListner.update(true)
                if (topicListResponse.status.equals(true)) {
                    if (topicListResponse.data.size > 0) {
                        initialize_list1(topicListResponse)
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.layoutNoContentFound.visibility = View.GONE
                    } else {
                        binding!!.layoutNoContentFound.visibility = View.VISIBLE
                    }
                } else {
                    binding!!.layoutNoContentFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }
        })
    }

    private fun initialize_list1(mList: TopicListResponse) {


        val mAdapter = this?.let { TopicTagListRecycleViewAdapter(it, mList.data, SharedPreference.getInstance().getString("type")) }

        binding!!.RecyclerView.setItemAnimator(null)
        binding!!.RecyclerView.setAdapter(mAdapter)
    }


    public fun call_search_topic_tag_List( // userID: String,
            type: String, searchText: String
    ) {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)
        val mCall = mApiInterface?.getSearchTopicTagList(SharedPreference.getInstance().getLoggedInUser().getId(),searchText,SharedPreference.getInstance().getString("type"))
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Topic and tag", "Response News : " + response.body())
                var topicListResponse: TopicListResponse = Gson().fromJson(response.body(), TopicListResponse::class.java)
                /*initialize_list1(topicListResponse)
                binding!!.progressBar.visibility = View.GONE
                */
                if (topicListResponse.status.equals(true)) {
                    if (topicListResponse.data.size > 0) {
                        initialize_list1(topicListResponse)
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.layoutNoContentFound.visibility = View.GONE
                    } else {
                        binding!!.layoutNoContentFound.visibility = View.VISIBLE
                    }
                } else {
                    binding!!.layoutNoContentFound.visibility = View.VISIBLE
                }


            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                //  mProgressListner.update(false)
            }


        })

    }


    override fun update(b: Boolean) {
        Log.d("UPDATE ", " REcieved " + b)
        // System.out.print("Update Recieved : "+ b)
        binding!!.progressBar.visibility = View.GONE
    }


    override fun onClick(v: View?) {

        binding!!.view1.visibility = View.GONE
        binding!!.view2.visibility = View.GONE
        binding!!.view3.visibility = View.GONE
        binding!!.view4.visibility = View.GONE
        binding!!.view5.visibility = View.GONE
        // unSelectTabs()
        unSelectTabs()
        when (v!!.id) {
            R.id.subjectTab -> {
                /*binding!!.subjectTab.setImageDrawable(resources.getDrawable(R.mipmap.category_largenew))*/
                binding!!.subjectTab.setImageDrawable(resources.getDrawable(R.mipmap.category_l))
                binding!!.viewPager.setCurrentItem(0, true)
                binding!!.view1.visibility = View.VISIBLE

                /*  val fragment = TopicTagHomeFragment.getInstance("subject")
                  replaceFragment(fragment, true)*/
                // type="subject"
                //  SharedPreference.getInstance().putString("type", "subject")
                SharedPreference.getInstance().putString("type", "category")
                SharedPreference.getInstance().putString("searchType", "")
                call_topic_tag_List()

            }
            R.id.topicTab -> {
                binding!!.topicTab.setImageDrawable(resources.getDrawable(R.mipmap.topics_large))
                binding!!.viewPager.setCurrentItem(1, true)
                binding!!.view2.visibility = View.VISIBLE
                // type="topics"
                SharedPreference.getInstance().putString("type", "topics")
                SharedPreference.getInstance().putString("searchType", "")
                call_topic_tag_List()
            }
            /* R.id.facultyTab -> {
                 binding!!.facultyTab.setImageDrawable(resources.getDrawable(R.mipmap.faculty_large))
                 binding!!.viewPager.setCurrentItem(2, true)
                 binding!!.view3.visibility = View.VISIBLE


             }*/
            R.id.tagTab -> {
             //   binding!!.tagTab.setImageDrawable(resources.getDrawable(R.mipmap.tag_large))
                binding!!.tagTab.setImageDrawable(resources.getDrawable(R.mipmap.alltag_l))
                binding!!.viewPager.setCurrentItem(3, true)
                binding!!.view4.visibility = View.VISIBLE
                //  type="all_tag"
                SharedPreference.getInstance().putString("type", "all_tag")
                SharedPreference.getInstance().putString("searchType", "")
                call_topic_tag_List()
                // subject","topics", "all_tag", "followed_tag
            }

            R.id.fallowtagTab -> {
             //   binding!!.fallowtagTab.setImageDrawable(resources.getDrawable(R.mipmap.fallowtag_large))
                binding!!.fallowtagTab.setImageDrawable(resources.getDrawable(R.mipmap.followtaged_l))
                binding!!.viewPager.setCurrentItem(4, true)
                binding!!.view5.visibility = View.VISIBLE
                //  type="followed_tag"
                SharedPreference.getInstance().putString("type", "followed_tag")
                SharedPreference.getInstance().putString("searchType", "")
                call_topic_tag_List()
            }
        }
    }


    fun unSelectTabs() {

      //  binding!!.subjectTab.setImageDrawable(resources.getDrawable(R.mipmap.category_large))
        binding!!.subjectTab.setImageDrawable(resources.getDrawable(R.mipmap.category_s))
        //  binding!!.historytab.setImageDrawable(resources.getDrawable(R.mipmap.history_small_new))
        binding!!.topicTab.setImageDrawable(resources.getDrawable(R.mipmap.topic_small))
        // binding!!.facultyTab.setImageDrawable(resources.getDrawable(R.mipmap.faculty_small))
       // binding!!.tagTab.setImageDrawable(resources.getDrawable(R.mipmap.tag_small))
        binding!!.tagTab.setImageDrawable(resources.getDrawable(R.mipmap.alltag_s))
        //binding!!.fallowtagTab.setImageDrawable(resources.getDrawable(R.mipmap.fallowtage_small))
        binding!!.fallowtagTab.setImageDrawable(resources.getDrawable(R.mipmap.followedtag_s))

    }

    fun setTabbedListner(tabs: TabLayout, viewTab1: View, viewTab2: View, viewTab3: View) {
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tabs.selectedTabPosition
                unSelectTabs()
                when (position) {
                    0 -> {
                        binding!!.subjectTab.setImageDrawable(resources.getDrawable(R.mipmap.subject_large))

                    }
                    1 -> {
                        binding!!.topicTab.setImageDrawable(resources.getDrawable(R.mipmap.topics_large))


                    }
                    /*  2 -> {

                          binding!!.facultyTab.setImageDrawable(resources.getDrawable(R.mipmap.faculty_large))

                      }*/
                    2 -> {
                        binding!!.tagTab.setImageDrawable(resources.getDrawable(R.mipmap.tag_large))

                    }
                    3 -> {
                        binding!!.fallowtagTab.setImageDrawable(resources.getDrawable(R.mipmap.fallowtag_large))


                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun replaceFragment(fragment: Fragment, isAddBackStack: Boolean) {
        val transaction: FragmentTransaction = this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment).commit()
        //if (isAddBackStack) transaction.addToBackStack(fragmen.c))
    }

    private fun searchForKeywordInAPI(loadAllData: Boolean) {
        if (loadAllData) {
            // filterType = ""
            searchedKeyword = ""
            // eMedicozApp.getInstance().filterType = filterType
            eMedicozApp.getInstance().searchedKeyword = searchedKeyword
            binding!!.recordedSearchFilter.setText(searchedKeyword)
            SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, searchedKeyword)

            // networkCallForLiveCourse()

            /*  val searchlist = TopicTagHomeFragment()
              searchlist.searchMethod(searchedKeyword, "subject", this)*/
            //  call_search_topic_tag_List("subject", "abc")
            SharedPreference.getInstance().putString("searchType", "")
            call_topic_tag_List()

            return
        }

        searchedKeyword = binding!!.recordedSearchFilter.text.trim().toString()
        eMedicozApp.getInstance().searchedKeyword = searchedKeyword

        if (searchedKeyword.isNotEmpty()) {
            //   networkCallForLiveCourse()

            //   val searchlist = TopicTagHomeFragment()
            //  searchlist.searchMethod(searchedKeyword, "subject", this)
            //   searchlist.searchMethod("", "")

            //  val fr = TopicTagHomeFragment().searchMethod(searchedKeyword, "subject", this)
            //  if( SharedPreference.getInstance().getString("type").equals("subject"))
            if( SharedPreference.getInstance().getString("type").equals("category"))
            {
                SharedPreference.getInstance().putString("searchType", "searchSubject")
                call_search_topic_tag_List("", searchedKeyword)
            }else
            {
                SharedPreference.getInstance().putString("searchType", "")
                call_search_topic_tag_List("", searchedKeyword)
            }


            //  onResume()
            Helper.closeKeyboard(this)
        } else
            Toast.makeText(this, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        /* val _fragA =*//*TopicTagHomeFragment
        _fragA.searchMethod(searchedKeyword, "subject", this)*/
    }

}