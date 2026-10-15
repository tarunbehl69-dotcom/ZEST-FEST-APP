package com.zenith26.fest.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Red300
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

/**
 * Add Event Modal Dialog for Admin (Stretch Goal 1).
 */
@Composable
fun AddEventModal(
    isSubmitting: Boolean,
    errorMessage: String?,
    onSave: (title: String, category: String, desc: String, date: String, time: String, venue: String, capacity: Int, fee: Int, icon: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technical") }
    var icon by remember { mutableStateOf("⚡") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-11-14") }
    var time by remember { mutableStateOf("10:00 AM") }
    var venue by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("50") }
    var feeText by remember { mutableStateOf("0") }

    var categoryDropdownOpen by remember { mutableStateOf(false) }
    val categories = listOf("Technical", "Cultural", "Sports", "Gaming", "Workshop")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(Slate900, RoundedCornerShape(20.dp))
                .border(1.dp, Slate800, RoundedCornerShape(20.dp)),
            color = Slate900,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Add New Fest Event", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("Admin CRUD operation (Stretch Goal)", color = Slate400, fontSize = 11.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400, modifier = Modifier.size(20.dp))
                    }
                }

                HorizontalDivider(color = Slate800)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Red500.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, Red500.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = Red400, modifier = Modifier.size(16.dp))
                                Text(errorMessage, color = Red300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Event Title *", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Hackathon Deluxe", fontSize = 12.sp, color = Slate400) },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category selector
                        Box(modifier = Modifier.weight(2f)) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category *", fontSize = 12.sp) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { categoryDropdownOpen = true }
                            )
                            DropdownMenu(
                                expanded = categoryDropdownOpen,
                                onDismissRequest = { categoryDropdownOpen = false },
                                modifier = Modifier.background(Slate900)
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = Slate200, fontSize = 13.sp) },
                                        onClick = {
                                            category = cat
                                            categoryDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        // Icon emoji
                        OutlinedTextField(
                            value = icon,
                            onValueChange = { icon = it },
                            label = { Text("Icon (Emoji)", fontSize = 12.sp) },
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description", fontSize = 12.sp) },
                        placeholder = { Text("Brief event summary...", fontSize = 12.sp, color = Slate400) },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (ISO) *", fontSize = 12.sp) },
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Time *", fontSize = 12.sp) },
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = capacityText,
                            onValueChange = { capacityText = it },
                            label = { Text("Capacity *", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = feeText,
                            onValueChange = { feeText = it },
                            label = { Text("Fee (₹)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Venue *", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Computer Lab 2", fontSize = 12.sp, color = Slate400) },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val cap = capacityText.toIntOrNull() ?: 50
                            val fee = feeText.toIntOrNull() ?: 0
                            onSave(title, category, desc, date, time, venue, cap, fee, icon)
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = TextWhite, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Create Event", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Purple500,
    unfocusedBorderColor = Slate700,
    focusedTextColor = TextWhite,
    unfocusedTextColor = Slate200,
    focusedContainerColor = Slate950,
    unfocusedContainerColor = Slate950
)
