package com.emedicoz.app.reward.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emedicoz.app.reward.model.RewardsPoint
import com.emedicoz.app.reward.model.transmodel.TransactionPoint
import com.emedicoz.app.reward.repository.RewardsRepository
import com.emedicoz.app.reward.repository.TransactionRepository
import com.emedicoz.app.utilso.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: RewardsRepository): ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getRewards(SharedPreference.getInstance().loggedInUser.id)
        }
    }

    val rewordsPoint : LiveData<RewardsPoint>
    get() = repository.rewards
}

//transaction viewmodel class
class TransactionViewModel(private val repository: TransactionRepository): ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransaction(SharedPreference.getInstance().loggedInUser.id)
        }
    }

    val transactionPoint : LiveData<TransactionPoint>
        get() = repository.transactions
}

