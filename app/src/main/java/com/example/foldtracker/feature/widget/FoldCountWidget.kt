package com.example.foldtracker.feature.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
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
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.foldtracker.MainActivity
import com.example.foldtracker.R
import com.example.foldtracker.core.di.dataStore
import com.example.foldtracker.repository.CounterRepositoryImpl
import com.example.foldtracker.repository.datastore.DataStoreKeys
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

/**
 * Data class representing fold counts
 */
data class FoldsState(
    val totalCount: Int = 0,
    val todayCount: Int = 0
)

/**
 * Widget implementation for Glance-based widgets
 */
class FoldCountGlanceWidget : GlanceAppWidget() {

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val preferences = context.dataStore.data.first()
        val today = LocalDate.now().toString()
        val dailyCount = preferences[DataStoreKeys.dailyCountKey(today)] ?: 0
        val totalCount = preferences[DataStoreKeys.COUNTER_KEY] ?: 0

        provideContent {
            Column(
                modifier = GlanceModifier
                    .wrapContentSize()
                    .background(ColorProvider(R.color.light_gray))
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
                    Text(
                        text = "📊 Fold Stats",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(R.color.dark_gray)
                        ),
                        modifier = GlanceModifier.padding(end = 8.dp)
                    )

                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh),
                        contentDescription = "Refresh",
                        modifier = GlanceModifier
                            .size(24.dp)
                            .clickable(actionRunCallback<WidgetRefreshAction>())
                    )
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                Box(
                    modifier = GlanceModifier
                        .wrapContentSize()
                        .background(ColorProvider(R.color.card_white))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                Box(
                    modifier = GlanceModifier
                        .wrapContentSize()
                        .background(ColorProvider(R.color.card_white))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
}

/**
 * Traditional AppWidgetProvider implementation for RemoteViews-based widgets
 */
class FoldCountWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Create a FoldsState object with the latest data
        var totalCount = 0
        var todayCount = 0
        
        // Use blocking call to get data - not ideal but works for widgets
        runBlocking {
            val today = LocalDate.now().toString()
            val preferences = context.dataStore.data.first()
            totalCount = preferences[DataStoreKeys.COUNTER_KEY] ?: 0
            todayCount = preferences[DataStoreKeys.dailyCountKey(today)] ?: 0
        }
        
        val foldsState = FoldsState(totalCount, todayCount)
        
        // Create pending intent for launching main activity
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Set up refresh action
        val refreshIntent = Intent(context, FoldCountWidgetReceiver::class.java).apply {
            action = WIDGET_REFRESH_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create RemoteViews
        val views = RemoteViews(context.packageName, R.layout.fold_count_widget)
        
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
        
        // Update the widget data
        updateWidgetData(views, foldsState)
        
        // Instruct the widget manager to update the widget immediately
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun updateWidgetData(views: RemoteViews, foldsState: FoldsState) {
        views.setTextViewText(R.id.fold_count, foldsState.totalCount.toString())
        views.setTextViewText(R.id.today_count, foldsState.todayCount.toString())
        
        // Update time text with current time
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        views.setTextViewText(R.id.last_updated_time, "Last updated: $currentTime")
    }

    companion object {
        const val WIDGET_REFRESH_ACTION = "com.example.foldtracker.WIDGET_REFRESH"
        
        /**
         * Triggers an immediate update of all FoldCountWidget instances
         */
        @OptIn(DelicateCoroutinesApi::class)
        fun refreshWidgets(context: Context) {
            // This kicks off an immediate update of the widget
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, FoldCountWidget::class.java)
            )
            
            // Force update without any delay
            val widget = FoldCountWidget()
            widget.onUpdate(context, appWidgetManager, appWidgetIds)
            
            // Send broadcast to notify of the update
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }
}

