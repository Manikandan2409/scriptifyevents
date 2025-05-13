package com.heremanikandan.scriptifyevents.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.AttendanceDao
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.dao.SharedWithDao
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.EventStatus
import com.heremanikandan.scriptifyevents.db.model.SharedWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel(
    private val eventId: Long,
    private val eventDao: EventDao,
    private val participantDao: ParticipantDao,
    private val attendanceDao: AttendanceDao,
    private val sharedWithDao: SharedWithDao,
) : ViewModel() {

    private val _eventDetails = MutableStateFlow<Event?>(null)
    val eventDetails: StateFlow<Event?> = _eventDetails

    private val _participantCount = MutableStateFlow(0L)
    var totalParticipants: StateFlow<Long> = _participantCount

    private val _attendeesCount = MutableStateFlow(0L)
    var totalAttendees: StateFlow<Long> = _attendeesCount

    private val _departmentCount = MutableStateFlow(0L)
    var totalDepartment: StateFlow<Long> = _departmentCount

    private val _sharedWithList = MutableStateFlow<List<SharedWith>>(emptyList())

    init {
        getEventDetails()
        getAllCounts()
    }

    private fun getEventDetails() {
        viewModelScope.launch {
            eventDao.getEventByIdFlow(eventId).collect {
                _eventDetails.value = it
            }
        }
    }

    fun setEventStatus(updatedStatus:EventStatus){
        viewModelScope.launch {
           val updatedEvent= eventDetails.value?.copy(
                status = updatedStatus
            )
            eventDao.updateEvent(updatedEvent!!)
        }
    }

    private fun getAllCounts() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // 1. Participants
//                val participantCount = participantDao.getParticipantsCountByEventId(eventId)
//                _participantCount.value = participantCount
//                totalParticipants = _participantCount
//                Log.d("DETAILS INFO", "Participants: $participantCount")
//
//                // 2. Departments
//                val deptCount = participantDao.getDepartmentWiseParticipantCount(eventId).count().toLong()
//                _departmentCount.value = deptCount
//                totalDepartment = _departmentCount
//                Log.d("DETAILS INFO", "Departments: $deptCount")
//
//                // 3. Attendees
//                val attendanceCount = attendanceDao.getAttendanceCountByEventId(eventId)
//                _attendeesCount.value = attendanceCount
//                totalAttendees = _attendeesCount
//                Log.d("DETAILS INFO", "Attendees: $attendanceCount")
//
//            } catch (e: Exception) {
//                Log.e("DETAILS INFO", "Error loading counts", e)
//            }
//        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Launching each task concurrently using async
                val participantDeferred = async { participantDao.getParticipantsCountByEventId(eventId) }
                val deptDeferred = async { participantDao.getDepartmentWiseParticipantCount(eventId).count().toLong() }
                val attendanceDeferred = async { attendanceDao.getAttendanceCountByEventId(eventId) }

                // Awaiting the results of each deferred task
                val participantCount = participantDeferred.await()
                val deptCount = deptDeferred.await()
                val attendanceCount = attendanceDeferred.await()

                // Updating LiveData on the main thread
                withContext(Dispatchers.Main) {
                    _participantCount.value = participantCount
                    totalParticipants = _participantCount

                    _departmentCount.value = deptCount
                    totalDepartment = _departmentCount

                    _attendeesCount.value = attendanceCount
                    totalAttendees = _attendeesCount

                    // Log output for debugging
                    Log.d("DETAILS INFO", "Participants: $participantCount")
                    Log.d("DETAILS INFO", "Departments: $deptCount")
                    Log.d("DETAILS INFO", "Attendees: $attendanceCount")
                }

            } catch (e: Exception) {
                // Handle any exceptions that occur during the database calls
                Log.e("DETAILS INFO", "Error loading counts", e)
            }
        }

    }
}

