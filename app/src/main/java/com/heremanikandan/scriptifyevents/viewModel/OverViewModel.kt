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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//class OverviewViewModel(
//    private val eventId: Long,
//    private val eventDao: EventDao,
//    private val participantDao: ParticipantDao,
//    private val attendanceDao: AttendanceDao,
//    private val sharedWithDao: SharedWithDao,
//) : ViewModel() {
//
//    private val _eventDetails = MutableStateFlow<Event?>(null)
//    val eventDetails: StateFlow<Event?> = _eventDetails
//
//    private val _participantCount = MutableStateFlow(0L)
//    var totalParticipants: StateFlow<Long> = _participantCount
//
//    private val _attendeesCount = MutableStateFlow(0L)
//    var totalAttendees: StateFlow<Long> = _attendeesCount
//
//    private val _departmentCount = MutableStateFlow(0L)
//    var totalDepartment: StateFlow<Long> = _departmentCount
//
//    private val _sharedWithList = MutableStateFlow<List<SharedWith>>(emptyList())
//
//    init {
//        getEventDetails()
//        getAllCounts()
//    }
//
//    private fun getEventDetails() {
//        viewModelScope.launch {
//            eventDao.getEventByIdFlow(eventId).collect {
//                _eventDetails.value = it
//            }
//        }
//    }
//
//    fun setEventStatus(updatedStatus:EventStatus){
//        viewModelScope.launch {
//           val updatedEvent= eventDetails.value?.copy(
//                status = updatedStatus
//            )
//            eventDao.updateEvent(updatedEvent!!)
//        }
//    }
//
//    private fun getAllCounts() {
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Launching each task concurrently using async
//                val participantDeferred = async { participantDao.getParticipantsCountByEventId(eventId) }
//                val deptDeferred = async { participantDao.getDepartmentWiseParticipantCount(eventId).count().toLong() }
//                val attendanceDeferred = async { attendanceDao.getAttendanceCountByEventId(eventId) }
//
//                // Awaiting the results of each deferred task
//                val participantCount = participantDeferred.await()
//                val deptCount = deptDeferred.await()
//                val attendanceCount = attendanceDeferred.await()
//
//                // Updating LiveData on the main thread
//                withContext(Dispatchers.Main) {
//                    _participantCount.value = participantCount
//                    totalParticipants = _participantCount
//
//                    _departmentCount.value = deptCount
//                    totalDepartment = _departmentCount
//
//                    _attendeesCount.value = attendanceCount
//                    totalAttendees = _attendeesCount
//
//                    // Log output for debugging
//                    Log.d("DETAILS INFO", "Participants: $participantCount")
//                    Log.d("DETAILS INFO", "Departments: $deptCount")
//                    Log.d("DETAILS INFO", "Attendees: $attendanceCount")
//                }
//
//            } catch (e: Exception) {
//                // Handle any exceptions that occur during the database calls
//                Log.e("DETAILS INFO", "Error loading counts", e)
//            }
//        }
//
//    }
//}

class OverviewViewModel(
    private val eventId: Long,
    private val eventDao: EventDao,
    private val participantDao: ParticipantDao,
    private val attendanceDao: AttendanceDao,
    private val sharedWithDao: SharedWithDao,
) : ViewModel() {

    private val _eventDetails = MutableStateFlow<Event?>(null)
    val eventDetails: StateFlow<Event?> = _eventDetails

    private val _participantCount = MutableStateFlow<Long>(0)
    val totalParticipants: StateFlow<Long> get() = _participantCount

    private val _departmentCount = MutableStateFlow<Long>(0)
    val totalDepartment: StateFlow<Long> get() = _departmentCount

    private val _attendeesCount = MutableStateFlow<Long>(0)
    val totalAttendees: StateFlow<Long> get() = _attendeesCount

    init {
        fetchEventDetails()
        fetchCountsIndividually()  // Instead of getAllCounts()
    }

    private fun fetchEventDetails() {
        viewModelScope.launch {
            eventDao.getEventByIdFlow(eventId).collect {
                _eventDetails.value = it
            }
        }
    }

    fun setEventStatus(updatedStatus: EventStatus) {
        viewModelScope.launch {
            eventDetails.value?.copy(status = updatedStatus)?.let {
                eventDao.updateEvent(it)
            }
        }
    }

//    private fun fetchCountsIndividually() {
//        // Fetch participant count
//        viewModelScope.launch {
//            try {
//                val count = participantDao.getParticipantsCountByEventId(eventId).collect{ count ->
//                    _participantCount.value = count
//                    Log.d("COUNT INFO", "Participants: $count")
//                }
//                Log.d("COUNT INFO", "Participants: $count")
//            } catch (e: Exception) {
//                Log.e("COUNT INFO", "Error fetching participants", e)
//            }
//        }
//
//        // Fetch department count
//        viewModelScope.launch {
//            try {
//                participantDao.getDistinctCourseCountByEventId(eventId).collect{
//                    _departmentCount.value = it
//                    Log.d("COUNT INFO", "Departments: $it")
//
//                }
//            } catch (e: Exception) {
//                Log.e("COUNT INFO", "Error fetching departments", e)
//            }
//        }
//
//        // Fetch attendance count
//        viewModelScope.launch {
//            try {
//                attendanceDao.getAttendanceCountByEventId(eventId).collect{count->
//                    _attendeesCount.value = count
//                    Log.d("COUNT INFO", "Attendance: $count")
//
//                }
//            } catch (e: Exception) {
//                Log.e("COUNT INFO", "Error fetching attendance", e)
//            }
//        }
//    }

    private fun fetchCountsIndividually() {
        // Participant count
        viewModelScope.launch {
            participantDao.getParticipantsCountByEventId(eventId).collect { count ->
                _participantCount.value = count
                Log.d("COUNT INFO", "Participants: $count")
            }
        }

        // Department count
        viewModelScope.launch {
            participantDao.getDistinctCourseCountByEventId(eventId).collect { count ->
                _departmentCount.value = count
                Log.d("COUNT INFO", "Departments: $count")
            }
        }

        // Attendance count
        viewModelScope.launch {
            attendanceDao.getAttendanceCountByEventId(eventId).collect { count ->
                _attendeesCount.value = count
                Log.d("COUNT INFO", "Attendance: $count")
            }
        }
    }

}


