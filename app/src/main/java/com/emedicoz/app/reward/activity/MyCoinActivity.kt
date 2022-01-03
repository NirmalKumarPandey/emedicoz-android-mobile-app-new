package com.emedicoz.app.reward.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.databinding.ActivityCoinBinding
import com.emedicoz.app.databinding.ActivityVcoinBinding
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.reward.repository.RewardsRepository
import com.emedicoz.app.reward.viewmodel.MainViewModel
import com.emedicoz.app.reward.viewmodel.MainViewModelFactory
import com.emedicoz.app.utilso.GenericUtils
import com.uxcam.internals.it
import kotlinx.android.synthetic.main.coin_header.*

class MyCoinActivity : AppCompatActivity() {

    lateinit var binding: ActivityCoinBinding
    lateinit var mainViewModel: MainViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    var navHostController: NavHostFragment? = null
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        val rewordsService = ApiClient.createService(ApiInterface::class.java)
        val repository = RewardsRepository(rewordsService)
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository)).get(MainViewModel::class.java)
        mainViewModel.rewordsPoint.observe(this, Observer {
            Log.d("rewards", it.data.reward_points)
            if (it.status.equals(true)){
                tvRewardsPoints.text = it.data.reward_points
            }else{

            }
        })
    }

    fun initViews(){
        navController = NavController(this)
        navHostController =
                supportFragmentManager.findFragmentById(R.id.nav_host_reward_frag) as NavHostFragment

        navController = navHostController!!.navController
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.rewardCoinHomeFragment
                )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        imgBackPressed.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}