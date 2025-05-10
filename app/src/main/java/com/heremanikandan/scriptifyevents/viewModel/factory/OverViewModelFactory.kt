package com.heremanikandan.scriptifyevents.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heremanikandan.scriptifyevents.db.dao.AttendanceDao
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.dao.SharedWithDao
import com.heremanikandan.scriptifyevents.viewModel.OverviewViewModel

class OverviewViewModelFactory(
    private val eventId: Long,
    private val eventDao: EventDao,
    private  val participantDao: ParticipantDao,
    private val attendanceDao: AttendanceDao,
    private val sharedWithDao: SharedWithDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(eventId ,eventDao,participantDao,attendanceDao, sharedWithDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}