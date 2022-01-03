package com.emedicoz.app.notifications.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.notifications.adapter.NotificationCatListAdapter
import com.emedicoz.app.notifications.adapter.NotificationCategoryAdapter
import com.emedicoz.app.notifications.model.CategoryNotification
import com.emedicoz.app.notifications.model.Data
import com.emedicoz.app.notifications.model.DataX
import com.emedicoz.app.retrofit.ApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllNotificationFragment : Fragment() {

    lateinit var recyclerCategoryItem: RecyclerView
    private lateinit var notificationItemListAdapter: NotificationCatListAdapter
    lateinit var categoryItemList: ArrayList<DataX>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        if (args != null) {
            val category_type = args.getString("category_type")
            System.out.println("category_type--------------------------"+category_type)

        }
        recyclerCategoryItem = view.findViewById(R.id.recyclerCategoryItem)
        categoryItemList = ArrayList()
        recyclerCategoryItem.layoutManager = LinearLayoutManager(context)
        val categoryListService = ApiClient.createService(ApiInterface::class.java)
        val itemResponse = categoryListService.getUserNotificationByCategory("ALL", "GENERAL")
        itemResponse.enqueue(object : Callback<CategoryNotification>{
            override fun onResponse(
                call: Call<CategoryNotification>,
                response: Response<CategoryNotification>
            ) {
                val categoryItem = response.body()
                if (categoryItem!!.status.equals(true)){
                    categoryItemList.addAll(categoryItem.data)
                    notificationItemListAdapter = NotificationCatListAdapter(context!!, categoryItemList)
                    recyclerCategoryItem.adapter = notificationItemListAdapter
                }
            }

            override fun onFailure(call: Call<CategoryNotification>, t: Throwable) {

            }
        })
    }
}