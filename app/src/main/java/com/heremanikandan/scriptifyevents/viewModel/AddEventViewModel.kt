package com.heremanikandan.scriptifyevents.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEventViewModel(private val eventDao: EventDao) : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList()) // StateFlow to hold list
    val events: StateFlow<List<Event>> = _events
    fun insertEvent(event: Event, onResult: (Boolean) -> Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id= eventDao.insertEvent(event)
                //val inserted = eventDao.getEventById(eventid)
                //if (inserted!!.name == event.name)
                onResult(true)  // Pass true if found, false if not
            } catch (e: Exception) {
                onResult(false) // Insertion failed
            }
        }
    }
    suspend fun getEventById(id: Long): Event? {
        return eventDao.getEventById(id)
    }
    fun updateSpreadsheetId(eventId: Long, spreadsheetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                eventDao.updateSpreadsheetId(eventId, spreadsheetId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun isEventNameExists(eventName: String): Boolean {
        return withContext(Dispatchers.IO) {
            eventDao.getEventByName(eventName) != null
        }
    }
}
