package com.example.foldtracker.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
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
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.foldtracker.datastore.DataStoreKeys
import com.example.foldtracker.di.dataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class FoldCountWidget : GlanceAppWidget() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Fetch data from DataStore
        val preferences = context.dataStore.data.first()
        val today = LocalDate.now().toString()
        val dailyCount = preferences[DataStoreKeys.dailyCountKey(today)] ?: 0
        val totalCount = preferences[DataStoreKeys.COUNTER_KEY] ?: 0

        // Provide the UI content
        provideContent {
            GlanceTheme {
                WidgetContent(dailyCount = dailyCount, totalCount = totalCount)
            }
        }
    }

    @Composable
    private fun WidgetContent(dailyCount: Int, totalCount: Int) {
        Box(
            modifier = GlanceModifier
                .size(170.dp)
                .background(ColorProvider(Color(0xFFE3F2FD)))
        ) {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(
                        colorProvider = ColorProvider(Color.LightGray.copy(alpha = 0.9f)) )
                    .padding(1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "📊 Fold Stats",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color(0xFF6A11CB))
                    )
                )

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Daily Count
                Text(
                    text = "📅 Today's Folds",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color.Black)
                    )
                )
                Text(
                    text = dailyCount.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color(0xFF2575FC))
                    )
                )

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Total Count
                Text(
                    text = "📈 Total Folds",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color.Black)
                    )
                )
                Text(
                    text = totalCount.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color(0xFF2575FC))
                    )
                )
            }
        }
    }

    companion object {
        suspend fun updateWidget(context: Context) {
            // Update all instances of the widget
            FoldCountWidget().updateAll(context)
        }
    }
}
