package com.emedicoz.app.reward.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.reward.model.RewardsPoint
import com.emedicoz.app.reward.model.transmodel.TransactionPoint

class RewardsRepository(private val apiInterface: ApiInterface) {

    private val rewardsLiveData = MutableLiveData<RewardsPoint>()

    val rewards: LiveData<RewardsPoint>
    get() = rewardsLiveData
    suspend fun getRewards(user_id: String){
        val rewardsResult = apiInterface.getRewardPoint(user_id)
        if (rewardsResult?.body()!= null){
            rewardsLiveData.postValue(rewardsResult.body())

        }
    }
}

//transaction repository point
class TransactionRepository(private val apiInterface: ApiInterface){
    private val transactionsLiveData = MutableLiveData<TransactionPoint>()

    val transactions: LiveData<TransactionPoint>
        get() = transactionsLiveData
    suspend fun getTransaction(user_id: String){
        val transactionResult = apiInterface.getCoinTransaction(user_id)
        if (transactionResult?.body()!= null){
            transactionsLiveData.postValue(transactionResult.body())

        }
    }
}
