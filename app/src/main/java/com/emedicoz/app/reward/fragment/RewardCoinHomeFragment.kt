package com.emedicoz.app.reward.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.MainNavGraphDirections
import com.emedicoz.app.R
import com.emedicoz.app.coins.*
import com.emedicoz.app.reward.activity.CoinInBankActivity
import com.emedicoz.app.reward.activity.MyCoinVActivity
import kotlinx.android.synthetic.main.activity_coin.*
import kotlinx.android.synthetic.main.my_coin_multiple_selection.*

class RewardCoinHomeFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RedeemAdapter
    lateinit var lytViewAll: RelativeLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reward_coin_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lytViewAll = view.findViewById(R.id.lytViewAll)
        recyclerView = view.findViewById(R.id.recycler_view_redeem_offers)
        recyclerView.adapter = context?.let { RedeemAdapter(it) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        lytViewAll.setOnClickListener{
            val intent = Intent(context, MyCoinVActivity::class.java)
            startActivity(intent)
        }

        relativeMyEarnedPoint.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CoinInBankActivity::class.java)
            startActivity(intent)
        })

        relativeRedeem.setOnClickListener(View.OnClickListener {
            //Toast.makeText(context, "Redeem", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_rewardCoinHomeFragment_to_redeemCouponFragment)
        })

        relativePlayGame.setOnClickListener(View.OnClickListener {
        })

        relativeEarnPoint.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, EarnCoinActivity::class.java)
            startActivity(intent)
            //findNavController().navigate(R.id.action_rewardCoinHomeFragment_to_earnPointFragment)
        })
    }
}