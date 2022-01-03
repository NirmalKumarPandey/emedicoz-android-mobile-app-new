package com.emedicoz.app.reward.activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.databinding.CoinbankActivityBinding
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.reward.adapter.TransactionAdapter
import com.emedicoz.app.reward.model.transmodel.Data
import com.emedicoz.app.reward.repository.TransactionRepository
import com.emedicoz.app.reward.viewmodel.TransactionModelFactory
import com.emedicoz.app.reward.viewmodel.TransactionViewModel
import com.emedicoz.app.utilso.GenericUtils
import kotlinx.android.synthetic.main.coin_header.*

class CoinInBankActivity : AppCompatActivity() {
    private lateinit var binding: CoinbankActivityBinding
    lateinit var mainViewModel: TransactionViewModel
    lateinit var transactionAdapter: TransactionAdapter
    lateinit var transactionList: ArrayList<Data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = CoinbankActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

        binding.transactionRecycler.layoutManager = LinearLayoutManager(this)
        binding.transactionRecycler.isNestedScrollingEnabled = false
        transactionList = ArrayList()
        val rewordsService = ApiClient.createService(ApiInterface::class.java)
        val repository = TransactionRepository(rewordsService)
        mainViewModel = ViewModelProvider(this, TransactionModelFactory(repository)).get(TransactionViewModel::class.java)
        mainViewModel.transactionPoint.observe(this, Observer {

            if (it.status.equals(true)){
                transactionList.addAll(it.data)
                transactionAdapter = TransactionAdapter(this, transactionList)
                binding.transactionRecycler.adapter = transactionAdapter
            }else{

            }
        })
    }

    private fun initViews(){
        txtCoin.text = "My Earned Points"
        tvRewardsPoints.visibility = View.GONE
        imgBackPressed.setOnClickListener(View.OnClickListener { finish() })
    }
}