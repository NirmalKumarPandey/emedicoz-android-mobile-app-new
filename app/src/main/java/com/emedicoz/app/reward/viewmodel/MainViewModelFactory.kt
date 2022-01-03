package com.emedicoz.app.reward.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emedicoz.app.reward.repository.RewardsRepository
import com.emedicoz.app.reward.repository.TransactionRepository

class MainViewModelFactory(private val repository: RewardsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}

//transaction factory
class TransactionModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransactionViewModel(repository) as T
    }
}
