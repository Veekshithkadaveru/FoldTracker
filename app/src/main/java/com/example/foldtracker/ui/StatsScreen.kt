package com.example.foldtracker.ui

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.foldtracker.viewmodel.CounterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen(viewModel: CounterViewModel) {
    val averageFolds by viewModel.averageFolds.collectAsState()
    val yearlyProjection by viewModel.yearlyProjection.collectAsState()
    val hingeAngle by viewModel.hingeAngle.collectAsState()

    var isDailyLimitCardExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
            .padding(16.dp)
            .pointerInput(isDailyLimitCardExpanded) {
                detectTapGestures { offset ->
                    if (isDailyLimitCardExpanded) {
                        isDailyLimitCardExpanded = false
                    }
                }
            },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Statistics",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.outlineVariant,
            fontWeight = FontWeight.Bold
        )
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
