package com.example.foldtracker.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.foldtracker.datastore.DataStoreKeys.COUNTER_KEY
import com.example.foldtracker.datastore.DataStoreKeys.DAILY_COUNT_KEY
import com.example.foldtracker.di.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FoldCountWidget : GlanceAppWidget() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dailyCount = getDailyCount(context)
        val totalCount = getTotalCount(context)

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier.fillMaxSize()
                        .background(Color.DarkGray)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fold Counts",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(text = "Daily: $dailyCount")
                    Text(text = "Total: $totalCount")
                }
            }
        }
    }

    private fun getDailyCount(context: Context): Int {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            preferences[DAILY_COUNT_KEY]?.toIntOrNull() ?: 0
        }
    }

    private fun getTotalCount(context: Context): Int {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            preferences[COUNTER_KEY] ?: 0
        }
    }

    companion object {
        suspend fun updateWidget(context: Context) {
            FoldCountWidget().updateAll(context)
        }
    }
}

