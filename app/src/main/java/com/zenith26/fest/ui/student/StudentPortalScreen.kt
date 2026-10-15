package com.zenith26.fest.ui.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.common.CategoryBadge
import com.zenith26.fest.ui.common.GradientText
import com.zenith26.fest.ui.common.StatusBadge
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Blue400
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Indigo400
import com.zenith26.fest.ui.theme.Indigo500
import com.zenith26.fest.ui.theme.Indigo600
import com.zenith26.fest.ui.theme.Pink400
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Red400
import com.zenith26.fest.ui.theme.Red500
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.TextWhite
import com.zenith26.fest.ui.theme.Typography
import com.zenith26.fest.ui.viewmodel.StudentViewModel

/**
 * Student Event Portal Screen.
 * Contains Hero Section, Search & Category Filters, and Dynamic Events Cards.
 */
@Composable
fun StudentPortalScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Hero Section (Step 5)
        item {
            HeroSection(isOfflineMode = state.isOfflineMode)
        }

        // 2. Search & Category Filters (Step 6)
        item {
            FilterAndSearchSection(
                selectedCategory = state.selectedCategory,
                searchQuery = state.searchQuery,
                onCategorySelect = { viewModel.selectCategory(it) },
                onSearchChange = { viewModel.onSearchQueryChange(it) },
                onClearSearch = { viewModel.clearSearch() }
            )
        }

        // 3. Results Count / Filter Indicator
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isLoading) "Loading events..." else "Showing ${state.events.size} event${if (state.events.size == 1) "" else "s"}",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (state.selectedCategory != "All" || state.searchQuery.isNotBlank()) {
                    Text(
                        text = "Filtered view",
                        color = Purple400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 4. Loading State (Step 9)
        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Purple500,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Fetching events from server...",
                            color = Slate400,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 5. Error State (Step 9)
        if (!state.isLoading && state.errorMessage != null) {
            item {
                ErrorCard(
                    message = state.errorMessage!!,
                    onRetry = { viewModel.fetchEvents() }
                )
            }
        }

        // 6. Empty State (Step 9)
        if (!state.isLoading && state.errorMessage == null && state.events.isEmpty()) {
            item {
                EmptyStateCard(
                    category = state.selectedCategory,
                    search = state.searchQuery,
                    onReset = { viewModel.resetFilters() }
                )
            }
        }

        // 7. Event Cards Grid/List (Step 6 & 11 Fade-in)
        if (!state.isLoading && state.errorMessage == null && state.events.isNotEmpty()) {
            itemsIndexed(state.events, key = { _, ev -> ev.id }) { index, event ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(durationMillis = 350, delayMillis = (index * 40).coerceAtMost(400)))
                ) {
                    EventCard(
                        event = event,
                        onViewDetails = { viewModel.openEventDetails(event) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    // Event Detail & Registration Dialog
    if (state.selectedEvent != null) {
        EventDetailModal(
            event = state.selectedEvent!!,
            isRegistering = state.isRegistering,
            successMessage = state.registrationSuccessMessage,
            errorMessage = state.registrationErrorMessage,
            onRegister = { name, roll, email, phone ->
                viewModel.registerForEvent(state.selectedEvent!!.id, name, roll, email, phone)
            },
            onDismiss = { viewModel.closeEventDetails() }
        )
    }
}

/**
 * Hero Section reproducing web layout and gradients.
 */
@Composable
private fun HeroSection(isOfflineMode: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Indigo600.copy(alpha = 0.15f),
                        Slate950,
                        Slate950
                    )
                )
            )
            .padding(horizontal = 18.dp, vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Live / Demo pill banner
            Box(
                modifier = Modifier
                    .background(
                        if (isOfflineMode) Amber400.copy(alpha = 0.12f) else Purple500.copy(alpha = 0.12f),
                        RoundedCornerShape(100.dp)
                    )
                    .border(
                        1.dp,
                        if (isOfflineMode) Amber400.copy(alpha = 0.3f) else Purple500.copy(alpha = 0.25f),
                        RoundedCornerShape(100.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isOfflineMode) Amber400 else Emerald400, CircleShape)
                    )
                    Text(
                        text = if (isOfflineMode) "Offline Demo Mode • Nov 14–15, 2026" else "Registrations Live • Nov 14–15, 2026",
                        color = if (isOfflineMode) Amber400 else Purple300,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Main Title
            Text(
                text = "Defy Gravity at",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            GradientText(
                text = "Zenith '26",
                style = Typography.headlineLarge.copy(fontSize = 32.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Tagline
            Text(
                text = "Two intense days of competitive coding, cultural showcases, esports clashes, and masterclasses. Secure your spot before capacities max out!",
                color = Slate300,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeroStatBadge(count = "14", label = "Events", countColor = Purple400)
                HeroStatBadge(count = "5", label = "Categories", countColor = Pink400)
                HeroStatBadge(count = "100%", label = "Instant Conf.", countColor = Indigo400)
            }
        }
    }
}

@Composable
private fun HeroStatBadge(count: String, label: String, countColor: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .background(Slate900.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = count, color = countColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = Slate400, fontSize = 11.sp)
        }
    }
}

/**
 * Filter Chips and Search Bar.
 */
@Composable
private fun FilterAndSearchSection(
    selectedCategory: String,
    searchQuery: String,
    onCategorySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    val categories = listOf(
        "All" to "All Events",
        "Technical" to "💻 Technical",
        "Cultural" to "🎭 Cultural",
        "Sports" to "⚽ Sports",
        "Gaming" to "🎮 Gaming",
        "Workshop" to "🚀 Workshop"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Horizontal scrollable Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (catKey, label) ->
                val isSelected = selectedCategory == catKey
                val bg = if (isSelected) Purple600 else Slate900.copy(alpha = 0.8f)
                val border = if (isSelected) Purple500 else Slate800
                val textColor = if (isSelected) TextWhite else Slate300

                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(12.dp))
                        .border(1.dp, border, RoundedCornerShape(12.dp))
                        .clickable { onCategorySelect(catKey) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by event title...", color = Slate400, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Slate400, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple500,
                unfocusedBorderColor = Slate800,
                focusedContainerColor = Slate900.copy(alpha = 0.8f),
                unfocusedContainerColor = Slate900.copy(alpha = 0.8f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = Slate200
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Event Card component matching the Tailwind rounded cards.
 */
@Composable
private fun EventCard(
    event: Event,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registered = event.registeredCount
    val capacity = event.capacity
    val capacityPct = if (capacity > 0) (registered.toFloat() / capacity.toFloat()) else 0f
    val isFull = event.isFull
    val isHighDemand = event.utilizationPct >= 90.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900.copy(alpha = 0.9f), RoundedCornerShape(18.dp))
            .border(1.dp, Slate800, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top Row: Icon, Category Badge & Capacity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Slate800, RoundedCornerShape(12.dp))
                        .border(1.dp, Slate700.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = event.icon ?: "⚡", fontSize = 22.sp)
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CategoryBadge(category = event.category)
                    StatusBadge(
                        isFull = isFull,
                        isHighDemand = isHighDemand,
                        availableSeats = event.availableSeats
                    )
                }
            }

            // Title
            Text(
                text = event.title,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Description preview (2 lines)
            Text(
                text = event.description ?: "Join us for this exciting fest event!",
                color = Slate400,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Metadata items (Date/Time, Venue)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Purple400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${event.eventDate} • ${event.eventTime}",
                        color = Slate300,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.venue,
                        color = Slate300,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Seat Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                LinearProgressIndicator(
                    progress = { capacityPct.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = if (isFull) Red500 else if (isHighDemand) Amber400 else Purple500,
                    trackColor = Slate800,
                )
            }

            // Bottom row: Fee and View Details Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.formattedFee,
                    color = if (event.fee == 0) Emerald400 else Slate200,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .background(Slate800, RoundedCornerShape(10.dp))
                        .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                        .clickable { onViewDetails() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "View Details",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Purple300,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Error state card with retry button.
 */
@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Red500.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .border(1.dp, Red500.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Red400, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Failed to Load Events", color = Red400, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(message, color = Slate300, fontSize = 12.sp, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Red500),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Retry Connection", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Empty state card when search/filter returns zero events.
 */
@Composable
private fun EmptyStateCard(
    category: String,
    search: String,
    onReset: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Slate900.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "🔍", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text("No events match your search", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "We couldn't find any events matching category \"$category\" ${if (search.isNotBlank()) "and term \"$search\"" else ""}.",
                color = Slate400,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Reset Filters", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
