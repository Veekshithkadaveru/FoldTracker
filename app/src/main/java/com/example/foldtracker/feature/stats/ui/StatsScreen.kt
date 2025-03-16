package com.example.foldtracker.feature.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foldtracker.core.ui.CounterCard
import com.example.foldtracker.core.ui.gradientBackground
import com.example.foldtracker.feature.counter.CounterViewModel
import com.example.foldtracker.feature.counter.ui.DailyLimitCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: CounterViewModel, navController: NavController) {
    val averageFolds by viewModel.averageFolds.collectAsState()
    val yearlyProjection by viewModel.yearlyProjection.collectAsState()
    val hingeAngle by viewModel.hingeAngle.collectAsState()

    var isDailyLimitCardExpanded by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showOverflowMenu = false
                                showAboutDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Help") },
                            onClick = {
                                showOverflowMenu = false
                                showHelpDialog = true
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground())
                .padding(paddingValues)
                .padding(16.dp)
                .pointerInput(isDailyLimitCardExpanded) {
                    detectTapGestures { _ ->
                        if (isDailyLimitCardExpanded) {
                            isDailyLimitCardExpanded = false
                        }
                    }
                },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CounterCard("Avg. Weekly Folds", String.format("%.2f", averageFolds))
                CounterCard("Hinge Angle", "$hingeAngle°")
            }
            Spacer(modifier = Modifier.height(16.dp))
            CounterCard("Yearly Projection", String.format("%d", yearlyProjection))
            Spacer(modifier = Modifier.height(16.dp))
            DailyLimitCard(viewModel = viewModel, isExpanded = isDailyLimitCardExpanded) {
                isDailyLimitCardExpanded = !isDailyLimitCardExpanded
            }
        }
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About FoldTracker") },
            text = {
                Column {
                    Text("FoldTracker v1.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("An application to track and analyze your device folding habits.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Developed by: FoldTracker Team")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help") },
            text = {
                Column {
                    Text("Statistics Screen Guide:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Avg. Weekly Folds: The average number of times you fold your device per week.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Hinge Angle: The current angle of your device's hinge.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Yearly Projection: Estimated number of folds for the entire year based on your current usage.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Daily Limit: Set and track your daily folding limit to prevent overuse.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
