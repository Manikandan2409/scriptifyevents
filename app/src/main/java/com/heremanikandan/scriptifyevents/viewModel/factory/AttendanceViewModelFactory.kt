package com.heremanikandan.scriptifyevents.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.repos.AttendanceRepository
import com.heremanikandan.scriptifyevents.viewModel.AttendanceViewModel

class AttendanceViewModelFactory(private val repository: AttendanceRepository,private val eventId:Long,private val participantDao: ParticipantDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(repository, eventId = eventId,participantDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
