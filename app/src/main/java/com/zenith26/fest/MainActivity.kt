package com.zenith26.fest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zenith26.fest.ui.admin.AdminDashboardScreen
import com.zenith26.fest.ui.common.TopNavBar
import com.zenith26.fest.ui.student.StudentPortalScreen
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.ZenithTheme
import com.zenith26.fest.ui.viewmodel.AdminViewModel
import com.zenith26.fest.ui.viewmodel.StudentViewModel

/**
 * Main Activity for Zenith '26 College Fest Management System Native Android App.
 * Orchestrates navigation between Student Event Portal and Admin Dashboard.
 */
class MainActivity : ComponentActivity() {

    private val studentViewModel: StudentViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZenithTheme {
                var isAdminScreen by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopNavBar(
                            isAdminScreen = isAdminScreen,
                            onNavigateToggle = {
                                isAdminScreen = !isAdminScreen
                                if (isAdminScreen) {
                                    adminViewModel.refreshAll()
                                } else {
                                    studentViewModel.fetchEvents()
                                }
                            }
                        )
                    },
                    containerColor = Slate950
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Slate950)
                    ) {
                        if (isAdminScreen) {
                            AdminDashboardScreen(
                                viewModel = adminViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            StudentPortalScreen(
                                viewModel = studentViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
