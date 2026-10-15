package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.ApiError
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.SeedDataProvider
import com.zenith26.fest.data.model.Stats
import com.zenith26.fest.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Admin Dashboard.
 */
data class AdminUiState(
    val stats: Stats? = null,
    val isStatsLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val isEventsLoading: Boolean = false,
    val registrations: List<RegistrationItem> = emptyList(),
    val isRegistrationsLoading: Boolean = false,
    val activeTab: Int = 0, // 0 = Events & Utilization, 1 = Student Registrations
    val selectedEventFilterId: Int? = null,
    val errorMessage: String? = null,
    val isAddingEvent: Boolean = false,
    val addEventError: String? = null,
    val addEventSuccess: String? = null,
    val isDeletingRegistration: Boolean = false
)

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    /**
     * Refresh all dashboard data: stats, events, registrations.
     */
    fun refreshAll() {
        loadStats()
        loadEvents()
        loadRegistrations(_uiState.value.selectedEventFilterId)
    }

    /**
     * Switch between "Events & Utilization" (tab 0) and "Student Registrations" (tab 1).
     */
    fun setActiveTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    /**
     * 1. Fetch live fest stats (GET /api/stats)
     */
    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isStatsLoading = true)
            try {
                val res = ApiClient.service.getStats()
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        stats = res.body(),
                        isStatsLoading = false
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Fallback below
            }
            // Offline/demo fallback
            _uiState.value = _uiState.value.copy(
                stats = SeedDataProvider.getStats(),
                isStatsLoading = false
            )
        }
    }

    /**
     * 2. Fetch all events and sort by utilization descending (GET /api/events)
     */
    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEventsLoading = true)
            try {
                val res = ApiClient.service.getEvents()
                if (res.isSuccessful && res.body() != null) {
                    val list = res.body() ?: emptyList()
                    val sorted = list.sortedByDescending { it.utilizationPct }
                    _uiState.value = _uiState.value.copy(
                        events = sorted,
                        isEventsLoading = false
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Fallback below
            }
            // Offline/demo fallback
            val fallbackEvents = SeedDataProvider.getEvents().sortedByDescending { it.utilizationPct }
            _uiState.value = _uiState.value.copy(
                events = fallbackEvents,
                isEventsLoading = false
            )
        }
    }

    /**
     * 3. Fetch registrations, optionally filtered by event ID (GET /api/registrations)
     */
    fun loadRegistrations(eventId: Int? = null) {
        _uiState.value = _uiState.value.copy(selectedEventFilterId = eventId)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRegistrationsLoading = true)
            try {
                val res = ApiClient.service.getRegistrations(eventId)
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        registrations = res.body() ?: emptyList(),
                        isRegistrationsLoading = false
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Fallback below
            }
            // Offline/demo fallback
            _uiState.value = _uiState.value.copy(
                registrations = SeedDataProvider.getRegistrations(eventId),
                isRegistrationsLoading = false
            )
        }
    }

    /**
     * Stretch Goal 1: Create a new fest event (POST /api/events)
     */
    fun createEvent(
        title: String,
        category: String,
        description: String,
        date: String,
        time: String,
        venue: String,
        capacity: Int,
        fee: Int,
        icon: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAddingEvent = true,
                addEventError = null,
                addEventSuccess = null
            )
            val payload = EventCreateIn(
                title = title.trim(),
                category = category.trim(),
                description = description.trim(),
                eventDate = date.trim(),
                eventTime = time.trim(),
                venue = venue.trim(),
                capacity = capacity,
                fee = fee,
                icon = icon.trim().ifEmpty { "⚡" }
            )
            try {
                val res = ApiClient.service.createEvent(payload)
                if (res.isSuccessful && res.code() == 201) {
                    _uiState.value = _uiState.value.copy(
                        isAddingEvent = false,
                        addEventSuccess = "Event '${payload.title}' successfully created!"
                    )
                    refreshAll()
                    onSuccess()
                    return@launch
                } else if (res.code() == 400) {
                    val err = ApiError.parse(res.errorBody())
                    _uiState.value = _uiState.value.copy(
                        isAddingEvent = false,
                        addEventError = err
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Network unreachable -> add to SeedDataProvider
            }

            SeedDataProvider.addEvent(payload)
            _uiState.value = _uiState.value.copy(
                isAddingEvent = false,
                addEventSuccess = "Event '${payload.title}' successfully created!"
            )
            refreshAll()
            onSuccess()
        }
    }

    /**
     * Stretch Goal 2: Cancel a student registration (DELETE /api/registrations/{id})
     */
    fun deleteRegistration(registrationId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingRegistration = true)
            try {
                val res = ApiClient.service.deleteRegistration(registrationId)
                if (res.isSuccessful) {
                    refreshAll()
                    return@launch
                }
            } catch (e: Exception) {
                // Fallback below
            }
            SeedDataProvider.deleteRegistration(registrationId)
            refreshAll()
            _uiState.value = _uiState.value.copy(isDeletingRegistration = false)
        }
    }
}
