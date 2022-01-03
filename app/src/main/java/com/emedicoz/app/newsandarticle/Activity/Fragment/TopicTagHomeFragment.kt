package com.emedicoz.app.newsandarticle.Activity.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.databinding.FragmentTopicTagHomeBinding
import com.emedicoz.app.network.ApiInterfacesNew
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.Activity.TopicAndTagActivity
import com.emedicoz.app.newsandarticle.Activity.TopicAndTagDetailListActivity
import com.emedicoz.app.newsandarticle.Adapter.TopicTagListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.Interface.SearchList
import com.emedicoz.app.newsandarticle.models.TopicListResponse
import com.emedicoz.app.newsandarticle.viewmodel.TopicTagHomeViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.ui.views.RecyclerItemClickListener
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_topic_and_tag.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TopicTagHomeFragment : Fragment(), ProgressUpdateListner, SearchList {

    val ARGUMENT: String = "ARGUMENT"
    var mTopicTagHomeViewModel: TopicTagHomeViewModel? = null
    var binding: FragmentTopicTagHomeBinding? = null
    private var mProgressBar: ProgressBar? = null
    var type: String? = null



    companion object {
        fun getInstance(str: String) = TopicTagHomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT, str)

            }

        }

        fun searchMethod(searchedKeyword: String, s: String, topicAndTagActivity: TopicAndTagActivity) {
            
        }
    }

    /* fun newInstance(mainCatID: StreamResponse?, mainCatName: String?, subCatList: ArrayList<SubStreamResponse?>?): ChooseSubCourseIBT? {
         val args = Bundle()
         args.putSerializable(Const.EXAM_CATEGORY, mainCatID)
         args.putString(Const.EXAM_CATEGORY_NAME, mainCatName)
         args.putSerializable(Const.SUB_CAT, subCatList)
         val fragment = ChooseSubCourseIBT()
         fragment.setArguments(args)
         return fragment
     }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTopicTagHomeViewModel = ViewModelProvider(this).get(TopicTagHomeViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicTagHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.getRoot()
       // type = checkNotNull(requireArguments().getString(ARGUMENT))
        mProgressBar = binding!!.progressBar
        val linearLayoutManager = LinearLayoutManager(context)
        binding!!.RecyclerView.setLayoutManager(linearLayoutManager)
        binding!!.RecyclerView.setHasFixedSize(true)
     //   mTopicTagHomeViewModel!!.topTagList.observe(requireActivity(), androidx.lifecycle.Observer { s -> initialize_list1(s as TopicListResponse) })
        binding!!.progressBar.visibility = View.VISIBLE
     //   mTopicTagHomeViewModel!!.TopicTagList(context, this, SharedPreference.getInstance().getLoggedInUser().getId(), "subject")

        call_topic_tag_List();
        //call_search_topic_tag_List("subject","abc")

        binding!!.apply {
           seeMoreTv.setOnClickListener {
             //  call_search_topic_tag_List("subject","abc")
            }
        }

        return root
    }

    public fun call_topic_tag_List() {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)
        val mCall = mApiInterface?.getTopicTagList(SharedPreference.getInstance().getLoggedInUser().getId(), "subject")

        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("topic and Home", "Response News : " + response.body())

                var topicListResponse: TopicListResponse = Gson().fromJson(response.body(), TopicListResponse::class.java)

                // mTopicTagHomeViewModel.topTagList.value = topicListResponse
                //// mProgressListner.update(true)
               initialize_list1(topicListResponse)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                // mProgressListner.update(false)
            }
        })
    }

    private fun initialize_list1(mList: TopicListResponse ) {


        val mAdapter = context?.let { TopicTagListRecycleViewAdapter(it, mList.data, "subject") }

        val linearLayoutManager = LinearLayoutManager(context)
        binding!!.RecyclerView.setLayoutManager(linearLayoutManager)
        binding!!.RecyclerView.setHasFixedSize(true)
        binding!!.RecyclerView.setItemAnimator(null)
        binding!!.RecyclerView.setAdapter(mAdapter)
        binding!!.RecyclerView.addOnItemTouchListener(RecyclerItemClickListener(
                context
        ) { view, temPosition ->
            val intent = Intent(context, TopicAndTagDetailListActivity::class.java)
            intent.putExtra("type", "subject")
            intent.putExtra("id", mList.data.get(temPosition).id)
            startActivity(intent)

        })
    }


    public fun call_search_topic_tag_List( // userID: String,
        type: String, searchText: String
             ) {
        val mApiInterface = ApiClient.createService(ApiInterfacesNew::class.java)
        val mCall = mApiInterface?.getSearchTopicTagList("1","subject","abc")
        mCall?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.d("Topic and tag", "Response News : " + response.body())
                var topicListResponse: TopicListResponse = Gson().fromJson(response.body(), TopicListResponse::class.java)

                /*  mTopicTagHomeViewModel.topTagList.value = topicListResponse
                  mProgressListner.update(true)*/
                 initialize_list1(topicListResponse)
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

  public fun searchMethod(search: String, type: String,context1: Context) {

        //onResume()
        // call_search_topic_tag_List(search,type);
        Toast.makeText(requireContext(), "hello", Toast.LENGTH_SHORT).show()

     //   call_search_topic_tag_List("","")

     /*   context.let {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.let {
                val currentFragment = fragmentManager.findFragmentById(R.id.constraintLayout)
                currentFragment.let {

                    val fragmentTransformation = fragmentManager.beginTransaction()
                    if (it != null) {
                        fragmentTransformation.detach(it)
                        fragmentTransformation.attach(it)
                        fragmentTransformation.commit()
                        //call_search_topic(context)
                    }

                }
            }

        }*/
    }

    public fun call_search_topic() {

        Toast.makeText(context, "hello2", Toast.LENGTH_SHORT).show()

    }

    override fun searchMethod(search: String, type: String) {
        Toast.makeText(context, "hello2", Toast.LENGTH_SHORT).show()

    }

}

