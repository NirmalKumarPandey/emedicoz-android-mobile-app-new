package com.emedicoz.app.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.databinding.ActivityNotificationBinding
import com.emedicoz.app.notifications.Repository.NotificationRepository
import com.emedicoz.app.notifications.`interface`.CategoryItemClickListener
import com.emedicoz.app.notifications.adapter.NotificationCategoryAdapter
import com.emedicoz.app.notifications.fragment.AllNotificationFragment
import com.emedicoz.app.notifications.model.Data
import com.emedicoz.app.notifications.viewmodel.NotificationFactory
import com.emedicoz.app.notifications.viewmodel.NotificationViewModel
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.GenericUtils
import kotlinx.android.synthetic.main.header_layout.*

class NotificationActivity : AppCompatActivity(), CategoryItemClickListener{

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationCategoryAdapter: NotificationCategoryAdapter
    lateinit var categoryList: ArrayList<Data>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "Notifications"
//        binding.recyclerNotiCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        val categoryService = ApiClient.createService(ApiInterface::class.java)
//        val repository = NotificationRepository(categoryService)
//        categoryList = ArrayList()
//        notificationViewModel = ViewModelProvider(this, NotificationFactory(repository)).get(NotificationViewModel::class.java)
//        notificationViewModel.categoryData.observe(this, Observer {
//            Log.d("rewards", it.data.toString())
//            if (it.status.equals(true)){
//                categoryList.addAll(it.data)
//                notificationCategoryAdapter = NotificationCategoryAdapter(this, categoryList, this)
//                binding.recyclerNotiCategory.adapter = notificationCategoryAdapter
//
//            }else{
//
//            }
//        })

    }

    private fun initView(){
//        val fragment = AllNotificationFragment()
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.frame_layout, fragment)
//        transaction.commit()
        val fragment = NotificationFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }

    override fun onItemClickListener(data: Data) {
        //Toast.makeText(this, data.id, Toast.LENGTH_SHORT).show()
        val fragment = AllNotificationFragment()
        val bundle = Bundle()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        bundle.putString("category_type", data.name)
        fragment.arguments = bundle
        transaction.commit()

    }

}