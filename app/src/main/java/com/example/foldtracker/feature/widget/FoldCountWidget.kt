package com.example.foldtracker.feature.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.foldtracker.MainActivity
import com.example.foldtracker.R
import com.example.foldtracker.core.di.dataStore
import com.example.foldtracker.repository.datastore.DataStoreKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.Executors

// Add a TAG for logging
private const val TAG = "FoldCountWidget"

class FoldCountWidget : GlanceAppWidget() {

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Log that we're providing content
        Log.d(TAG, "provideGlance called - fetching fresh data")
        
        // Force data refresh on each update
        val preferences = context.dataStore.data.first()
        val today = LocalDate.now().toString()
        val dailyCount = preferences[DataStoreKeys.dailyCountKey(today)] ?: 0
        val totalCount = preferences[DataStoreKeys.COUNTER_KEY] ?: 0
        
        Log.d(TAG, "Data fetched - daily: $dailyCount, total: $totalCount")

        provideContent {
            Column(
                modifier = GlanceModifier
                    .height(204.dp)
                    .width(172.dp)
                    .background(ColorProvider(R.color.light_gray))
                    .cornerRadius(24.dp)
                    .clickable(actionStartActivity<MainActivity>())
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Image(
                        provider = ImageProvider(R.mipmap.ic_launcher_round),
                        contentDescription = "App Logo",
                        modifier = GlanceModifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )

                    Text(
                        text = "Fold Tracker",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(R.color.medium_gray)
                        )
                    )

                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh),
                        contentDescription = "Refresh",
                        modifier = GlanceModifier
                            .size(24.dp)
                            .padding(start = 8.dp)
                            .clickable(
                                // Use direct action for immediate refresh
                                actionRunCallback<WidgetRefreshCallback>()
                            )
                    )
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                Box(
                    modifier = GlanceModifier
                        .height(60.dp)
                        .width(120.dp)
                        .background(ColorProvider(R.color.card_white))
                        .cornerRadius(12.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📅 Today's Folds",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(R.color.medium_gray)
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "$dailyCount",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(R.color.blue)
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                Box(
                    modifier = GlanceModifier
                        .height(60.dp)
                        .width(120.dp)
                        .background(ColorProvider(R.color.card_white))
                        .cornerRadius(12.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📈 Total Folds",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(R.color.medium_gray)
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "$totalCount",
                            style = TextStyle(
                                fontSize = 18.sp,
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
        suspend fun updateWidget(context: Context) {
            Log.d(TAG, "updateWidget called")
            
            try {
                // Update using Glance API
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(FoldCountWidget::class.java)
                Log.d(TAG, "Found ${glanceIds.size} Glance widgets to update")
                
                // Update all instances of the widget immediately
                for (glanceId in glanceIds) {
                    try {
                        // Force content refresh with proper key
                        updateAppWidgetState(context, glanceId) { prefs ->
                            prefs.toMutablePreferences().apply {
                                this[DataStoreKeys.WIDGET_REFRESH_TIMESTAMP_KEY] = System.currentTimeMillis()
                            }
                        }
                        
                        // Update the widget UI
                        FoldCountWidget().update(context, glanceId)
                        Log.d(TAG, "Updated Glance widget: $glanceId")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating Glance widget: ${e.message}")
                    }
                }
                
                // Also use traditional AppWidgetManager for fallback
                withContext(Dispatchers.Main.immediate) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, FoldCountWidgetReceiver::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                    
                    if (appWidgetIds.isNotEmpty()) {
                        Log.d(TAG, "Also updating ${appWidgetIds.size} widgets via AppWidgetManager")
                        // Force update on main thread
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.widget_frame)
                        
                        // Use an executor for immediate background execution
                        Executors.newSingleThreadExecutor().execute {
                            appWidgetManager.updateAppWidget(componentName, null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in updateWidget: ${e.message}")
            }
        }
    }
}

