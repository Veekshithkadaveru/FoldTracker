package com.example.foldtracker.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.foldtracker.viewmodel.CounterViewModel

// Function to check if notification permission is granted
fun isNotificationPermissionGranted(context: Context): Boolean {
    // For Android 13 (API 33) and above, check for POST_NOTIFICATIONS permission
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        // For older versions, permission is granted at install time
        true
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel, navController: NavController) {
    val context = LocalContext.current
    val counter by viewModel.counter.collectAsState()
    val dailyFolds by viewModel.dailyFolds.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    var showResetConfirmationDialog by remember { mutableStateOf(false) }

    // Check if notification permission is needed and not granted
    val permissionNeeded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !isNotificationPermissionGranted(context)

    // Permission request launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("Notification granted? ", "$isGranted")
        // Mark that we've requested permission
        viewModel.setNotificationPermissionRequested(true)
        // No additional action needed if permission is denied since notifications are optional
    }

    // Check permission on first composition
    LaunchedEffect(key1 = true) {
        if (permissionNeeded) {
            // Check if we've already requested permission before
            val alreadyRequested = viewModel.isNotificationPermissionRequested()
            if (!alreadyRequested) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val nextMilestone = if (counter == 0) 50 else ((counter / 50) + 1) * 50
    val progress = if (counter == 0) 0f else counter / nextMilestone.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CounterStats(counter, dailyFolds, navController)
            ProgressBar(progress)
            AchievementSection(achievements)
            ActionButtons { showResetConfirmationDialog = true }
        }
    }

    // Reset confirmation dialog
    if (showResetConfirmationDialog) {
        ResetConfirmationDialog(
            onConfirm = {
                viewModel.resetCounter(context)
                showResetConfirmationDialog = false
            },
            onDismiss = { showResetConfirmationDialog = false }
        )
    }
}

@Composable
fun gradientBackground(): Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)
    )
)

@Composable
fun CounterStats(
    counter: Int,
    dailyFolds: Int,
    navController: NavController
) {

    var isDailyLimitCardExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->

                    if (isDailyLimitCardExpanded) {
                        isDailyLimitCardExpanded = false
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CounterCard("Total Folds", counter)
            CounterCard("Today Folds", dailyFolds)
        }
        MoreStatsButton(navController = navController)
    }
}

@Composable
fun CounterCard(label: String, count: Any) {
    // Fixed size for all cards
    val cardWidth = 150.dp
    val cardHeight = 120.dp

    AnimatedContent(
        targetState = count,
        transitionSpec = {
            (slideInVertically { height -> -height } + fadeIn() + scaleIn(initialScale = 0.8f))
                .togetherWith(
                    slideOutVertically
                    { height -> height } + fadeOut() + scaleOut(targetScale = 1.2f))
                .using(SizeTransform(clip = false))
        }
    ) { targetCount ->
        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .padding(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$targetCount",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                )
            }
        }
    }
}


@Composable
fun ProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(top = 12.dp)
            .height(12.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Gray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.inversePrimary,
                            MaterialTheme.colorScheme.error
                        )
                    )
                )
                .animateContentSize()
        )
    }
}


@Composable
fun AchievementSection(achievements: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Achievements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (achievements.isEmpty()) {
                Text(
                    text = "No achievements yet. Keep folding!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                achievements.forEach { achievement ->
                    Text(
                        text = "✅ $achievement",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            onClick = onResetClick,
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
        ) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Reset")
            Spacer(modifier = Modifier.padding(4.dp))
            Text("Reset Counter")
        }
    }
}

@Composable
fun ResetConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Reset Counter?") },
        text = { Text("Are you sure you want to reset all your folds? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun MoreStatsButton(navController: NavController, modifier: Modifier = Modifier) {
    var buttonScale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = buttonScale,
        animationSpec = tween(durationMillis = 100)
    )

    ElevatedButton(
        onClick = { navController.navigate("stats_screen") },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        )
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = "Stats",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "More Stats",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
