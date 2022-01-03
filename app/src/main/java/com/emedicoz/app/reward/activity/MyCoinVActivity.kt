package com.emedicoz.app.reward.activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.coins.OfferAdapter
import com.emedicoz.app.coins.RedeemOfferAdapter
import com.emedicoz.app.coins.RedeemSaveActivity
import com.emedicoz.app.coins.SaveUsedadapter
import com.emedicoz.app.databinding.ActivityCoinBinding
import com.emedicoz.app.databinding.ActivityVcoinBinding
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.reward.repository.RewardsRepository
import com.emedicoz.app.reward.viewmodel.MainViewModel
import com.emedicoz.app.reward.viewmodel.MainViewModelFactory
import com.emedicoz.app.utilso.GenericUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.coin_header.*
import kotlinx.android.synthetic.main.recorded_details_description_fragment.*

class MyCoinVActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVcoinBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityVcoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    fun initViews(){
    }
}