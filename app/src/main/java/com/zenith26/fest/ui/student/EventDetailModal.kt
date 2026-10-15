package com.zenith26.fest.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.common.CategoryBadge
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Indigo600
import com.zenith26.fest.ui.theme.Purple300
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
import kotlinx.coroutines.delay

/**
 * Event Details & Registration Modal Dialog.
 * Replicates the original modal from index.html with metadata, description,
 * client-side checks, server-side error banners, and registration confirmation.
 */
@Composable
fun EventDetailModal(
    event: Event,
    isRegistering: Boolean,
    successMessage: String?,
    errorMessage: String?,
    onRegister: (name: String, roll: String, email: String, phone: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Auto-close dialog 2 seconds after successful registration
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(2000)
            onDismiss()
        }
    }

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
                // Modal Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Slate800, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = event.icon ?: "⚡", fontSize = 22.sp)
                        }

                        Column {
                            CategoryBadge(category = event.category)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.title,
                                color = TextWhite,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(color = Slate800)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Metadata Grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate950.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .border(1.dp, Slate800.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("DATE & TIME", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${event.eventDate} • ${event.eventTime}", color = Slate200, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("VENUE", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(event.venue, color = Slate200, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ENTRY FEE", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(event.formattedFee, color = if (event.fee == 0) Emerald400 else Slate200, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("AVAILABLE SEATS", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${event.availableSeats} / ${event.capacity}", color = Purple300, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // About the Event
                    Column {
                        Text(
                            text = "ABOUT THE EVENT",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = event.description ?: "No detailed description provided.",
                            color = Slate300,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }

                    // Capacity Alert Banner (if full)
                    if (event.isFull) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Red500.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .border(1.dp, Red500.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Red400,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "This event has reached full capacity. Registrations are closed.",
                                    color = Red300,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Registration Form
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "REGISTRATION FORM",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )

                            // Success banner
                            if (successMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Emerald500.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                        .border(1.dp, Emerald500.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald400, modifier = Modifier.size(18.dp))
                                        Text(successMessage, color = Emerald300, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Error banner
                            if (errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Red500.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                        .border(1.dp, Red500.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Error, contentDescription = null, tint = Red400, modifier = Modifier.size(18.dp))
                                        Text(errorMessage, color = Red300, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Input fields
                            OutlinedTextField(
                                value = studentName,
                                onValueChange = { studentName = it },
                                label = { Text("Student Name *", fontSize = 12.sp) },
                                placeholder = { Text("e.g. Alex Morgan", fontSize = 12.sp, color = Slate400) },
                                singleLine = true,
                                enabled = !isRegistering && successMessage == null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = Slate200,
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = rollNumber,
                                    onValueChange = { rollNumber = it },
                                    label = { Text("Roll Number *", fontSize = 12.sp) },
                                    placeholder = { Text("e.g. 23CS042", fontSize = 12.sp, color = Slate400) },
                                    singleLine = true,
                                    enabled = !isRegistering && successMessage == null,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Purple500,
                                        unfocusedBorderColor = Slate700,
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = Slate200,
                                        focusedContainerColor = Slate950,
                                        unfocusedContainerColor = Slate950
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone", fontSize = 12.sp) },
                                    placeholder = { Text("+91 98765...", fontSize = 12.sp, color = Slate400) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    enabled = !isRegistering && successMessage == null,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Purple500,
                                        unfocusedBorderColor = Slate700,
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = Slate200,
                                        focusedContainerColor = Slate950,
                                        unfocusedContainerColor = Slate950
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("College / Personal Email *", fontSize = 12.sp) },
                                placeholder = { Text("e.g. alex@college.edu", fontSize = 12.sp, color = Slate400) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                enabled = !isRegistering && successMessage == null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = Slate200,
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    onRegister(studentName, rollNumber, email, phone)
                                },
                                enabled = !isRegistering && successMessage == null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Purple600,
                                    disabledContainerColor = Slate800
                                )
                            ) {
                                if (isRegistering) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = TextWhite,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text("Registering...", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else if (successMessage != null) {
                                    Text("Registered!", color = Emerald400, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Confirm Registration", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
