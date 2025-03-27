package com.heremanikandan.scriptifyevents.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.db.repos.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    private val _allAttendance = MutableLiveData<List<Attendance>>()
    val allAttendance: LiveData<List<Attendance>> = _allAttendance

    fun insertAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.insertAttendance(attendance)
        }
    }

    fun updateAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.updateAttendance(attendance)
        }
    }

    fun deleteAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.deleteAttendance(attendance)
        }
    }

    fun deleteAttendanceByEventId(eventId: Long,participantId: Long) {
        viewModelScope.launch {
            repository.deleteAttendanceByEventId(eventId,participantId)
        }
    }

    fun getAttendanceByEventAndUser(eventId: Long, userId: Long) {
        viewModelScope.launch {
            _allAttendance.postValue(repository.getAttendanceByEventAndUser(eventId, userId))
        }
    }

    fun getAttendanceByEventId(eventId: Long) {
        viewModelScope.launch {
            _allAttendance.postValue(repository.getAttendanceByEventId(eventId))
        }
    }

    fun getAttendanceByMode(mode: AttendanceMode) {
        viewModelScope.launch {
            _allAttendance.postValue(repository.getAttendanceByMode(mode))
        }
    }

    fun getParticipantWithAttendance(participantId: Long) {
        viewModelScope.launch {
            val participantWithAttendance = repository.getParticipantWithAttendance(participantId)
            Log.d("AttendanceViewModel", "Data: $participantWithAttendance")
        }
    }
}
