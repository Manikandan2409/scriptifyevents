package com.heremanikandan.scriptifyevents.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//
//class HomeViewModel(private val eventDao: EventDao) : ViewModel() {
//    private val _searchQuery = MutableStateFlow("")
//    private val _sortByDate = MutableStateFlow(false) // Toggle sorting by date (asc/desc)
//    private val _sortByName = MutableStateFlow(false) // Toggle sorting by name (A-Z / Z-A)
//    private val _filterByCreator = MutableStateFlow("") // Filter by creator
//
//    private val allEvents = eventDao.getAllEvents()
//
//    val events: StateFlow<List<Event>> = combine(
//        allEvents,
//        _searchQuery,
//        _sortByDate,
//        _sortByName,
//        _filterByCreator
//    ) { events, query, sortByDate, sortByName, creatorFilter ->
//        var filteredList = events
//
//        // Apply search filter
//        if (query.isNotEmpty()) {
//            filteredList = filteredList.filter {
//                it.name.contains(query, ignoreCase = true) ||
//                        it.description.contains(query, ignoreCase = true)
//            }
//        }
//
//        // Apply creator filter
//        if (creatorFilter.isNotEmpty()) {
//            filteredList = filteredList.filter { it.createdBy == creatorFilter }
//        }
//
//        // Apply sorting (by date or name)
//        when {
//            sortByDate -> filteredList = filteredList.sortedBy { it.dateTimeMillis }
//            sortByName -> filteredList = filteredList.sortedBy { it.name }
//        }
//
//        filteredList
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//    }
//
//    fun toggleSortByDate() {
//        _sortByDate.value = !_sortByDate.value
//        _sortByName.value = false // Disable name sorting when sorting by date
//    }
//
//    fun toggleSortByName() {
//        _sortByName.value = !_sortByName.value
//        _sortByDate.value = false // Disable date sorting when sorting by name
//    }
//
//    fun setFilterByCreator(creator: String) {
//        _filterByCreator.value = creator
//    }
//
//    fun insertEvent(event: Event, onResult: (Boolean) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                eventDao.insertEvent(event)
//                onResult(true) // Success
//            } catch (e: Exception) {
//                onResult(false) // Failure
//            }
//        }
//    }
//
//    suspend fun isEventNameExists(eventName: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            eventDao.getEventByName(eventName) != null
//        }
//    }
//}
//
//class HomeViewModel(private val eventDao: EventDao) : ViewModel() {
//    private val _searchQuery = MutableStateFlow("")
//    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
//    private val _filterType = MutableStateFlow(FilterType.ALL)
//    private val _dateRange = MutableStateFlow<Pair<Long, Long>?>(null)
//
//    private val allEvents = eventDao.getAllEvents()
//
//    val events: StateFlow<List<Event>> = combine(
//        allEvents, _searchQuery, _sortOrder, _filterType, _dateRange
//    ) { events, query, sortOrder, filterType, dateRange ->
//        var filteredList = events
//
//        // Apply search filter
//        if (query.isNotEmpty()) {
//            filteredList = filteredList.filter {
//                it.name.contains(query, ignoreCase = true) ||
//                        it.description.contains(query, ignoreCase = true)
//            }
//        }
//
//        // Apply filter by type
//        filteredList = when (filterType) {
//            FilterType.COMPLETED -> filteredList.filter { it.isCompleted }
//            FilterType.ONGOING -> filteredList.filter { it.isOngoing }
//            FilterType.WAITING -> filteredList.filter { it.isWaiting }
//            FilterType.DISABLED -> filteredList.filter { it.disabled }
//            FilterType.ALL -> filteredList
//        }
//
//        // Apply date range filter
//        dateRange?.let { range ->
//            filteredList = filteredList.filter {
//                it.dateTimeMillis in range.first..range.second
//            }
//        }
//
//        // Apply sorting
//        when (sortOrder) {
//            SortOrder.ASCENDING -> filteredList = filteredList.sortedBy { it.name }
//            SortOrder.DESCENDING -> filteredList = filteredList.sortedByDescending { it.name }
//            SortOrder.DATE_ASC -> filteredList = filteredList.sortedBy { it.dateTimeMillis }
//            SortOrder.DATE_DESC -> filteredList = filteredList.sortedByDescending { it.dateTimeMillis }
//            SortOrder.NONE -> {} // No sorting
//        }
//
//        filteredList
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//    }
//
//    fun updateSortOrder(order: SortOrder) {
//        _sortOrder.value = order
//    }
//
//    fun updateFilterType(filter: FilterType) {
//        _filterType.value = filter
//    }
//
//    fun setDateRange(startDate: Long, endDate: Long) {
//        _dateRange.value = Pair(startDate, endDate)
//    }
//
//    fun insertEvent(event: Event, onResult: (Boolean) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                eventDao.insertEvent(event)
//                onResult(true)
//            } catch (e: Exception) {
//                onResult(false)
//            }
//        }
//    }
//}
//
//enum class SortOrder {
//    NONE, ASCENDING, DESCENDING, DATE_ASC, DATE_DESC
//}
//
//enum class FilterType {
//    ALL, COMPLETED, ONGOING, WAITING, DISABLED
//}


class HomeViewModel(private val eventDao: EventDao) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NONE) // Default: No sorting
    private val _filterByStatus = MutableStateFlow(FilterType.ALL)
    private val _dateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null) // Start & End Date

    private val allEvents = eventDao.getAllEvents()

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

            FilterType.COMPLETED -> filteredList.filter { it.isCompleted }
            FilterType.ONGOING -> filteredList.filter { it.isOngoing }
            FilterType.WAITING -> filteredList.filter { it.isWaiting }
            FilterType.DISABLED -> filteredList.filter { it.disabled }
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
