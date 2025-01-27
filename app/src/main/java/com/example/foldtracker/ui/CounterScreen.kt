package com.example.foldtracker.ui

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foldtracker.viewmodel.CounterViewModel

@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val counter by viewModel.counter.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val nextMilestone = if (counter == 0) 50 else ((counter / 50) + 1) * 50
    val progress = if (counter == 0) 0f else counter / nextMilestone.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedContent(targetState = counter) { targetCounter ->
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "$targetCounter times folded",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f), // Ensure progress is between 0 and 1
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Achievements:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            achievements.forEach { achievement ->
                Text(
                    text = "\u2022 $achievement",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp)
                )
            }

            // Reset and toggle buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { viewModel.resetCounter() }) {
                    Text("Reset Counter")
                }

            }


            val context = LocalContext.current
            Button(
                onClick = {
                    val shareText = "I've opened my foldable device $counter times! Can you beat that?"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share your fold count"))
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Share")
            }
        }
    }
}
