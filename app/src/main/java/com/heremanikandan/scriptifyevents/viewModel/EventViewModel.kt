package com.heremanikandan.scriptifyevents.viewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.repos.EventRepo
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepo) : ViewModel() {

    var insertSuccess = mutableStateOf<Boolean?>(null) // Mutable state to observe UI

    fun insertEvent(event: Event, reminderTimeMillis: Long?) {
        viewModelScope.launch {
            val success = repository.insertEventWithReminder(event, reminderTimeMillis)
            insertSuccess.value = success // Update UI state
        }
    }
}
