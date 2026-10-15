package com.zenith26.fest.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Embedded repository containing all 14 official Zenith '26 seed events.
 * Provides offline fallback so the application works seamlessly in cloud
 * emulators (such as Appetize.io), offline environments, or when the local
 * backend server is unreachable.
 */
object SeedDataProvider {

    private val initialEvents = listOf(
        Event(
            id = 1,
            title = "Code Sprint 2.0",
            category = "Technical",
            description = "An intense 4-hour competitive programming sprint solving complex algorithmic challenges under strict time constraints.",
            eventDate = "2026-11-14",
            eventTime = "10:00 AM",
            venue = "Main Auditorium",
            capacity = 100,
            fee = 0,
            icon = "💻",
            availableSeats = 100
        ),
        Event(
            id = 2,
            title = "Robo Race",
            category = "Technical",
            description = "Design, build, and navigate custom obstacle-racing robots through challenging terrain to clock the fastest time.",
            eventDate = "2026-11-14",
            eventTime = "02:00 PM",
            venue = "Engineering Lab 2",
            capacity = 40,
            fee = 50,
            icon = "🤖",
            availableSeats = 40
        ),
        Event(
            id = 3,
            title = "AI Pitch Fest",
            category = "Technical",
            description = "Pitch your breakthrough generative AI and machine learning product concepts to top founders and venture technologists.",
            eventDate = "2026-11-15",
            eventTime = "11:00 AM",
            venue = "Seminar Hall A",
            capacity = 60,
            fee = 0,
            icon = "💡",
            availableSeats = 60
        ),
        Event(
            id = 4,
            title = "Web Design Rumble",
            category = "Technical",
            description = "Rapid-fire frontend design battle where creators craft responsive, accessible, and stunning user interfaces from scratch.",
            eventDate = "2026-11-14",
            eventTime = "04:00 PM",
            venue = "Computer Lab 1",
            capacity = 50,
            fee = 0,
            icon = "🎨",
            availableSeats = 50
        ),
        Event(
            id = 5,
            title = "Battle of Bands",
            category = "Cultural",
            description = "College rock, metal, and indie bands collide under the amphitheatre lights for ultimate musical supremacy and fest glory.",
            eventDate = "2026-11-15",
            eventTime = "06:00 PM",
            venue = "Open Air Theatre",
            capacity = 300,
            fee = 0,
            icon = "🎸",
            availableSeats = 300
        ),
        Event(
            id = 6,
            title = "Dance Fiesta",
            category = "Cultural",
            description = "Electrifying dance battle showcasing solo and group choreographies across hip-hop, classical, contemporary, and street.",
            eventDate = "2026-11-14",
            eventTime = "07:00 PM",
            venue = "Open Air Theatre",
            capacity = 200,
            fee = 0,
            icon = "💃",
            availableSeats = 200
        ),
        Event(
            id = 7,
            title = "Nukkad Natak",
            category = "Cultural",
            description = "Traditional street theatre competition delivering poignant socio-cultural messages with raw acoustic beats and high energy.",
            eventDate = "2026-11-15",
            eventTime = "03:00 PM",
            venue = "Amphitheatre",
            capacity = 150,
            fee = 0,
            icon = "🎭",
            availableSeats = 150
        ),
        Event(
            id = 8,
            title = "Fashion Fusion",
            category = "Cultural",
            description = "Runway couture showcase celebrating ethnic textiles fused with avant-garde modern and sustainable fashion statements.",
            eventDate = "2026-11-15",
            eventTime = "08:00 PM",
            venue = "Main Stage",
            capacity = 250,
            fee = 100,
            icon = "👗",
            availableSeats = 250
        ),
        Event(
            id = 9,
            title = "Open Mic Night",
            category = "Cultural",
            description = "Intimate evening of spoken word poetry, acoustic performances, stand-up comedy, and campus storytelling.",
            eventDate = "2026-11-14",
            eventTime = "09:00 PM",
            venue = "Cafeteria Lawn",
            capacity = 100,
            fee = 0,
            icon = "🎤",
            availableSeats = 100
        ),
        Event(
            id = 10,
            title = "Five-a-Side Football",
            category = "Sports",
            description = "High-tempo 5v5 soccer tournament on turf pitches featuring knockout rounds and golden-goal thrillers.",
            eventDate = "2026-11-14",
            eventTime = "08:00 AM",
            venue = "Sports Ground",
            capacity = 80,
            fee = 0,
            icon = "⚽",
            availableSeats = 80
        ),
        Event(
            id = 11,
            title = "Chess Championship",
            category = "Sports",
            description = "Timed rapid and blitz Swiss-system chess championship testing tactical foresight and strategic precision.",
            eventDate = "2026-11-15",
            eventTime = "09:00 AM",
            venue = "Indoor Games Hall",
            capacity = 32,
            fee = 0,
            icon = "♟️",
            availableSeats = 32
        ),
        Event(
            id = 12,
            title = "Valorant Showdown",
            category = "Gaming",
            description = "Competitive 5v5 tactical shooter esports bracket played on LAN tournament servers with spectator casting.",
            eventDate = "2026-11-14",
            eventTime = "01:00 PM",
            venue = "Gaming Arena",
            capacity = 60,
            fee = 0,
            icon = "🎮",
            availableSeats = 60
        ),
        Event(
            id = 13,
            title = "UI/UX Design Workshop",
            category = "Workshop",
            description = "Hands-on studio session mastering user journey mapping, design systems, usability testing, and high-fidelity Figma components.",
            eventDate = "2026-11-14",
            eventTime = "11:00 AM",
            venue = "Design Lab",
            capacity = 40,
            fee = 0,
            icon = "🖌️",
            availableSeats = 40
        ),
        Event(
            id = 14,
            title = "Intro to Vibe Coding",
            category = "Workshop",
            description = "Master the art of AI-assisted software engineering and rapid product prototyping using Antigravity, LLMs, and prompt architectures.",
            eventDate = "2026-11-15",
            eventTime = "10:00 AM",
            venue = "Computer Lab 1",
            capacity = 45,
            fee = 0,
            icon = "🚀",
            availableSeats = 45
        )
    )

    private val events = mutableListOf<Event>().apply { addAll(initialEvents) }
    private val registrations = mutableListOf<RegistrationItem>()
    private var nextRegId = 1
    private var nextEventId = 15

    @Synchronized
    fun getEvents(category: String? = null, search: String? = null): List<Event> {
        return events.filter { ev ->
            val matchCat = category.isNullOrBlank() || category.equals("All", ignoreCase = true) || ev.category.equals(category, ignoreCase = true)
            val matchSearch = search.isNullOrBlank() || ev.title.contains(search.trim(), ignoreCase = true)
            matchCat && matchSearch
        }
    }

    @Synchronized
    fun getEvent(id: Int): Event? {
        return events.find { it.id == id }
    }

    @Synchronized
    fun register(eventId: Int, reg: RegistrationIn): Result<RegistrationOut> {
        val eventIndex = events.indexOfFirst { it.id == eventId }
        if (eventIndex == -1) {
            return Result.failure(Exception("Event not found"))
        }

        val event = events[eventIndex]
        if (event.availableSeats <= 0) {
            return Result.failure(Exception("Event is full"))
        }

        // Check duplicate email
        val duplicate = registrations.any { it.eventId == eventId && it.email.equals(reg.email.trim(), ignoreCase = true) }
        if (duplicate) {
            return Result.failure(Exception("Already registered"))
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        val regItem = RegistrationItem(
            id = nextRegId++,
            eventId = eventId,
            studentName = reg.studentName.trim(),
            rollNumber = reg.rollNumber.trim(),
            email = reg.email.trim(),
            phone = reg.phone?.trim(),
            registeredAt = timestamp,
            eventTitle = event.title
        )
        registrations.add(regItem)

        // Decrement available seats
        events[eventIndex] = event.copy(availableSeats = (event.availableSeats - 1).coerceAtLeast(0))

        return Result.success(
            RegistrationOut(
                id = regItem.id,
                eventId = regItem.eventId,
                studentName = regItem.studentName,
                registeredAt = regItem.registeredAt
            )
        )
    }

    @Synchronized
    fun getRegistrations(eventId: Int? = null): List<RegistrationItem> {
        return if (eventId != null) {
            registrations.filter { it.eventId == eventId }
        } else {
            registrations.toList()
        }
    }

    @Synchronized
    fun deleteRegistration(id: Int): Boolean {
        val reg = registrations.find { it.id == id } ?: return false
        registrations.remove(reg)
        val eventIndex = events.indexOfFirst { it.id == reg.eventId }
        if (eventIndex != -1) {
            val ev = events[eventIndex]
            events[eventIndex] = ev.copy(availableSeats = (ev.availableSeats + 1).coerceAtMost(ev.capacity))
        }
        return true
    }

    @Synchronized
    fun addEvent(payload: EventCreateIn): Event {
        val ev = Event(
            id = nextEventId++,
            title = payload.title,
            category = payload.category,
            description = payload.description,
            eventDate = payload.eventDate,
            eventTime = payload.eventTime,
            venue = payload.venue,
            capacity = payload.capacity,
            fee = payload.fee,
            icon = payload.icon ?: "⚡",
            availableSeats = payload.capacity
        )
        events.add(ev)
        return ev
    }

    @Synchronized
    fun getStats(): Stats {
        val totalEvents = events.size
        val totalRegs = registrations.size
        val totalCap = events.sumOf { it.capacity }
        val util = if (totalCap > 0) {
            String.format(Locale.US, "%.1f", (totalRegs.toDouble() / totalCap.toDouble()) * 100.0).toDoubleOrNull() ?: 0.0
        } else 0.0

        val categoryMap = events.groupingBy { it.category }.eachCount()

        return Stats(
            totalEvents = totalEvents,
            totalRegistrations = totalRegs,
            totalCapacity = totalCap,
            overallUtilizationPct = util,
            upcomingEventsCount = totalEvents,
            eventsByCategory = categoryMap
        )
    }
}
