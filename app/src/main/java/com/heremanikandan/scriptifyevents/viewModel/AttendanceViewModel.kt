package com.heremanikandan.scriptifyevents.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.dto.AttendanceDTO
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.db.model.Participant
import com.heremanikandan.scriptifyevents.db.repos.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository,private val eventId: Long,private  val participantDao: ParticipantDao) : ViewModel() {

    private val _attendance = MutableStateFlow<List<AttendanceDTO>>(emptyList())
    val filteredAttendence = MutableStateFlow<List<AttendanceDTO>>(emptyList())
    private  val TAG ="ATTENDANCE"
    private var isSorted = false


    init {
        //loadAttendance()
        getAttendanceByEventId(eventId)
    }

    fun getAttendanceByEventId(eventId: Long) {

        viewModelScope.launch {
            repository.getAttendanceByEventId(eventId = eventId).collect{
                _attendance.value = it
                if (!isSorted)
                    filteredAttendence.value = it
            }
        }
    }
    fun updateSearchQuery(query: String) {
        filteredAttendence.value = if (query.isEmpty()) {
            _attendance.value
        } else {
            _attendance.value.filter {
                it.name.contains(query, ignoreCase = true) || it.rollNo.contains(query, ignoreCase = true)
            }
        }
    }
    /* Sort
    */

    fun sortAttendance(option: String, ascending: Boolean) {
        if (_attendance.value.isEmpty()) return

        // Apply sorting only if the user clicks a sort option
        isSorted = true
        filteredAttendence.value = when (option) {
            "Id" ->{
                if (ascending) _attendance.value.sortedBy { it.attendanceId }
                else _attendance.value.sortedByDescending { it.attendanceId }
            }
            "Name" -> {
                if (ascending) _attendance.value.sortedBy { it.name }
                else _attendance.value.sortedByDescending { it.name }
            }

            "Roll No" -> {
                if (ascending) _attendance.value.sortedBy { it.rollNo }
                else _attendance.value.sortedByDescending { it.rollNo }
            }

            else -> _attendance.value
        }
    }


    suspend fun getParticipantID(rollNo: String): Long? {
        val participant = participantDao.getParticipantByRollNO(rollNo, eventId)
        return if (participant != null) {
            Log.d(TAG,"ID: ${participant.id} name: ${participant.name} ")

            participant.id
        } else {

            //    createParticipant(rollNo)
            return null;
        }
    }

   suspend fun createParticipant(rollNo: String) :Long {
        val newParticipant = Participant(
            name = "unknown user",
            rollNo = rollNo,
            eventId = eventId,
            email = "unknown email",
            course = "unknown course"
        )

       Log.d(TAG,"ID: ${newParticipant.id} name: ${newParticipant.name} ")

       return  participantDao.insertParticipant(newParticipant)

    }

    fun deleteAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.deleteAttendance(attendance)
        }
    }

    fun getAttendanceByEventAndUser(eventId: Long, userId: Long) {
        viewModelScope.launch {
            repository.getAttendanceByEventAndUser(eventId, userId).collect{
                _attendance.value = it
            }
        }
    }


    fun getAttendanceByMode(mode: AttendanceMode) {
        viewModelScope.launch {
            repository.getAttendanceByMode(mode).collect{
                _attendance.value = it
            }
        }
    }

/*  for future reference

  */

    fun insertAttendance(attendance: Attendance) {
        viewModelScope.launch {
            Log.d(TAG,"crossed  atttendance viewmodel")
            repository.insertAttendance(attendance)
            Log.d(TAG,"returned to attendance viewModel")
        }
    }

}
