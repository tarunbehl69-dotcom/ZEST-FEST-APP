package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.ApiError
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.SeedDataProvider
import com.zenith26.fest.data.remote.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Student Event Portal.
 */
data class StudentUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOfflineMode: Boolean = false,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val selectedEvent: Event? = null,
    val isDetailLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val registrationSuccessMessage: String? = null,
    val registrationErrorMessage: String? = null
)

class StudentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        fetchEvents()
    }

    /**
     * Fetch events from API using active category and search filter parameters.
     */
    fun fetchEvents() {
        val current = _uiState.value
        val categoryParam = if (current.selectedCategory != "All") current.selectedCategory else null
        val searchParam = current.searchQuery.trim().ifEmpty { null }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                val response = ApiClient.service.getEvents(
                    category = categoryParam,
                    search = searchParam
                )
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        events = list,
                        isLoading = false,
                        errorMessage = null,
                        isOfflineMode = false
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Network unreachable or timeout -> fall back to SeedDataProvider
            }

            // Seamless offline/demo fallback with all 14 official seed events
            val fallbackEvents = SeedDataProvider.getEvents(categoryParam, searchParam)
            _uiState.value = _uiState.value.copy(
                events = fallbackEvents,
                isLoading = false,
                errorMessage = null,
                isOfflineMode = true
            )
        }
    }

    /**
     * Select a category filter.
     */
    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        fetchEvents()
    }

    /**
     * Update search query with debounce.
     */
    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(350)
            fetchEvents()
        }
    }

    /**
     * Clear search filter.
     */
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        fetchEvents()
    }

    /**
     * Reset all filters to default.
     */
    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategory = "All",
            searchQuery = ""
        )
        fetchEvents()
    }

    /**
     * Load specific event detail when user taps "View Details".
     */
    fun openEventDetails(event: Event) {
        _uiState.value = _uiState.value.copy(
            selectedEvent = event,
            isDetailLoading = true,
            registrationSuccessMessage = null,
            registrationErrorMessage = null
        )
        viewModelScope.launch {
            try {
                val response = ApiClient.service.getEventDetail(event.id)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedEvent = response.body(),
                        isDetailLoading = false
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Ignore network error; fallback below
            }
            val localEvent = SeedDataProvider.getEvent(event.id) ?: event
            _uiState.value = _uiState.value.copy(
                selectedEvent = localEvent,
                isDetailLoading = false
            )
        }
    }

    /**
     * Dismiss the event detail modal.
     */
    fun closeEventDetails() {
        _uiState.value = _uiState.value.copy(
            selectedEvent = null,
            registrationSuccessMessage = null,
            registrationErrorMessage = null,
            isRegistering = false
        )
    }

    /**
     * Register student for event (Step 7).
     */
    fun registerForEvent(
        eventId: Int,
        studentName: String,
        rollNumber: String,
        email: String,
        phone: String?
    ) {
        // Client-side checks
        val nameTrimmed = studentName.trim()
        val rollTrimmed = rollNumber.trim()
        val emailTrimmed = email.trim()

        if (nameTrimmed.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                registrationErrorMessage = "Please enter your full name."
            )
            return
        }
        if (rollTrimmed.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                registrationErrorMessage = "Please enter your roll number."
            )
            return
        }
        if (emailTrimmed.isEmpty() || !emailTrimmed.contains("@")) {
            _uiState.value = _uiState.value.copy(
                registrationErrorMessage = "Please provide a valid email containing '@'."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRegistering = true,
                registrationSuccessMessage = null,
                registrationErrorMessage = null
            )
            val payload = RegistrationIn(
                studentName = nameTrimmed,
                rollNumber = rollTrimmed,
                email = emailTrimmed,
                phone = phone?.trim()?.ifEmpty { null }
            )

            try {
                val response = ApiClient.service.registerForEvent(eventId, payload)

                if (response.isSuccessful && response.code() == 201) {
                    val regOut = response.body()
                    val eventTitle = _uiState.value.selectedEvent?.title ?: "this event"
                    _uiState.value = _uiState.value.copy(
                        isRegistering = false,
                        registrationSuccessMessage = "Registration confirmed for ${regOut?.studentName ?: nameTrimmed} in $eventTitle!",
                        registrationErrorMessage = null
                    )
                    // Refresh events
                    fetchEvents()
                    return@launch
                } else if (response.code() == 409 || response.code() == 400) {
                    val errorDetail = ApiError.parse(response.errorBody())
                    _uiState.value = _uiState.value.copy(
                        isRegistering = false,
                        registrationErrorMessage = errorDetail
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // If network fails (e.g. Appetize cloud / offline), execute local seed registration
            }

            // Local seed data registration
            val localResult = SeedDataProvider.register(eventId, payload)
            localResult.onSuccess { regOut ->
                val eventTitle = _uiState.value.selectedEvent?.title ?: "this event"
                _uiState.value = _uiState.value.copy(
                    isRegistering = false,
                    registrationSuccessMessage = "Registration confirmed for ${regOut.studentName} in $eventTitle!",
                    registrationErrorMessage = null
                )
                fetchEvents()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isRegistering = false,
                    registrationErrorMessage = err.message ?: "Registration failed."
                )
            }
        }
    }
}
