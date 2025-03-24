package com.heremanikandan.scriptifyevents.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.SharedWithDao
import com.heremanikandan.scriptifyevents.viewModel.OverviewViewModel

class OverviewViewModelFactory(
    private val eventId: String,
    private val eventDao: EventDao,
    private val sharedWithDao: SharedWithDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(eventId, eventDao, sharedWithDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}