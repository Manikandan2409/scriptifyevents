package com.heremanikandan.scriptifyevents.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.AttendanceDao
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.dao.SharedWithDao
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.SharedWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class OverviewViewModel(
    private val eventId: Long,
    private val eventDao: EventDao,
    private val participantDao: ParticipantDao,
    private  val attendanceDao: AttendanceDao,
    private val sharedWithDao: SharedWithDao,

) : ViewModel() {

    private val _eventDetails = MutableStateFlow<Event?>(null)
    var eventDetails: StateFlow<Event?> = _eventDetails
    private val _participantCount = MutableStateFlow<Long>(0)
    var totalParticipants :StateFlow<Long> = _participantCount
    private  val _attendeesCount = MutableStateFlow<Long>(0)
    var  totalAttendes :StateFlow<Long> = _attendeesCount

    private  val _departmentCount = MutableStateFlow<Long>(0)
    var totalDepartment :StateFlow<Long> = _departmentCount

    private val _sharedWithList = MutableStateFlow<List<SharedWith>>(emptyList())
//    val sharedWithList: Long = _sharedWithList

    init {
        getEventDetails()
        //getSharedWithDetails()
        getAllCounts()
    }

    private fun getEventDetails() {
        viewModelScope.launch {
           eventDao.getEventByIdFlow(eventId).collect{ _eventDetails.value = it }

        }
    }

    fun getAllCounts(){
        viewModelScope.launch {
          _participantCount.value = participantDao.getParticipantsCountByEventId(eventId)
            totalParticipants = _participantCount
            _departmentCount.value =
                participantDao.getDepartmentWiseParticipantCount(eventId).count().toLong()

            _attendeesCount.value = attendanceDao.getAttendanceCountByEventId(eventId)
        }
    }

}
