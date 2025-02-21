package com.example.foldtracker.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foldtracker.viewmodel.CounterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val context = LocalContext.current
    val counter by viewModel.counter.collectAsState()
    val dailyFolds by viewModel.dailyFolds.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

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
            CounterStats(counter, dailyFolds)
            ProgressBar(progress)
            AchievementSection(achievements)
            ActionButtons(counter, viewModel, context) { showDialog = true }
        }
    }
    if (showDialog) ResetConfirmationDialog(onConfirm = {
        viewModel.resetCounter(context)
        showDialog = false
    }, onDismiss = { showDialog = false })
}

@Composable
fun gradientBackground(): Brush = Brush.verticalGradient(
    colors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
    )
)

@Composable
fun CounterStats(counter: Int, dailyFolds: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        CounterCard("Total Folds", counter)
        CounterCard("Today Folds", dailyFolds)
    }
}

@Composable
fun CounterCard(label: String, count: Int) {
    AnimatedContent(
        targetState = count,
        transitionSpec = {
            (slideInVertically { height -> -height } + fadeIn() + scaleIn(initialScale = 0.8f))
                .togetherWith(slideOutVertically
                { height -> height } + fadeOut() + scaleOut(targetScale = 1.2f))
                .using(SizeTransform(clip = false))
        }
    ) { targetCount ->
        Card(
            modifier = Modifier
                .padding(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$targetCount",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
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
    LinearProgressIndicator(
        progress = progress.coerceIn(0f, 1f),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(top = 16.dp),
        color = MaterialTheme.colorScheme.primary
    )
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
    counter: Int,
    viewModel: CounterViewModel,
    context: Context,
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
            Text("Reset Counter")
        }

        Button(
            onClick = {
                val shareText = "I've opened my foldable device $counter times! Can you beat that?"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, "Share your fold count"))
            }
        ) {
            Text("Share")
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
