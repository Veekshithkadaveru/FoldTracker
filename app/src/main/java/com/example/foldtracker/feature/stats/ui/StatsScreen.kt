package com.example.foldtracker.feature.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    
    val scrollState = rememberScrollState()

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground())
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures { 
                        if (isDailyLimitCardExpanded) {
                            isDailyLimitCardExpanded = false
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(60.dp)
            ) {
                // First row with two cards
                Row(
                    horizontalArrangement = Arrangement.spacedBy(48.dp)
                ) {
                    // Average Weekly Folds Card
                    CounterCard(
                        label = "Avg. Weekly Folds",
                        count = String.format("%.1f", averageFolds),
                        modifier = Modifier.size(165.dp, 140.dp)
                    )
                    
                    // Hinge Angle Card
                    CounterCard(
                        label = "Hinge Angle",
                        count = "$hingeAngle°",
                        modifier = Modifier.size(165.dp, 140.dp)
                    )
                }
                
                // Second row with two cards
                Row(
                    horizontalArrangement = Arrangement.spacedBy(48.dp)
                ) {
                    // Yearly Projection Card
                    CounterCard(
                        label = "Yearly Projection",
                        count = String.format("%d", yearlyProjection),
                        modifier = Modifier.size(165.dp, 140.dp)
                    )
                    
                    // Daily Limit Card (in the same size as others)
                    Box(
                        modifier = Modifier
                            .size(165.dp, 140.dp)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    isDailyLimitCardExpanded = true
                                }
                            }
                    ) {
                        if (!isDailyLimitCardExpanded) {
                            DailyLimitCard(
                                viewModel = viewModel,
                                isExpanded = false
                            ) {
                                isDailyLimitCardExpanded = true
                            }
                        }
                    }
                }
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // If daily limit card is expanded, show it on top
            if (isDailyLimitCardExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                        .pointerInput(Unit) {
                            detectTapGestures { 
                                isDailyLimitCardExpanded = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Stop propagation of clicks within the card
                    Box(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures { /* Consume click events */ }
                        }
                    ) {
                        DailyLimitCard(
                            viewModel = viewModel,
                            isExpanded = true
                        ) {
                            isDailyLimitCardExpanded = it
                        }
                    }
                }
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
