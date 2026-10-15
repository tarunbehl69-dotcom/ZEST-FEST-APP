package com.zenith26.fest.ui.common

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.remote.ApiClient
import com.zenith26.fest.ui.theme.Indigo400
import com.zenith26.fest.ui.theme.Indigo500
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.TextWhite

/**
 * Top navigation bar matching the Zenith '26 web header.
 * Displays fest logo, title, mode badge, and portal switch button.
 */
@Composable
fun TopNavBar(
    isAdminScreen: Boolean,
    onNavigateToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHostDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate950.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = Slate800.copy(alpha = 0.8f),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(Purple600, Indigo500)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 18.sp
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ZENITH '26",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )

                        // Mode Badge
                        val badgeText = if (isAdminScreen) "Admin Portal" else "Annual Fest"
                        val badgeBg = if (isAdminScreen) Indigo500.copy(alpha = 0.15f) else Purple500.copy(alpha = 0.15f)
                        val badgeColor = if (isAdminScreen) Indigo400 else Purple300
                        val badgeBorder = if (isAdminScreen) Indigo500.copy(alpha = 0.3f) else Purple500.copy(alpha = 0.3f)

                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(6.dp))
                                .border(1.dp, badgeBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText.uppercase(),
                                color = badgeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Text(
                        text = if (isAdminScreen) "Fest Operations & Real-Time Monitoring" else "College Fest Management",
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
            }

            // Right side: Network Settings & Navigation Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Host Settings Icon
                IconButton(
                    onClick = { showHostDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Server Settings",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Switch View Button
                Box(
                    modifier = Modifier
                        .background(Slate800, RoundedCornerShape(10.dp))
                        .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                        .clickable { onNavigateToggle() }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = if (isAdminScreen) "Student View" else "Admin Dashboard",
                            color = if (isAdminScreen) Slate200 else Purple300,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = if (isAdminScreen) Slate300 else Purple300,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }

    if (showHostDialog) {
        HostConfigDialog(
            onDismiss = { showHostDialog = false }
        )
    }
}

/**
 * Dialog enabling developer/user to configure backend base URL.
 * Defaults to 10.0.2.2:8001 for Android Emulator, with options for localhost or LAN IP.
 */
@Composable
fun HostConfigDialog(
    onDismiss: () -> Unit
) {
    var urlText by remember { mutableStateOf(ApiClient.getBaseUrl()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = "Backend Server Settings",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Specify the FastAPI backend URL. Use 10.0.2.2 for Android Emulator, or your local Wi-Fi IP (e.g. 192.168.1.50:8001) for a physical phone.",
                    color = Slate300,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("Base URL", color = Slate400, fontSize = 12.sp) },
                    singleLine = true,
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

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { urlText = "https://fruit-colleague-brooklyn-threatening.trycloudflare.com/" },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple600.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🌐 Cloud Tunnel (Appetize.io / Web)", fontSize = 11.sp, color = Purple300)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { urlText = "http://10.0.2.2:8001/" },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("10.0.2.2 (Local)", fontSize = 10.sp, color = Slate300)
                        }
                        Button(
                            onClick = { urlText = "http://localhost:8001/" },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("localhost", fontSize = 10.sp, color = Slate300)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    ApiClient.setBaseUrl(urlText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save & Connect", color = TextWhite, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Slate400, fontSize = 12.sp)
            }
        }
    )
}
