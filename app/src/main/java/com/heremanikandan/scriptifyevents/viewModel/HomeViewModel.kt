package com.heremanikandan.scriptifyevents.viewModel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.EventStatus
import com.heremanikandan.scriptifyevents.sharedPref.SharedPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(private val eventDao: EventDao,private val context: Context) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NONE) // Default: No sorting
    private val _filterByStatus = MutableStateFlow(FilterType.ALL)
    private val _dateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null) // Start & End Date
    private  val sharedPrefManager = SharedPrefManager(context)
    private val allEvents = eventDao.getAllEventsByUserId(sharedPrefManager.getUserUid()!!)

    val filteredEvents: StateFlow<List<Event>> = combine(
        allEvents,
        _searchQuery,
        _sortOrder,
        _filterByStatus,
        _dateRange
    ) { events, query, sortOrder, filterType, dateRange ->
        var filteredList = events

        // ✅ **Apply search filter**
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        // ✅ **Apply event status filter**
        filteredList = when (filterType) {

            FilterType.COMPLETED -> filteredList.filter { it.status == EventStatus.COMPLETED }
            FilterType.ONGOING -> filteredList.filter { it.status == EventStatus.ONGOING }
            FilterType.WAITING -> filteredList.filter { it.status == EventStatus.WAITING}
            FilterType.DISABLED -> filteredList.filter { it.status == EventStatus.DISABLED }
            FilterType.ALL -> filteredList
        }

        // ✅ **Apply date range filter**
        dateRange.first?.let { startDate ->
            filteredList = filteredList.filter { it.dateTimeMillis >= startDate }
        }
        dateRange.second?.let { endDate ->
            filteredList = filteredList.filter { it.dateTimeMillis <= endDate }
        }

        // ✅ **Apply sorting**
        filteredList = when (sortOrder) {
            SortOrder.ASCENDING -> filteredList.sortedBy { it.name }
            SortOrder.DESCENDING -> filteredList.sortedByDescending { it.name }
            SortOrder.DATE_ASCENDING -> filteredList.sortedBy { it.dateTimeMillis }
            SortOrder.DATE_DESCENDING -> filteredList.sortedByDescending { it.dateTimeMillis }
            SortOrder.NONE -> filteredList
        }

        filteredList
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun  getEventById(eventId:Long):Event?{
         return   eventDao.getEventById(eventId)
    }

    suspend fun deleteEvent(event: Event){
        eventDao.deleteEvent(event)
    }

    // ✅ **Update search query**
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ✅ **Set filter type**
    fun setFilter(filter: FilterType) {
        _filterByStatus.value = filter

    }

    // ✅ **Set date range**
    fun setDateRange(start: Long?, end: Long?) {
        _dateRange.value = start to end
    }

    // ✅ **Set sorting order**
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun insertEvent(event: Event, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                eventDao.insertEvent(event)
                onResult(true) // Success
            } catch (e: Exception) {
                onResult(false) // Failure
            }
        }
    }
    fun sortEvents(ascending: Boolean) {
        _sortOrder.value = if (ascending) SortOrder.ASCENDING else SortOrder.DESCENDING
    }
    suspend fun isEventNameExists(eventName: String): Boolean {
        return withContext(Dispatchers.IO) {
            eventDao.getEventByName(eventName) != null
        }
    }
}

// ✅ **Enums for Sorting & Filtering**
enum class SortOrder {
    NONE, ASCENDING, DESCENDING, DATE_ASCENDING, DATE_DESCENDING
}

enum class FilterType {
    ALL, COMPLETED, ONGOING, WAITING, DISABLED
}
