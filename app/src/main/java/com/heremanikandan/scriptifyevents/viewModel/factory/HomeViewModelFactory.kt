package com.heremanikandan.scriptifyevents.viewModel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.viewModel.HomeViewModel

class HomeViewModelFactory(private val eventDao: EventDao,private val context:Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(eventDao,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
