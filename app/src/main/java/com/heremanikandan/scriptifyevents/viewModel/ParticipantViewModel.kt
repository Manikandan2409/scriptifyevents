package com.heremanikandan.scriptifyevents.viewModel

import android.util.Log
import android.util.Log.i
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.model.Participant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParticipantViewModel(
    private val participantDao: ParticipantDao,

    private val eventId: Long
) : ViewModel() {
    private  val TAG ="PARTICIPANT"
    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val filteredParticipants = MutableStateFlow<List<Participant>>(emptyList())
    private var isSorted = false
    init {
        loadParticipants()
    }

//    private fun loadParticipants() {
//        viewModelScope.launch {
//            participantDao.getParticipantsByEventId(eventId).collect {
//                _participants.value = it
//                filteredParticipants.value = it
//            }
//        }
//    }
private fun loadParticipants() {
    viewModelScope.launch {
        participantDao.getParticipantsByEventId(eventId).collect {
            _participants.value = it

            // Show natural order initially or revert if no sorting is applied
            if (!isSorted) {
                filteredParticipants.value = it
            }
        }
    }
}
    fun resetSort() {
        isSorted = false
        filteredParticipants.value = _participants.value
    }

    fun updateSearchQuery(query: String) {
        filteredParticipants.value = if (query.isEmpty()) {
            _participants.value
        } else {
            _participants.value.filter {
                it.name.contains(query, ignoreCase = true) || it.rollNo.contains(query, ignoreCase = true)
            }
        }
    }


    fun sortParticipants(option: String, ascending: Boolean) {
        if (_participants.value.isEmpty()) return

        // Apply sorting only if the user clicks a sort option
        isSorted = true
        filteredParticipants.value = when (option) {
            "id" -> {
                if (ascending)
                    _participants.value.sortedBy { it.id }
                else _participants.value.sortedByDescending { it.name }
            }
            "Name" -> {
                if (ascending) _participants.value.sortedBy { it.name }
                else _participants.value.sortedByDescending { it.name }
            }

            "Roll No" -> {
                if (ascending) _participants.value.sortedBy { it.rollNo }
                else _participants.value.sortedByDescending { it.rollNo }
            }

            else -> _participants.value
        }
    }



    fun addParticipant(participant: Participant) {
        viewModelScope.launch(Dispatchers.IO) {
            i(TAG, "addParticipant: ${participant.name}")
            participantDao.insertParticipant(participant)
            i(TAG, "addParticipant: ${participant.name} inserted")

            // Fetch the updated list of participants and update _participants
            val updatedParticipants = participantDao.getParticipantsByEventId(participant.eventId)

            // Switch to Main thread to update the UI
            withContext(Dispatchers.Main) {
                val updatedList = _participants.value.toMutableList()
                updatedList.add(participant)
                // Update filteredParticipants based on sorting or search if applicable
                // Update StateFlow with the new list
                _participants.value = updatedList.toList()
                filteredParticipants.value = updatedList.toList()
                Log.i(TAG, "addParticipant: Updated list size = ${updatedList.size}")
            }
        }
    }

    fun deleteParticipant(participant: Participant){
        viewModelScope.launch(Dispatchers.IO){
            participantDao.deleteParticipant(participant)

            withContext(Dispatchers.Main){
                val last = participantDao.getParticipantById(participant.eventId)
                loadParticipants()
            }

        }
    }

}

