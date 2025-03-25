package com.heremanikandan.scriptifyevents.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.viewModel.ParticipantViewModel

class ParticipantViewModelFactory(
    private val participantDao: ParticipantDao,
    private val eventId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParticipantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParticipantViewModel(participantDao, eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}