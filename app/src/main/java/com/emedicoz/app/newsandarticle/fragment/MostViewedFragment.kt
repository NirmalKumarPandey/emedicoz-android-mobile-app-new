package com.emedicoz.app.newsandarticle.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.databinding.FragmentMostViewedBinding
import com.emedicoz.app.databinding.NewsHomeFragmentBinding
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.Activity.NewAndArticalDetailActivity
import com.emedicoz.app.newsandarticle.models.LatestNewsListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.MostViewedListRecycleViewAdapter
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.newsandarticle.viewmodel.NewsAndArticleViewModel
import com.emedicoz.app.ui.views.RecyclerItemClickListener
import com.emedicoz.app.utilso.SharedPreference

class MostViewedFragment : Fragment() , ProgressUpdateListner {

    val ARGUMENT : String = "ARGUMENT"
    lateinit  var mNewsAndArticleViewModel: NewsAndArticleViewModel
    lateinit var binding: FragmentMostViewedBinding
    private var mProgressBar: ProgressBar? = null
    companion object{

        fun getInstance(str : String) = MostViewedFragment().apply {
            arguments = Bundle().apply {

                putString(ARGUMENT , "newsHome")
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNewsAndArticleViewModel = ViewModelProvider(this).get(NewsAndArticleViewModel::class.java)

    }


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View?
    {


        binding =   FragmentMostViewedBinding.inflate(inflater, container, false)
        val root: View = binding.getRoot()
        mProgressBar = binding.progressBar


        val linearLayoutManager = LinearLayoutManager(context)
        binding.RecyclerView.setLayoutManager(linearLayoutManager)
        binding.RecyclerView.setHasFixedSize(true)

        //  binding.RecyclerView.addItemDecoration(SpacesItemDecoration(1))

        mNewsAndArticleViewModel.newsList.observe(requireActivity(),
                Observer<NewsListResponse?> { s -> initialize_list(s as NewsListResponse) })

        binding.progressBar.visibility= View.VISIBLE
        //    mNewsAndArticleViewModel.loadNewsList(context,this , "1" )
        // mNewsAndArticleViewModel.loadNewsList(context,this , "1","latest" )
        mNewsAndArticleViewModel.loadNewsList(context,this , SharedPreference.getInstance().getLoggedInUser().getId(),"viewed" )
        return root
    }


    private fun initialize_list(mList: NewsListResponse) {

        // Toast.makeText(getActivity(), "Inside ")

        // Creating the Adapter class
        val mAdapter = context?.let { MostViewedListRecycleViewAdapter(it, mList.data) }
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.RecyclerView.setItemAnimator(null)
        binding.RecyclerView.setAdapter(mAdapter)
        binding.RecyclerView.addOnItemTouchListener(RecyclerItemClickListener(
                context
        ) { view, temPosition ->
           /* when (temPosition) {
                0 -> {

                    Toast.makeText(context ,"pos click 0 ", Toast.LENGTH_SHORT ).show()

                }
                1 -> {
                }
                else -> {
                }
            }*/

            val intent = Intent(context, NewAndArticalDetailActivity::class.java)
            intent.putExtra("articleId",mList.data.get(temPosition).articleId)
            startActivity(intent)
        })
    }


    override fun update(b: Boolean) {

        Log.d("UPDATE ", " REcieved "+ b)
        // System.out.print("Update Recieved : "+ b)

        binding.progressBar.visibility= View.GONE
    }

}