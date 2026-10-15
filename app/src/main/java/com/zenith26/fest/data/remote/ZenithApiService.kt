package com.zenith26.fest.data.remote

import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.Stats
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit REST API interface for Zenith '26 College Fest Management System.
 * Matches all FastAPI backend endpoints and API contracts exactly.
 */
interface ZenithApiService {

    /**
     * GET /api/events
     * Fetch events list with live seat calculation.
     * Supports optional category filter and title search query parameters.
     */
    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Event>>

    /**
     * GET /api/events/{event_id}
     * Retrieve single event details by id.
     */
    @GET("api/events/{event_id}")
    suspend fun getEventDetail(
        @Path("event_id") eventId: Int
    ): Response<Event>

    /**
     * POST /api/events/{event_id}/register
     * Register a student for an event.
     */
    @POST("api/events/{event_id}/register")
    suspend fun registerForEvent(
        @Path("event_id") eventId: Int,
        @Body payload: RegistrationIn
    ): Response<RegistrationOut>

    /**
     * GET /api/registrations
     * Retrieve registrations, optionally filtered by event_id.
     */
    @GET("api/registrations")
    suspend fun getRegistrations(
        @Query("event_id") eventId: Int? = null
    ): Response<List<RegistrationItem>>

    /**
     * GET /api/stats
     * Live aggregated fest metrics for admin dashboard.
     */
    @GET("api/stats")
    suspend fun getStats(): Response<Stats>

    /**
     * POST /api/events
     * Admin stretch goal: Add new fest event with server validation.
     */
    @POST("api/events")
    suspend fun createEvent(
        @Body payload: EventCreateIn
    ): Response<Event>

    /**
     * DELETE /api/registrations/{registration_id}
     * Admin stretch goal: Cancel a registration.
     */
    @DELETE("api/registrations/{registration_id}")
    suspend fun deleteRegistration(
        @Path("registration_id") registrationId: Int
    ): Response<Map<String, Any>>
}
