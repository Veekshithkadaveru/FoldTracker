package com.example.foldtracker.feature.counter.ui

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.foldtracker.feature.counter.CounterViewModel
import kotlin.math.roundToInt

@Composable
fun DailyLimitCard(
    viewModel: CounterViewModel,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    val dailyLimit by viewModel.dailyLimit.collectAsState()

    var sliderValue by remember { mutableStateOf(dailyLimit.toFloat()) }

    val context = LocalContext.current

    val message = "Daily Limit is set to ${sliderValue.toInt()}"

    // Adjust card size based on expanded state
    val cardWidth = if (isExpanded) 300.dp else 150.dp
    val cardHeight = if (isExpanded) 240.dp else 120.dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(8.dp)
            .clickable { onExpandChange(!isExpanded) }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Daily Limit",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isExpanded) "${sliderValue.toInt()}" else "$dailyLimit",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = (it/10).roundToInt()*10f },
                        valueRange = 10f..500f,
                        steps = 49,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.updateDailyLimit(sliderValue.toInt())
                        onExpandChange(false)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }) {
                        Text("Set Limit")
                    }
                }
            }
            
            // Edit icon in the top right corner
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Daily Limit",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}