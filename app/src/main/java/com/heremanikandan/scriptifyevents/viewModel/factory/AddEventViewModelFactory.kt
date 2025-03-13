package com.heremanikandan.scriptifyevents.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.viewModel.AddEventViewModel

class AddEventViewModelFactory(private val eventDao: EventDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEventViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
