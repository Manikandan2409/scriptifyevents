package com.heremanikandan.scriptifyevents.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.SharedWithDao
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.SharedWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverviewViewModel(
    private val eventId: String,
    private val eventDao: EventDao,
    private val sharedWithDao: SharedWithDao,
) : ViewModel() {

    private val _eventDetails = MutableStateFlow<Event?>(null)
    val eventDetails: StateFlow<Event?> = _eventDetails

    private val _sharedWithList = MutableStateFlow<List<SharedWith>>(emptyList())
//    val sharedWithList: Long = _sharedWithList

    init {
        getEventDetails()
        //getSharedWithDetails()
    }

    private fun getEventDetails() {
        viewModelScope.launch {
            _eventDetails.value = eventDao.getEventById(Integer.parseInt(eventId).toLong())
        }
    }

//    private fun getSharedWithDetails() {
//        viewModelScope.launch {
//            _sharedWithList.value = sharedWithDao.getPermittedUsersCount(eventId)
//        }
//    }
}
