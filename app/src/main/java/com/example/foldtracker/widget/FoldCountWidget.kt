package com.example.foldtracker.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.foldtracker.R
import com.example.foldtracker.datastore.DataStoreKeys
import com.example.foldtracker.di.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class FoldCountWidget : GlanceAppWidget() {

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val preferences = runBlocking { context.dataStore.data.first() }
        val today = LocalDate.now().toString()
        val dailyCount = preferences[DataStoreKeys.dailyCountKey(today)] ?: 0
        val totalCount = preferences[DataStoreKeys.COUNTER_KEY] ?: 0

        provideContent {
            Column(
                modifier = GlanceModifier
                    .wrapContentSize()
                    .background(ColorProvider(R.color.light_gray)) // ✅ Use color resources
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "📊 Fold Stats",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(R.color.dark_gray) // ✅ Use color resource
                    )
                )

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Daily Folds Card
                Box(
                    modifier = GlanceModifier
                        .wrapContentSize()
                        .background(ColorProvider(R.color.card_white)) // ✅ Use color resource
                        .padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📅 Today's Folds",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(R.color.medium_gray)
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "$dailyCount",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(R.color.blue)
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Total Folds Card
                Box(
                    modifier = GlanceModifier
                        .wrapContentSize()
                        .background(ColorProvider(R.color.card_white)) // ✅ Use color resource
                        .padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📈 Total Folds",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(R.color.medium_gray)
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "$totalCount",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(R.color.purple)
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Call this function to update all instances of the widget.
         */
        suspend fun updateWidget(context: Context) {
            FoldCountWidget().updateAll(context)
        }
    }
}
