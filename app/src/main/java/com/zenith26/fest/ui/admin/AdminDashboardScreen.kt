package com.zenith26.fest.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.ui.common.CategoryBadge
import com.zenith26.fest.ui.common.StatusBadge
import com.zenith26.fest.ui.theme.Amber300
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Amber500
import com.zenith26.fest.ui.theme.Blue400
import com.zenith26.fest.ui.theme.Blue500
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Indigo400
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Red400
import com.zenith26.fest.ui.theme.Red500
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.TextWhite
import com.zenith26.fest.ui.viewmodel.AdminViewModel

/**
 * Admin Operations & Monitoring Dashboard Screen.
 * Implements Step 8 (Stat cards, Category breakdown, Sorted events table, Registrations tab).
 */
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showAddEventModal by remember { mutableStateOf(false) }
    var registrationToDelete by remember { mutableStateOf<RegistrationItem?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Dashboard Subheader with Action Buttons
        item {
            AdminHeaderActions(
                onAddEventClick = { showAddEventModal = true },
                onRefreshClick = { viewModel.refreshAll() }
            )
        }

        // 2. Fest Overview (Live Metrics Stat Cards) (Step 8.1)
        item {
            FestOverviewSection(
                stats = state.stats,
                isLoading = state.isStatsLoading
            )
        }

        // 3. Category Breakdown (Step 8.2)
        item {
            CategoryBreakdownSection(
                eventsByCategory = state.stats?.eventsByCategory ?: emptyMap()
            )
        }

        // 4. Navigation Tabs (Events vs Registrations) (Step 8.3 & 8.5)
        item {
            AdminNavigationTabs(
                activeTab = state.activeTab,
                eventsCount = state.events.size,
                regsCount = state.registrations.size,
                onTabSelect = { viewModel.setActiveTab(it) }
            )
        }

        // 5. Tab View Content
        if (state.activeTab == 0) {
            // Tab 1: Events & Capacity Utilization (Sorted descending, >90% highlighted)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sorted by Capacity Utilization (Highest First)", color = Slate400, fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(7.dp).background(Amber400, RoundedCornerShape(100.dp)))
                        Text("Highlighted if > 90% cap", color = Slate400, fontSize = 11.sp)
                    }
                }
            }

            if (state.isEventsLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Purple500, modifier = Modifier.size(32.dp))
                    }
                }
            } else if (state.events.isEmpty()) {
                item {
                    Text("No events found.", color = Slate500, fontSize = 12.sp, modifier = Modifier.padding(24.dp))
                }
            } else {
                items(state.events, key = { it.id }) { event ->
                    AdminEventRowCard(
                        event = event,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            // Tab 2: Student Registrations with Event Filter
            item {
                RegistrationsFilterHeader(
                    events = state.events,
                    selectedEventId = state.selectedEventFilterId,
                    registrationsCount = state.registrations.size,
                    onSelectEvent = { viewModel.loadRegistrations(it) }
                )
            }

            if (state.isRegistrationsLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Purple500, modifier = Modifier.size(32.dp))
                    }
                }
            } else if (state.registrations.isEmpty()) {
                item {
                    Text("No registrations found for this filter.", color = Slate500, fontSize = 12.sp, modifier = Modifier.padding(24.dp))
                }
            } else {
                items(state.registrations, key = { it.id }) { reg ->
                    AdminRegistrationRowCard(
                        registration = reg,
                        onDeleteClick = { registrationToDelete = reg },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // Modal: Add Event (Stretch Goal 1)
    if (showAddEventModal) {
        AddEventModal(
            isSubmitting = state.isAddingEvent,
            errorMessage = state.addEventError,
            onSave = { title, cat, desc, date, time, venue, cap, fee, icon ->
                viewModel.createEvent(title, cat, desc, date, time, venue, cap, fee, icon) {
                    showAddEventModal = false
                }
            },
            onDismiss = { showAddEventModal = false }
        )
    }

    // Dialog: Delete Registration Confirmation (Stretch Goal 2)
    if (registrationToDelete != null) {
        AlertDialog(
            onDismissRequest = { registrationToDelete = null },
            containerColor = Slate900,
            title = {
                Text("Cancel Registration", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to cancel the registration for ${registrationToDelete!!.studentName} in '${registrationToDelete!!.eventTitle}'? This will immediately free up one seat.",
                    color = Slate300,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = registrationToDelete!!.id
                        registrationToDelete = null
                        viewModel.deleteRegistration(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel Registration", color = TextWhite, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { registrationToDelete = null }) {
                    Text("Keep Registration", color = Slate400, fontSize = 12.sp)
                }
            }
        )
    }
}

/**
 * Admin Top Action Bar: Add Event & Refresh Data buttons.
 */
@Composable
private fun AdminHeaderActions(
    onAddEventClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Fest Overview (Live Metrics)", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text("Real-Time SQLite Sync", color = Slate500, fontSize = 10.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Add Event Button (Stretch Goal)
            Button(
                onClick = onAddEventClick,
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                    Text("Add Event", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Refresh Button
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier
                    .size(34.dp)
                    .background(Slate900, RoundedCornerShape(10.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Slate300, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * Fest Overview Section: 4 Stat Cards.
 */
@Composable
private fun FestOverviewSection(
    stats: com.zenith26.fest.data.model.Stats?,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stat Card 1: Total Events
            StatCard(
                title = "TOTAL EVENTS",
                value = stats?.totalEvents?.toString() ?: "--",
                subtitle = "cataloged",
                iconEmoji = "📅",
                iconColor = Blue400,
                modifier = Modifier.weight(1f)
            )

            // Stat Card 2: Total Registrations
            StatCard(
                title = "REGISTRATIONS",
                value = stats?.totalRegistrations?.toString() ?: "--",
                subtitle = "students confirmed",
                iconEmoji = "🎟️",
                iconColor = Emerald400,
                valueColor = Emerald400,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stat Card 3: Capacity Utilization
            val utilPct = stats?.overallUtilizationPct ?: 0.0
            StatCardWithProgress(
                title = "CAPACITY UTIL.",
                value = "$utilPct%",
                ratio = "${stats?.totalRegistrations ?: 0} / ${stats?.totalCapacity ?: 0} seats",
                pct = (utilPct / 100.0).toFloat().coerceIn(0f, 1f),
                iconEmoji = "📊",
                iconColor = Purple400,
                modifier = Modifier.weight(1f)
            )

            // Stat Card 4: Upcoming Events
            StatCard(
                title = "UPCOMING EVENTS",
                value = stats?.upcomingEventsCount?.toString() ?: "--",
                subtitle = "scheduled",
                iconEmoji = "⏳",
                iconColor = Amber400,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    iconEmoji: String,
    iconColor: Color,
    valueColor: Color = TextWhite,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Slate900.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iconEmoji, fontSize = 14.sp)
                }
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(value, color = valueColor, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(subtitle, color = Slate400, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun StatCardWithProgress(
    title: String,
    value: String,
    ratio: String,
    pct: Float,
    iconEmoji: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Slate900.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iconEmoji, fontSize = 14.sp)
                }
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(value, color = Purple400, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(ratio, color = Slate400, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            LinearProgressIndicator(
                progress = { pct },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = Purple500,
                trackColor = Slate800
            )
        }
    }
}

/**
 * Category Breakdown Row (Step 8.2).
 */
@Composable
private fun CategoryBreakdownSection(
    eventsByCategory: Map<String, Int>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(Slate900.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .border(1.dp, Slate800.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("EVENTS BY CATEGORY", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (eventsByCategory.isEmpty()) {
                    Text("Loading category breakdown...", color = Slate500, fontSize = 11.sp)
                } else {
                    eventsByCategory.forEach { (cat, count) ->
                        Box(
                            modifier = Modifier
                                .background(Slate800.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                                .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(cat, color = Slate200, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Purple600, RoundedCornerShape(100.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(count.toString(), color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Navigation Tabs between Events and Registrations.
 */
@Composable
private fun AdminNavigationTabs(
    activeTab: Int,
    eventsCount: Int,
    regsCount: Int,
    onTabSelect: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = activeTab,
        containerColor = Slate950,
        contentColor = Purple400,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                color = Purple500,
                height = 2.dp
            )
        },
        divider = { HorizontalDivider(color = Slate800) },
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Tab(
            selected = activeTab == 0,
            onClick = { onTabSelect(0) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Events & Utilization",
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 0) Purple400 else Slate400
                    )
                    Box(modifier = Modifier.background(Purple500.copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                        Text(eventsCount.toString(), color = Purple300, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )

        Tab(
            selected = activeTab == 1,
            onClick = { onTabSelect(1) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Registrations",
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 1) Purple400 else Slate400
                    )
                    Box(modifier = Modifier.background(Slate800, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                        Text(regsCount.toString(), color = Slate300, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

/**
 * Event Row Card for Admin View.
 * Highlighted with Amber when utilization >= 90%.
 */
@Composable
private fun AdminEventRowCard(
    event: Event,
    modifier: Modifier = Modifier
) {
    val isHighDemand = event.utilizationPct >= 90.0
    val isFull = event.isFull

    val bg = if (isHighDemand) Amber500.copy(alpha = 0.08f) else Slate900.copy(alpha = 0.8f)
    val border = if (isHighDemand) Amber500.copy(alpha = 0.4f) else Slate800

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(event.icon ?: "⚡", fontSize = 20.sp)
                    Column {
                        Text(event.title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("ID #${event.id} • ${event.venue}", color = Slate400, fontSize = 11.sp)
                    }
                }

                StatusBadge(isFull = isFull, isHighDemand = isHighDemand, availableSeats = event.availableSeats)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category = event.category)
                Text("${event.eventDate} • ${event.eventTime}", color = Slate400, fontSize = 11.sp)
            }

            // Utilization Bar and stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    LinearProgressIndicator(
                        progress = { (event.utilizationPct / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(5.dp),
                        color = if (isFull) Red500 else if (isHighDemand) Amber400 else Purple500,
                        trackColor = Slate800
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${event.registeredCount} / ${event.capacity}",
                        color = if (isHighDemand) Amber300 else Slate300,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${event.utilizationPct}%)",
                        color = if (isHighDemand) Amber400 else Slate400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Filter header for registrations list.
 */
@Composable
private fun RegistrationsFilterHeader(
    events: List<Event>,
    selectedEventId: Int?,
    registrationsCount: Int,
    onSelectEvent: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentSelectedTitle = events.firstOrNull { it.id == selectedEventId }?.let { "${it.icon ?: ""} ${it.title}" } ?: "All Events"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("FILTER BY EVENT", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Showing $registrationsCount registrations", color = Slate400, fontSize = 11.sp)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(12.dp))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentSelectedTitle, color = Slate200, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate400)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Slate900)
            ) {
                DropdownMenuItem(
                    text = { Text("All Events", color = Slate200, fontSize = 12.sp) },
                    onClick = {
                        onSelectEvent(null)
                        expanded = false
                    }
                )
                events.forEach { ev ->
                    DropdownMenuItem(
                        text = { Text("${ev.icon ?: ""} ${ev.title} (ID: ${ev.id})", color = Slate200, fontSize = 12.sp) },
                        onClick = {
                            onSelectEvent(ev.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Registration Row Card with delete/cancel action (Stretch Goal 2).
 */
@Composable
private fun AdminRegistrationRowCard(
    registration: RegistrationItem,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
            .border(1.dp, Slate800, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(registration.studentName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.background(Slate800, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                        Text(registration.rollNumber, color = Purple300, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(registration.email, color = Slate300, fontSize = 11.sp)
                if (!registration.phone.isNullOrBlank()) {
                    Text("Phone: ${registration.phone}", color = Slate400, fontSize = 11.sp)
                }
                Text("Event: ${registration.eventTitle}", color = Purple400, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("Registered: ${registration.registeredAt}", color = Slate500, fontSize = 10.sp)
            }

            // Cancel Registration Button (Stretch Goal 2)
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(32.dp)
                    .background(Red500.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Cancel Registration",
                    tint = Red400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
